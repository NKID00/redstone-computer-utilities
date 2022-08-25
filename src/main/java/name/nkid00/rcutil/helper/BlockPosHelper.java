package name.nkid00.rcutil.helper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class BlockPosHelper {
    public static Vec3i toVec3i(BlockPos v) {
        return new Vec3i(v.getX(), v.getY(), v.getZ());
    }

    public static BlockPos fromVec3i(Vec3i v) {
        return new BlockPos(v);
    }

    public static Vec3i scale(Vec3i v, int s) {
        return new Vec3i(v.getX() * s, v.getY() * s, v.getZ() * s);
    }

    public static Vec3i getOffset(BlockPos begin, BlockPos end) {
        return new Vec3i(end.getX() - begin.getX(), end.getY() - begin.getY(), end.getZ() - begin.getZ());
    }

    public static BlockPos applyOffset(BlockPos v, Vec3i offset) {
        return new BlockPos(v.getX() + offset.getX(), v.getY() + offset.getY(), v.getZ() + offset.getZ());
    }

    public static String toString(BlockPos pos) {
        return "%s, %s, %s".formatted(pos.getX(), pos.getY(), pos.getZ());
    }
}
