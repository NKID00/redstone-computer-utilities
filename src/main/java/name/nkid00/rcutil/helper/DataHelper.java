package name.nkid00.rcutil.helper;

import java.util.BitSet;

import net.minecraft.util.math.Vec3f;

public class DataHelper {
    public static boolean isFloatIntegral(float v) {
        int vi = (int) v;
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
        int vi = (int) v;
        if (v - vi > 0.99999F) {
            return vi + 1;
        } else if (-(v - vi) > 0.99999F) {
            return vi - 1;
        } else {
            return vi;
        }
    }

    public static byte reverseByte(byte v) {
        BitSet bitSet = BitSet.valueOf(new byte[] { v });
        BitSet result = new BitSet(8);
        for (int i = 0; i < 8; i++) {
            result.set(i, bitSet.get(7 - i));
        }
        byte b = BitSetHelper.toByteArray(result, 1)[0];
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

    // useful when debugging
    public static String byteArray2String(byte[] v) {
        if (v.length > 0) {
            StringBuilder stringBuilder = new StringBuilder(v.length * 9);
            for (byte b : v) {
                BitSet bitSet = BitSet.valueOf(new byte[] { b });
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

    // 0 <= h <= 360, 0 <= s, v, r, g, b <= 1
    public static float[] HSV2RGB(float h, float s, float v) {
        float c = v * s;
        float x = c * (1F - Math.abs((h / 60F % 2F) - 1F));
        float m = v - c;
        if (0F <= h && h <= 60F) {
            return new float[] { c + m, x + m, m };
        } else if (60F < h && h <= 120F) {
            return new float[] { x + m, c + m, m };
        } else if (120F < h && h <= 180F) {
            return new float[] { m, c + m, x + m };
        } else if (180F < h && h <= 240F) {
            return new float[] { m, x + m, c + m };
        } else if (240F < h && h <= 300F) {
            return new float[] { x + m, m, c + m };
        } else { // 300F < h && h <= 360F
            return new float[] { c + m, m, x + m };
        }
    }

    // 0 <= h <= 360, 0 <= s, v, r, g, b <= 1
    public static Vec3f HSV2RGBVec3f(float h, float s, float v) {
        var rgb = HSV2RGB(h, s, v);
        return new Vec3f(rgb[0], rgb[1], rgb[2]);
    }

    // 0 <= v <= 1
    public static float linearMap(float begin, float end, float v) {
        return begin + (end - begin) * v;
    }

    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public static int gcd(int a, int b, int c) {
        return gcd(gcd(a, b), c);
    }

    public static int gcd(int... numbers) {
        if (numbers.length == 0) {
            return 1;
        } else if (numbers.length == 1) {
            return numbers[0];
        } else if (numbers.length == 2) {
            return gcd(numbers[0], numbers[1]);
        } else if (numbers.length == 3) {
            return gcd(numbers[0], numbers[1], numbers[2]);
        } else {
            var result = numbers[0];
            for (int i = 1; i < numbers.length; i++) {
                result = gcd(result, numbers[i]);
            }
            return result;
        }
    }
}
