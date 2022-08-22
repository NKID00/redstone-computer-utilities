package name.nkid00.rcutil.io;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

public class JsonCodec extends ByteToMessageCodec<JsonElement> {
    private static Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        var json = in.readCharSequence(in.readableBytes(), StandardCharsets.UTF_8).toString();
        out.add(gson.fromJson(json, JsonElement.class));
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, JsonElement msg, ByteBuf out) throws Exception {
        out.writeBytes(gson.toJson(msg).getBytes(StandardCharsets.UTF_8));
    }
}
