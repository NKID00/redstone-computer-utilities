package name.nkid00.rcutil.server;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class JsonCodec extends MessageToMessageCodec<TextWebSocketFrame, JsonElement> {
    private static Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame msg, List<Object> out) throws Exception {
        out.add(gson.fromJson(msg.text(), JsonElement.class));
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, JsonElement msg, List<Object> out) throws Exception {
        out.add(new TextWebSocketFrame(gson.toJson(msg)));
    }
}
