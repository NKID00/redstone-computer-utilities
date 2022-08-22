package name.nkid00.rcutil.io;

import com.google.gson.JsonElement;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import name.nkid00.rcutil.helper.Log;

public class ScriptServerIOHandler extends SimpleChannelInboundHandler<JsonElement> {
    String addr;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        addr = ctx.channel().remoteAddress().toString();
        Log.info("{} connected", addr);
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonElement msg) throws Exception {
        Log.info("Received message {}", msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.info("{} disconnected", addr);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof DecoderException) {
            Log.warn("Received invalid message from " + addr);
        } else {
            ctx.fireExceptionCaught(cause);
        }
    }
}
