package name.nkid00.rcutil;

import java.util.BitSet;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class MathUtil {
    public static Vec3i BlockPos2Vec3i(BlockPos v) {
        return new Vec3i(v.getX(), v.getY(), v.getZ());
    }

    public static BlockPos Vec3i2BlockPos(Vec3i v) {
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

    public static boolean onSameLine(BlockPos v1, BlockPos v2, BlockPos v3) {
        Vec3i offset12 = getOffset(v1, v2);
        Vec3i offset13 = getOffset(v1, v3);
        float x, y, z;
        if (offset12.getX() == 0) {
            if (offset13.getX() == 0) {
                x = 0F;
            } else {
                return false;
            }
        } else {
            x = offset13.getX() / offset12.getX();
        }
        if (offset12.getY() == 0) {
            if (offset13.getY() == 0) {
                y = 0F;
            } else {
                return false;
            }
        } else {
            y = offset13.getY() / offset12.getY();
        }
        if (offset12.getZ() == 0) {
            if (offset13.getZ() == 0) {
                z = 0F;
            } else {
                return false;
            }
        } else {
            z = offset13.getZ() / offset12.getZ();
        }
        if ((x == 0F && y == 0F) || (x == 0F && z == 0F) || (y == 0F && z == 0F)) {
            return true;
        } else {
            return isFloatEqual(x, y) && isFloatEqual(y, z);
        }
    }

    public static boolean isFloatIntegral(float v) {
        int vi = (int)v;
        if (v >= 0) {
            return v - vi < 1e-5F || v - vi > 0.99999F;
        } else {
            return -(v - vi) < 1e-5F || -(v - vi) > 0.99999F;
        }
    }

    public static boolean isFloatEqual(float v1, float v2) {
        return Math.abs(v1 - v2) < 1e-5F;
    }

    public static int float2Int(float v) {
        int vi = (int)v;
        if (v - vi > 0.99999F) {
            return vi + 1;
        } else if (-(v - vi) > 0.99999F) {
            return vi - 1;
        } else {
            return vi;
        }
    }

    public static long bitSet2Long(BitSet v) {
        long[] longArray = v.toLongArray();
        if (longArray.length > 0) {
            return longArray[0];
        } else {
            return 0;
        }
    }

    // 0 <= h <= 360, 0 <= s, v, r, g, b <= 1
    public static float[] HSV2RGB(float h, float s, float v) {
        float c = v * s;
        float x = c * (1F - Math.abs((h / 60F % 2F) - 1F));
        float m = v - c;
        if (0F <= h && h <= 60F) {
            return new float[]{c + m, x + m, m};
        } else if (60F < h && h <= 120F) {
            return new float[]{x + m, c + m, m};
        } else if (120F < h && h <= 180F) {
            return new float[]{m, c + m, x + m};
        } else if (180F < h && h <= 240F) {
            return new float[]{m, x + m, c + m};
        } else if (240F < h && h <= 300F) {
            return new float[]{x + m, m, c + m};
        } else {  // 300F < h && h <= 360F
            return new float[]{c + m, m, x + m};
        }
    }
}
