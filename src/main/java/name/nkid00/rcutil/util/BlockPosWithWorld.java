package name.nkid00.rcutil.util;

import com.google.gson.JsonArray;

import name.nkid00.rcutil.helper.PosHelper;
import name.nkid00.rcutil.helper.WorldHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record BlockPosWithWorld(BlockPos pos, ServerWorld world) {
    public JsonArray toJson() {
        var result = new JsonArray();
        result.add(pos.getX());
        result.add(pos.getY());
        result.add(pos.getZ());
        result.add(WorldHelper.toString(world));
        return result;
    }

    public static BlockPosWithWorld fromJson(JsonArray v) {
        return new BlockPosWithWorld(
                PosHelper.toBlockPos(PosHelper.fromJson(v)),
                WorldHelper.fromString(v.get(3).getAsString()));
    }
}
