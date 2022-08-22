package name.nkid00.rcutil.io;

import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.script.ScriptApiDispatcher;

public class ScriptServerIOHandler extends SimpleChannelInboundHandler<JsonElement> {
    private String addr;
    private static final ByteBuf PARSE_ERROR_RESPONSE = Unpooled.wrappedBuffer("""
            {"jsonrpc":"2.0","error":{"code":-32700,"message":"Parse error"},"id":null}"""
            .getBytes(StandardCharsets.UTF_8));

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        addr = ctx.channel().remoteAddress().toString();
        Log.info("{} connected", addr);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonElement msg) throws Exception {
        Log.info("Received message {}", msg);
        if (msg.isJsonObject()) {
            ctx.writeAndFlush(ScriptApiDispatcher.dispatch(msg.getAsJsonObject()));
        } else if (msg.isJsonArray()) {
            var result = new JsonArray();
            msg.getAsJsonArray().forEach(element -> {
                result.add(ScriptApiDispatcher.dispatch(element.getAsJsonObject()));
            });
            ctx.writeAndFlush(result);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.info("{} disconnected", addr);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof DecoderException) {
            Log.warn("Received invalid message from " + addr);
            PARSE_ERROR_RESPONSE.retain();
            ctx.writeAndFlush(PARSE_ERROR_RESPONSE);
        } else {
            ctx.fireExceptionCaught(cause);
        }
    }
}
