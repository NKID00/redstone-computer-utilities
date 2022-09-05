package name.nkid00.rcutil.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import io.netty.util.concurrent.Promise;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.manager.ScriptManager;

public class ScriptServerIOHandler extends SimpleChannelInboundHandler<JsonElement> {
    private String addr;
    private static final ByteBuf PARSE_ERROR_RESPONSE = Unpooled.wrappedBuffer("""
            {"jsonrpc":"2.0","error":{"code":-32700,"message":"Parse error"},"id":null}"""
            .getBytes(StandardCharsets.UTF_8));
    private static final ByteBuf INVALID_REQUEST_RESPONSE = Unpooled.wrappedBuffer("""
            {"jsonrpc":"2.0","error":{"code":-32600,"message":"Invalid Request"},"id":null}"""
            .getBytes(StandardCharsets.UTF_8));
    public ConcurrentLinkedDeque<String> callbackRequestIds = new ConcurrentLinkedDeque<>();
    public ConcurrentHashMap<String, Promise<UnblockResult>> unblockPromises = new ConcurrentHashMap<>();
    public Promise<UnblockResult> fallbackUnblockPromise;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        addr = ctx.channel().remoteAddress().toString();
        ScriptServerIO.connections.put(addr, ctx);
        fallbackUnblockPromise = ctx.executor().newPromise();
        Log.info("{} connected", addr);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonElement msg) throws Exception {
        if (msg.isJsonObject()) {
            dispatch(ctx, msg.getAsJsonObject());
        } else if (msg.isJsonArray()) {
            for (JsonElement element : msg.getAsJsonArray()) {
                dispatch(ctx, element.getAsJsonObject());
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ScriptServerIO.connections.remove(addr);
        for (var promise : unblockPromises.values()) {
            promise.trySuccess(null);
        }
        Log.info("{} disconnected", addr);
        ScriptManager.deregisterClientAddress(addr);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof DecoderException) {
            PARSE_ERROR_RESPONSE.retain();
            ctx.writeAndFlush(PARSE_ERROR_RESPONSE);
        } else if (cause instanceof IOException) {
            Log.error("IOException caught, disconnected");
            ctx.close();
        } else {
            ctx.fireExceptionCaught(cause);
        }
    }

    private void dispatch(ChannelHandlerContext ctx, JsonObject msg) throws Exception {
        try {
            if (!msg.get("jsonrpc").getAsString().equals("2.0")) {
                INVALID_REQUEST_RESPONSE.retain();
                ctx.writeAndFlush(INVALID_REQUEST_RESPONSE);
            } else if (msg.has("method")) {
                if (callbackRequestIds.isEmpty()) {
                    fallbackUnblockPromise.trySuccess(UnblockResult.Request(
                            msg, addr, ctx.executor().newPromise()));
                } else {
                    var id = callbackRequestIds.getFirst();
                    if (unblockPromises.containsKey(id)) {
                        unblockPromises.get(id).trySuccess(UnblockResult.Request(
                                msg, addr, ctx.executor().newPromise()));
                    }
                }
            } else if (msg.has("result") || msg.has("error")) {
                var id = msg.get("id").getAsString();
                if (unblockPromises.containsKey(id)) {
                    unblockPromises.get(id).trySuccess(UnblockResult.Response(msg));
                }
            } else {
                INVALID_REQUEST_RESPONSE.retain();
                ctx.writeAndFlush(INVALID_REQUEST_RESPONSE);
            }
        } catch (ClassCastException | IllegalStateException | UnsupportedOperationException | NullPointerException e) {
            INVALID_REQUEST_RESPONSE.retain();
            ctx.writeAndFlush(INVALID_REQUEST_RESPONSE);
        }
    }
}
