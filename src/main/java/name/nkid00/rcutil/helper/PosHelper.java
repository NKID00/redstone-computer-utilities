package name.nkid00.rcutil.helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import name.nkid00.rcutil.util.Blocks;
import name.nkid00.rcutil.util.TargetBlockPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;

public class PosHelper {
    public static Vec3i toVec3i(BlockPos v) {
        if (v == null) {
            return null;
        }
        return new Vec3i(v.getX(), v.getY(), v.getZ());
    }

    public static BlockPos toBlockPos(Vec3i v) {
        if (v == null) {
            return null;
        }
        return new BlockPos(v);
    }

    public static Vec3i toVec3i(Vec3f v) {
        if (v == null) {
            return null;
        }
        return new Vec3i(v.getX(), v.getY(), v.getZ());
    }

    public static Vec3f toVec3f(Vec3i v) {
        if (v == null) {
            return null;
        }
        return new Vec3f(v.getX(), v.getY(), v.getZ());
    }

    public static Vec3i fromJson(JsonElement v) {
        return fromJson(v.getAsJsonArray());
    }

    public static Vec3i fromJson(JsonArray v) {
        return new Vec3i(v.get(0).getAsInt(), v.get(1).getAsInt(), v.get(2).getAsInt());
    }

    public static Vec3f center(BlockPos v) {
        return new Vec3f(v.getX() + 0.5f, v.getY() + 0.5f, v.getZ() + 0.5f);
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

    public static Vec3i copy(Vec3i v) {
        if (v == null) {
            return null;
        }
        return new Vec3i(v.getX(), v.getY(), v.getZ());
    }

    public static Vec3f copy(Vec3f v) {
        if (v == null) {
            return null;
        }
        return new Vec3f(v.getX(), v.getY(), v.getZ());
    }

    public static Blocks copy(Blocks v) {
        if (v == null) {
            return null;
        }
        return new Blocks(v.first(), v.increment(), v.size());
    }

    public static Vec3i scale(Vec3i v, int factor) {
        if (factor == 1) {
            return copy(v);
        }
        return new Vec3i(v.getX() * factor, v.getY() * factor, v.getZ() * factor);
    }

    public static Vec3i divide(Vec3i v, int divisor) {
        if (divisor == 1) {
            return copy(v);
        }
        return new Vec3i(v.getX() / divisor, v.getY() / divisor, v.getZ() / divisor);
    }

    public static Vec3f scale(Vec3f v, float factor) {
        if (DataHelper.isFloatEqual(factor, 1.0f)) {
            return copy(v);
        }
        return new Vec3f(v.getX() * factor, v.getY() * factor, v.getZ() * factor);
    }

    public static Vec3f divide(Vec3f v, float divisor) {
        if (DataHelper.isFloatEqual(divisor, 1.0f)) {
            return copy(v);
        }
        return new Vec3f(v.getX() / divisor, v.getY() / divisor, v.getZ() / divisor);
    }

    public static Vec3i getOffset(BlockPos begin, BlockPos end) {
        return new Vec3i(end.getX() - begin.getX(), end.getY() - begin.getY(), end.getZ() - begin.getZ());
    }

    public static BlockPos applyOffset(BlockPos v, Vec3i offset) {
        return new BlockPos(v.getX() + offset.getX(), v.getY() + offset.getY(), v.getZ() + offset.getZ());
    }

    public static TargetBlockPos applyOffset(TargetBlockPos v, Vec3i offset) {
        return new TargetBlockPos(v.world(), v.getX() + offset.getX(), v.getY() + offset.getY(),
                v.getZ() + offset.getZ());
    }

    public static Vec3f applyOffset(Vec3f v, Vec3i offset) {
        return new Vec3f(v.getX() + offset.getX(), v.getY() + offset.getY(), v.getZ() + offset.getZ());
    }

    public static Vec3f applyOffset(Vec3f v, Vec3f offset) {
        return new Vec3f(v.getX() + offset.getX(), v.getY() + offset.getY(), v.getZ() + offset.getZ());
    }

    public static Vec3f getPerpendicularVector(Direction v) {
        return new Vec3f(1 - v.getOffsetX(), 1 - v.getOffsetY(), 1 - v.getOffsetZ());
    }

    public static String toString(BlockPos v) {
        return "%s, %s, %s".formatted(v.getX(), v.getY(), v.getZ());
    }

    public static String toString(TargetBlockPos v) {
        return v.toString();
    }
}
