package name.nkid00.rcutil.adapter;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import name.nkid00.rcutil.helper.PosHelper;
import net.minecraft.util.math.BlockPos;

public class BlockPosAdapter extends TypeAdapter<BlockPos> {
    private static final Vec3iAdapter VEC3I_ADAPTER = new Vec3iAdapter();

    @Override
    public void write(JsonWriter out, BlockPos value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            VEC3I_ADAPTER.write(out, PosHelper.toVec3i(value));
        }
    }

    @Override
    public BlockPos read(JsonReader in) throws IOException {
        if (in.peek().equals(JsonToken.NULL)) {
            in.nextNull();
            return null;
        } else {
            return PosHelper.toBlockPos(VEC3I_ADAPTER.read(in));
        }
    }
}
