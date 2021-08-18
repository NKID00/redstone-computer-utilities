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
        // TODO: fix sloping lines
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

    public static byte[] bitSet2ByteArray(BitSet v, int bytes) {
        byte[] rawByteArray = v.toByteArray();
        int rawLength = rawByteArray.length;
        if (rawLength < bytes) {
            byte[] byteArray = new byte[bytes];
            int i;
            for (i = 0; i < rawLength; i++) {
                byteArray[i] = rawByteArray[i];
            }
            for (; i < bytes; i++) {
                byteArray[i] = 0;
            }
            return byteArray;
        } else {
            return rawByteArray;
        }
    }

    public static byte reverseByte(byte v) {
        BitSet bitSet = BitSet.valueOf(new byte[]{v});
        BitSet result = new BitSet(8);
        for (int i = 0; i < 8; i++) {
            result.set(i, bitSet.get(7 - i));
        }
        byte b = bitSet2ByteArray(result, 1)[0];
        return b;
    }

    public static byte[] reverseByteArray(byte[] v) {
        int length = v.length;
        byte[] reversed = new byte[length];
        for (int i = 0; i < length; i++) {
            reversed[i] = reverseByte(v[length - i - 1]);
        }
        return reversed;
    }

    public static BitSet reverseBitSet(BitSet v, int bits) {
        if ((bits & 0b111) > 0) {
            BitSet reversed = BitSet.valueOf(reverseByteArray(bitSet2ByteArray(v, bits >> 3)));
            BitSet remaining = v.get(bits & ~0b111, v.length());
            BitSet result = new BitSet(bits);
            int p = 0;
            for (int i = remaining.length() - 1; i >= 0; i--, p++) {
                result.set(p, remaining.get(i));
            }
            for (int i = 0; i < reversed.length(); i++, p++) {
                result.set(p, reversed.get(i));
            }
            return result;
        } else {
            BitSet t = BitSet.valueOf(reverseByteArray(bitSet2ByteArray(v, bits >> 3)));
            return t;
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

    // useful when debugging
    public static String byteArray2String(byte[] v) {
        if (v.length > 0) {
            StringBuilder stringBuilder = new StringBuilder(v.length * 9);
            for (byte b : v) {
                BitSet bitSet = BitSet.valueOf(new byte[]{b});
                for (int i = 0; i < 8; i++) {
                    stringBuilder.append(bitSet.get(i) ? '1' : '0');
                }
                stringBuilder.append(' ');
            }
            return stringBuilder.toString().substring(0, stringBuilder.length() - 1);
        } else {
            return "";
        }
    }

    // useful when debugging
    public static String bitSet2String(BitSet v, int bits) {
        StringBuilder stringBuilder = new StringBuilder(bits + (bits >> 3));
        for (int i = 0; i < bits; i += 8) {
            for (int j = 0; j < 8; j++) {
                stringBuilder.append(v.get(i + j) ? '1' : '0');
            }
            stringBuilder.append(' ');
        }
        return String.format("\"%s\"(%d)", stringBuilder.toString().substring(0, bits + (bits >> 3)), bits + (bits >> 3));
    }
}
