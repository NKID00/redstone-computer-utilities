package name.nkid00.rcutil.adapter;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import name.nkid00.rcutil.helper.BlockPosHelper;
import net.minecraft.util.math.BlockPos;

public class BlockPosAdapter extends TypeAdapter<BlockPos> {
    private static final Vec3iAdapter VEC3I_ADAPTER = new Vec3iAdapter();

    @Override
    public void write(JsonWriter out, BlockPos value) throws IOException {
        VEC3I_ADAPTER.write(out, BlockPosHelper.toVec3i(value));
    }

    @Override
    public BlockPos read(JsonReader in) throws IOException {
        return BlockPosHelper.fromVec3i(VEC3I_ADAPTER.read(in));
    }

}
