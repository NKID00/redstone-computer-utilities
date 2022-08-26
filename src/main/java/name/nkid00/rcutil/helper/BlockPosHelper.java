package name.nkid00.rcutil.helper;

import name.nkid00.rcutil.util.Blocks;
import name.nkid00.rcutil.util.TargetBlockPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class BlockPosHelper {
    public static Vec3i toVec3i(BlockPos v) {
        if (v == null) {
            return null;
        }
        return new Vec3i(v.getX(), v.getY(), v.getZ());
    }

    public static BlockPos fromVec3i(Vec3i v) {
        if (v == null) {
            return null;
        }
        return new BlockPos(v);
    }

    public static BlockPos copy(BlockPos v) {
        if (v == null) {
            return null;
        }
        return v.mutableCopy();
    }

    public static TargetBlockPos copy(TargetBlockPos v) {
        if (v == null) {
            return null;
        }
        return v.copy();
    }

    public static Blocks copy(Blocks v) {
        if (v == null) {
            return null;
        }
        return new Blocks(v.first(), v.increment(), v.size());
    }

    public static Vec3i copy(Vec3i v) {
        if (v == null) {
            return null;
        }
        return new Vec3i(v.getX(), v.getY(), v.getZ());
    }

    public static Vec3i scale(Vec3i v, int factor) {
        return new Vec3i(v.getX() * factor, v.getY() * factor, v.getZ() * factor);
    }

    public static Vec3i divide(Vec3i v, int divisor) {
        return new Vec3i(v.getX() / divisor, v.getY() / divisor, v.getZ() / divisor);
    }

    public static Vec3i getOffset(BlockPos begin, BlockPos end) {
        return new Vec3i(end.getX() - begin.getX(), end.getY() - begin.getY(), end.getZ() - begin.getZ());
    }

    public static BlockPos applyOffset(BlockPos v, Vec3i offset) {
        return new BlockPos(v.getX() + offset.getX(), v.getY() + offset.getY(), v.getZ() + offset.getZ());
    }

    public static TargetBlockPos applyOffset(TargetBlockPos v, Vec3i offset) {
        return new TargetBlockPos(v.world(), v.getX() + offset.getX(), v.getY() + offset.getY(), v.getZ() + offset.getZ());
    }

    public static String toString(BlockPos pos) {
        return "%s, %s, %s".formatted(pos.getX(), pos.getY(), pos.getZ());
    }

    public static String toString(TargetBlockPos pos) {
        return pos.toString();
    }
}
