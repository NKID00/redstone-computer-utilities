package name.nkid00.rcutil.helper

import name.nkid00.rcutil.util.Vec3f
import java.util.*
import kotlin.math.abs

object DataHelper {
    fun isFloatIntegral(v: Float): Boolean {
        val vi = v.toInt()
        return if (v >= 0) {
            v - vi < 1e-5f || v - vi > 0.99999f
        } else {
            -(v - vi) < 1e-5f || -(v - vi) > 0.99999f
        }
    }

    fun isFloatEqual(v1: Float, v2: Float): Boolean {
        return abs((v1 - v2).toDouble()) < 1e-5f
    }

    fun float2Int(v: Float): Int {
        val vi = v.toInt()
        return if (v - vi > 0.99999f) {
            vi + 1
        } else if (-(v - vi) > 0.99999f) {
            vi - 1
        } else {
            vi
        }
    }

    fun reverseByte(v: Byte): Byte {
        val bitSet = BitSet.valueOf(byteArrayOf(v))
        val result = BitSet(8)
        for (i in 0..7) {
            result[i] = bitSet[7 - i]
        }
        val b = BitSetHelper.toByteArray(result, 1)[0]
        return b
    }

    fun reverseByteArray(v: ByteArray): ByteArray {
        val length = v.size
        val reversed = ByteArray(length)
        for (i in 0 until length) {
            reversed[i] = reverseByte(v[length - i - 1])
        }
        return reversed
    }

    // useful when debugging
    fun byteArray2String(v: ByteArray): String {
        if (v.size > 0) {
            val stringBuilder = StringBuilder(v.size * 9)
            for (b in v) {
                val bitSet = BitSet.valueOf(byteArrayOf(b))
                for (i in 0..7) {
                    stringBuilder.append(if (bitSet[i]) '1' else '0')
                }
                stringBuilder.append(' ')
            }
            return stringBuilder.toString().substring(0, stringBuilder.length - 1)
        } else {
            return ""
        }
    }

    // 0 <= h <= 360, 0 <= s, v, r, g, b <= 1
    fun HSV2RGB(h: Float, s: Float, v: Float): FloatArray {
        val c = v * s
        val x = (c * (1f - abs(((h / 60f % 2f) - 1f).toDouble()))).toFloat()
        val m = v - c
        return if (0f <= h && h <= 60f) {
            floatArrayOf(c + m, x + m, m)
        } else if (60f < h && h <= 120f) {
            floatArrayOf(x + m, c + m, m)
        } else if (120f < h && h <= 180f) {
            floatArrayOf(m, c + m, x + m)
        } else if (180f < h && h <= 240f) {
            floatArrayOf(m, x + m, c + m)
        } else if (240f < h && h <= 300f) {
            floatArrayOf(x + m, m, c + m)
        } else { // 300F < h && h <= 360F
            floatArrayOf(c + m, m, x + m)
        }
    }

    // 0 <= h <= 360, 0 <= s, v, r, g, b <= 1
    @JvmStatic
    fun HSV2RGBVec3f(h: Float, s: Float, v: Float): Vec3f {
        val rgb = HSV2RGB(h, s, v)
        return Vec3f(rgb[0], rgb[1], rgb[2])
    }

    // 0 <= v <= 1
    @JvmStatic
    fun linearMap(begin: Float, end: Float, v: Float): Float {
        return begin + (end - begin) * v
    }

    fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a else gcd(b, a % b)
    }

    fun gcd(a: Int, b: Int, c: Int): Int {
        return gcd(gcd(a, b), c)
    }

    @JvmStatic
    fun gcd(vararg numbers: Int): Int {
        if (numbers.size == 0) {
            return 1
        } else if (numbers.size == 1) {
            return numbers[0]
        } else if (numbers.size == 2) {
            return gcd(numbers[0], numbers[1])
        } else if (numbers.size == 3) {
            return gcd(numbers[0], numbers[1], numbers[2])
        } else {
            var result = numbers[0]
            for (i in 1 until numbers.size) {
                result = gcd(result, numbers[i])
            }
            return result
        }
    }
}
