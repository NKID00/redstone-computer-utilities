package name.nkid00.rcutil.helper;

import java.util.Base64;
import java.util.BitSet;

public class BitSetHelper {
    static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();
    static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

    public static long toLong(BitSet v) {
        var longArray = v.toLongArray();
        if (longArray.length > 0) {
            return longArray[0];
        } else {
            return 0;
        }
    }

    public static byte[] toByteArray(BitSet v, int bytes) {
        var rawByteArray = v.toByteArray();
        var rawLength = rawByteArray.length;
        if (rawLength < bytes) {
            var byteArray = new byte[bytes];
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

    public static BitSet reverse(BitSet v) {
        return reverse(v, v.size());
    }

    public static BitSet reverse(BitSet v, int bits) {
        if ((bits & 0b111) > 0) {
            var reversed = BitSet.valueOf(DataHelper.reverseByteArray(toByteArray(v, bits >> 3)));
            var remaining = v.get(bits & ~0b111, v.length());
            var result = new BitSet(bits);
            int p = 0;
            for (int i = remaining.length() - 1; i >= 0; i--, p++) {
                result.set(p, remaining.get(i));
            }
            for (int i = 0; i < reversed.length(); i++, p++) {
                result.set(p, reversed.get(i));
            }
            return result;
        } else {
            return BitSet.valueOf(DataHelper.reverseByteArray(toByteArray(v, bits >> 3)));
        }
    }

    public static BitSet fromBase64(String v) {
        return BitSet.valueOf(BASE64_DECODER.decode(v));
    }

    public static String toBase64(BitSet v) {
        return BASE64_ENCODER.encodeToString(v.toByteArray());
    }

    // useful when debugging
    public static String toString(BitSet v, int bits) {
        StringBuilder stringBuilder = new StringBuilder(bits + (bits >> 3));
        for (int i = 0; i < bits; i += 8) {
            for (int j = 0; j < 8; j++) {
                stringBuilder.append(v.get(i + j) ? '1' : '0');
            }
            stringBuilder.append(' ');
        }
        return String.format("\"%s\"(%d)", stringBuilder.toString().substring(0, bits + (bits >> 3)),
                bits + (bits >> 3));
    }
}
