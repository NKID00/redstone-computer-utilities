package name.nkid00.rcutil.io;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
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
import name.nkid00.rcutil.Options;
import name.nkid00.rcutil.helper.Log;

public class ScriptServerIO {
    private static MultithreadEventLoopGroup group;
    private static InetAddress address;
    private static int port;
    private static ServerBootstrap bootstrap;
    private static Channel channel;

    public static void init() {
        address = null;
        port = Options.port();
        if (Options.localhostOnly()) {
            address = InetAddress.getLoopbackAddress();
            Log.info("Strarting script server on localhost:{}", port);
        } else {
            if (!Options.host().isEmpty()) {
                try {
                    address = InetAddress.getByName(Options.host());
                } catch (UnknownHostException e) {
                }
            }
            Log.info("Strarting script server on {}:{}", address == null ? "*" : address.getHostAddress(), port);
        }
        bootstrap = new ServerBootstrap();
        var threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("rcutil script server #%d")
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
                        .addLast(new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN, 32767, 0, 2, 0, 2, true))
                        .addLast(new LengthFieldPrepender(ByteOrder.BIG_ENDIAN, 2, 0, false))
                        .addLast(new JsonCodec())
                        .addLast(new ScriptServerIOHandler());
            }
        });
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    public static void start() {
        channel = bootstrap.bind(address, port).syncUninterruptibly().channel();
    }

    public static void stop() {
        Log.info("Stopping script server");
        channel.close().syncUninterruptibly();
        group.shutdownGracefully().syncUninterruptibly();
    }
}
