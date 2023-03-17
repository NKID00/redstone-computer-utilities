package name.nkid00.rcutil.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import name.nkid00.rcutil.Options;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.manager.ScriptManager;

public class ApiServerHandler extends SimpleChannelInboundHandler<JsonElement> {
    private String addr;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        addr = ctx.channel().remoteAddress().toString();
        Log.info("{} connected", addr);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonElement msg0) throws Exception {
        if (!msg0.isJsonObject()) {
            Log.error("Invalid message received, disconnecting");
            ctx.close();
            return;
        }
        var msg = msg0.getAsJsonObject();
        if (!msg.has("id")) {
            Log.error("Invalid message received, disconnecting");
            ctx.close();
            return;
        }
        if (msg.has("api")) { // api call
            if (ApiServer.eventScopeStack.isEmpty() || !ApiServer.eventScopeStack.getFirst().isAddr(addr)) {
                Log.error("API call outside event scope, disconnecting");
                ctx.close();
                return;
            }
            ApiServer.blockingPromise.trySuccess(Reply.ApiCall(msg));
        } else if (msg.has("finish")) { // event finish
            if (ApiServer.eventScopeStack.isEmpty() || !ApiServer.eventScopeStack.getFirst().isAddr(addr)) {
                Log.error("Event finish outside event scope, disconnecting");
                ctx.close();
                return;
            }
            ApiServer.blockingPromise.trySuccess(Reply.EventFinish(msg));
        } else {
            Log.error("Invalid message received, disconnecting");
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ScriptManager.deregister(addr);
        Log.info("{} disconnected", addr);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof DecoderException) {
            Log.error("Invalid message received, disconnecting");
            ctx.close();
        } else if (cause instanceof IOException) {
            Log.error("IOException caught, disconnecting");
            ctx.close();
        } else {
            ctx.fireExceptionCaught(cause);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof HandshakeComplete) {
            // when WebSocket connection established
            URI uri;
            try {
                uri = new URI(((HandshakeComplete) evt).requestUri());
            } catch (URISyntaxException e) {
                Log.error("Invalid request \"{}\" in handshake, disconnecting", ((HandshakeComplete) evt).requestUri());
                ctx.close();
                return;
            }
            var path = uri.getPath();
            if (path == null || !path.equals("/")) {
                Log.error("Invalid path \"{}\" in handshake, disconnecting", uri.getPath());
                ctx.close();
                return;
            }
            var query = uri.getQuery();
            if (query == null) {
                Log.error("Empty query in handshake, disconnecting");
                ctx.close();
                return;
            }
            var queryMap = Arrays.stream(query.split("&"))
                    .map(param -> Arrays.stream(param.split("="))
                            .map(s -> URLDecoder.decode(s, StandardCharsets.UTF_8))
                            .toArray(String[]::new))
                    .collect(Collectors.toMap(pair -> pair[0], pair -> pair[1]));
            if (queryMap.get("name") == null) {
                Log.error("Illegal name in handshake, disconnecting");
                ctx.close();
                return;
            }
            if (!Options.key().isEmpty() && !Options.key().equals(queryMap.get("key"))) {
                Log.error("Incorrect key in handshake, disconnecting");
                ctx.close();
                return;
            }
            ScriptManager.register(queryMap.get("name"), queryMap.getOrDefault("description", ""), addr, ctx);
        }
        ctx.fireUserEventTriggered(evt);
    }
}
