package name.nkid00.rcutil.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.Promise;
import name.nkid00.rcutil.Options;
import name.nkid00.rcutil.exception.ResponseException;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.helper.MapHelper;
import name.nkid00.rcutil.script.ScriptApi;
import net.minecraft.server.MinecraftServer;

public class ScriptServerIO {
    private static MultithreadEventLoopGroup group;
    private static InetAddress address;
    private static int port;
    private static ServerBootstrap bootstrap;
    private static Channel serverChannel;
    private static AtomicLong id = new AtomicLong(0);
    public static ConcurrentHashMap<String, ChannelHandlerContext> connections = new ConcurrentHashMap<>();
    public static MinecraftServer server;

    public static void init(MinecraftServer server) {
        address = null;
        port = Options.port();
        if (Options.localhostOnly()) {
            address = InetAddress.getLoopbackAddress();
            Log.info("Preparing script server on localhost:{}", port);
        } else {
            if (!Options.host().isEmpty()) {
                try {
                    address = InetAddress.getByName(Options.host());
                } catch (UnknownHostException e) {
                }
            }
            Log.info("Preparing script server on {}:{}", address == null ? "*" : address.getHostAddress(), port);
        }
        bootstrap = new ServerBootstrap();
        var threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("rcutilScriptServer#%d")
                .setDaemon(true)
                .build();
        if (Epoll.isAvailable()) {
            group = new EpollEventLoopGroup(0, threadFactory);
            bootstrap.channel(EpollServerSocketChannel.class);
        } else {
            group = new NioEventLoopGroup(0, threadFactory);
            bootstrap.channel(NioServerSocketChannel.class);
        }
        bootstrap.group(group);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN, 65535, 0, 2, 0, 2, true))
                        .addLast(new LengthFieldPrepender(ByteOrder.BIG_ENDIAN, 2, 0, false))
                        .addLast(new JsonCodec())
                        .addLast(new ScriptServerIOHandler());
            }
        });
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        ScriptServerIO.server = server;
    }

    public static void start(MinecraftServer server) {
        Log.info("Starting script server");
        var future = bootstrap.bind(address, port).awaitUninterruptibly();
        if (future.cause() != null) {
            Log.error("Error occurred while starting script server", future.cause());
            server.stop(false);
        } else {
            serverChannel = future.channel();
        }
    }

    public static void stop(MinecraftServer server) {
        if (serverChannel != null) {
            Log.info("Stopping script server");
            serverChannel.close().awaitUninterruptibly();
            serverChannel = null;
            group.shutdownGracefully().awaitUninterruptibly();
        }
    }

    private static String id() {
        return "s_%d".formatted(id.incrementAndGet());
    }

    public static JsonObject handleRequest(JsonObject request, String clientAddress) {
        var response = new JsonObject();
        var id = request.get("id").getAsString();
        try {
            response.add("result", ScriptApi.dispatch(
                    request.get("method").getAsString(),
                    request.get("params").getAsJsonObject(),
                    clientAddress, server));
        } catch (ResponseException e) {
            return e.toResponse(id);
        } catch (IllegalStateException | ClassCastException | NullPointerException e) {
            return ResponseException.INVALID_REQUEST.toResponse(id);
        }
        response.addProperty("jsonrpc", "2.0");
        response.addProperty("id", id);
        return response;
    }

    private static JsonElement send(JsonObject request, String clientAddress) throws ResponseException, IOException {
        var id = request.get("id").getAsString();
        var ctx = connections.get(clientAddress);
        Promise<UnblockResult> promise = ctx.executor().newPromise();
        var handler = (ScriptServerIOHandler) ctx.handler();
        handler.unblockPromises.put(id, promise);
        handler.callbackRequestIds.addFirst(id);
        ctx.writeAndFlush(request).awaitUninterruptibly();
        UnblockResult result;
        while (true) {
            if (!promise.awaitUninterruptibly(Options.timeoutMillis())) {
                Log.error("Communication timed out, disconnected");
                ctx.close();
                return null;
            }
            result = promise.getNow();
            if (result == null) {
                return null;
            } else if (result.isRequest()) {
                promise = result.nextPromise();
                handler.unblockPromises.put(id, promise);
                ctx.writeAndFlush(handleRequest(result.msg(), result.addr()));
                continue;
            }
            break;
        }
        handler.unblockPromises.remove(id);
        handler.callbackRequestIds.removeFirst();
        JsonObject response = result.msg();
        if (response.has("error")) {
            throw ResponseException.fromResponse(response);
        } else {
            return response.get("result");
        }
    }

    public static JsonElement send(String method, JsonObject params, String clientAddress) throws ResponseException {
        var request = new JsonObject();
        request.addProperty("jsonrpc", "2.0");
        request.addProperty("method", method);
        request.add("params", params);
        request.addProperty("id", id());
        try {
            return send(request, clientAddress);
        } catch (IOException e) {
            Log.error("IOException caught");
            return null;
        }
    }

    public static void sync() {
        MapHelper.forEachValueSynchronized(connections, ctx -> {
            var handler = (ScriptServerIOHandler) ctx.handler();
            while (true) {
                if (!handler.fallbackUnblockPromise.isDone()) {
                    return;
                }
                var result = handler.fallbackUnblockPromise.getNow();
                if (result.isResponse()) {
                    return;
                }
                handler.fallbackUnblockPromise = result.nextPromise();
                ctx.writeAndFlush(handleRequest(result.msg(), result.addr()));
            }
        });
    }
}
