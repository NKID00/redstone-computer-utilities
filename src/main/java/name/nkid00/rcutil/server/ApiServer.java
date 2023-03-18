package name.nkid00.rcutil.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.Promise;
import name.nkid00.rcutil.Options;
import name.nkid00.rcutil.event.Event;
import name.nkid00.rcutil.exception.ApiException;
import name.nkid00.rcutil.exception.BlockNotTargetException;
import name.nkid00.rcutil.helper.BitSetHelper;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.GametimeHelper;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.helper.TextHelper;
import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.model.Interface;
import name.nkid00.rcutil.model.Script;
import name.nkid00.rcutil.util.BlockPosWithWorld;
import net.minecraft.server.MinecraftServer;

public class ApiServer {
    private static MultithreadEventLoopGroup group;
    private static InetAddress address;
    private static int port;
    private static ServerBootstrap bootstrap;
    private static Channel serverChannel;
    public static MinecraftServer server;
    // nested event scope
    // pushes when a new event is published
    // pops when the event finished or script disconnected
    public static final ConcurrentLinkedDeque<Script> eventScopeStack = new ConcurrentLinkedDeque<>();
    // promise that blocks the game to wait for reply
    public static Promise<Reply> blockingPromise;

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
        group = new NioEventLoopGroup(0, new ThreadFactoryBuilder()
                .setNameFormat("rcutil#%d")
                .setDaemon(true)
                .build());
        bootstrap = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(group)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(65536))
                                .addLast(new WebSocketServerProtocolHandler(WebSocketServerProtocolConfig.newBuilder()
                                        .websocketPath("/")
                                        .checkStartsWith(true)
                                        .build()))
                                .addLast(new JsonCodec())
                                .addLast(new ApiServerHandler());
                    }
                })
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ApiServer.server = server;
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

    public static JsonObject publishEvent(Event event, JsonObject content, Script script) throws ApiException {
        var msg = new JsonObject();
        msg.addProperty("event", event.name());
        msg.add("param", event.param());
        msg.add("content", content);
        var ctx = script.ctx;
        var previousPromise = blockingPromise;
        Promise<Reply> promise = ctx.executor().newPromise();
        blockingPromise = promise;
        eventScopeStack.push(script);
        ctx.writeAndFlush(msg).awaitUninterruptibly();
        Reply reply;
        while (true) {
            if (!promise.awaitUninterruptibly(Options.timeoutMillis())) {
                Log.error("Communication timed out, disconnecting");
                ctx.close();
                return null;
            }
            reply = promise.getNow();
            if (reply == null) {
                Log.error("reply is null, disconnecting");
                ctx.close();
                return null;
            } else if (reply.isApiCall()) {
                JsonObject result = new JsonObject();
                try {
                    result.add("result", dispatchApiCall(reply.msg(), script));
                } catch (ApiException e) {
                    result.addProperty("result", e.code());
                }
                ctx.writeAndFlush(result);
                continue;
            }
            break; // reply.isEventFinish()
        }
        blockingPromise = previousPromise;
        eventScopeStack.pop();
        JsonObject eventFinishReply = reply.msg();
        var result = eventFinishReply.get("finish");
        if (result.isJsonObject()) {
            return result.getAsJsonObject();
        } else {
            throw ApiException.fromCode(result.getAsInt());
        }
    }

    public static JsonObject dispatchApiCall(JsonObject msg, Script script) throws ApiException {
        var result = new JsonObject();
        // TODO: check whether property exists
        var api = msg.get("api").getAsString();
        var param = msg.get("param").getAsJsonObject();
        switch (api) {
            case "subscribe": {
                // TODO: check whether event name exists
                var event = Event.fromJson(
                        param.get("name").getAsString(),
                        param.get("param").getAsJsonObject());
                // TODO: check whether event is already subscribed
                script.subscribe(event);
                return result;
            }
            case "unsubscribe": {
                // TODO: check whether event name exists
                var event = Event.fromJson(
                        param.get("name").getAsString(),
                        param.get("param").getAsJsonObject());
                // TODO: check whether event is subscribed
                script.unsubscribe(event);
                return result;
            }
            case "newInterface": {
                // TODO: check whether name argument exists
                var name = param.get("name").getAsString();
                if (!CommandHelper.isLetterDigitUnderline(name)) {
                    throw ApiException.NAME_ILLEGAL;
                }
                if (InterfaceManager.nameExists(name)) {
                    throw ApiException.NAME_EXISTS;
                }
                var lsb = BlockPosWithWorld.fromJson(param.get("lsb").getAsJsonArray());
                var msb = BlockPosWithWorld.fromJson(param.get("msb").getAsJsonArray());
                if (!lsb.world().equals(msb.world())) {
                    throw ApiException.GENERAL_ERROR;
                }
                JsonArray option;
                if (param.get("option") == null) {
                    option = new JsonArray();
                } else {
                    option = param.get("option").getAsJsonArray();
                }
                Interface interfaze;
                try {
                    interfaze = InterfaceManager.tryCreate(
                            name, null, lsb.world(), lsb.pos(), msb.pos(),
                            option.asList().stream().map(v -> v.getAsString()).toList());
                } catch (BlockNotTargetException e) {
                    throw ApiException.GENERAL_ERROR;
                } catch (IllegalArgumentException e) {
                    throw ApiException.ARGUMENT_INVALID;
                }
                if (interfaze == null) {
                    throw ApiException.GENERAL_ERROR;
                }
                return result;
            }
            case "removeInterface": {
                // TODO: check whether interface exists
                var name = param.get("name").getAsString();
                InterfaceManager.remove(name);
                return result;
            }
            case "listInterface": {
                // TODO: implement listInterface
                return result;
            }
            case "readInterface": {
                // TODO: check whether name argument exists
                var name = param.get("name").getAsString();
                var interfaze = InterfaceManager.interfaceByName(name);
                if (interfaze == null) {
                    throw ApiException.NAME_NOT_FOUND;
                }
                result.addProperty("value", BitSetHelper.toBase64(interfaze.readSuppress()));
                return result;
            }
            case "writeInterface": {
                // TODO: check whether argument exists
                var name = param.get("name").getAsString();
                var interfaze = InterfaceManager.interfaceByName(name);
                if (interfaze == null) {
                    throw ApiException.NAME_NOT_FOUND;
                }
                interfaze.writeSuppress(BitSetHelper.fromBase64(param.get("value").getAsString()));
                return result;
            }
            case "queryGametime": {
                // TODO: check for unnecessary arguments
                result.addProperty("gametime", GametimeHelper.gametime());
                return result;
            }
            case "executeCommand": {
                // TODO: implement executeCommand
                return result;
            }
            case "log": {
                var message = param.get("message").getAsString();
                switch (param.get("level").getAsString()) {
                    case "info":
                        Log.info("({}) {}", script.name, message);
                        Log.broadcastToOps(server, "(%s/INFO) %s".formatted(script.name, message));
                        return result;
                    case "warn":
                        Log.warn("({}) {}", script.name, message);
                        Log.broadcastToOps(server, TextHelper.warn(TextHelper.literal(
                                "(%s/WARN) %s".formatted(script.name, message))));
                        return result;
                    case "error":
                        Log.error("({}) {}", script.name, message);
                        Log.broadcastToOps(server, TextHelper.error(TextHelper.literal(
                                "(%s/ERROR) %s".formatted(script.name, message))));
                        return result;
                    default:
                        throw ApiException.ARGUMENT_INVALID;
                }
            }
            default: {
                Log.error("invalid api, disconnecting");
                script.ctx.close();
                return null;
            }
        }
    }
}
