package name.nkid00.rcutil.adapter;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import name.nkid00.rcutil.helper.WorldHelper;
import name.nkid00.rcutil.util.BlockPosWithWorld;
import net.minecraft.util.math.BlockPos;

public class BlockPosWithWorldAdapter extends TypeAdapter<BlockPosWithWorld> {
    @Override
    public void write(JsonWriter out, BlockPosWithWorld value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.beginArray();
            out.value(value.pos().getX());
            out.value(value.pos().getY());
            out.value(value.pos().getZ());
            out.value(WorldHelper.toString(value.world()));
            out.endArray();
        }
    }

    @Override
    public BlockPosWithWorld read(JsonReader in) throws IOException {
        if (in.peek().equals(JsonToken.NULL)) {
            in.nextNull();
            return null;
        } else {
            in.beginArray();
            var x = in.nextInt();
            var y = in.nextInt();
            var z = in.nextInt();
            var world = WorldHelper.fromString(in.nextString());
            in.endArray();
            return new BlockPosWithWorld(new BlockPos(x, y, z), world);
        }
    }
}
