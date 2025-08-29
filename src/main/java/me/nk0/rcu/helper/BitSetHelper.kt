package me.nk0.rcu.helper

import java.util.*

object BitSetHelper {
    val BASE64_DECODER: Base64.Decoder = Base64.getDecoder()
    val BASE64_ENCODER: Base64.Encoder = Base64.getEncoder()

    fun toLong(v: BitSet): Long {
        val longArray = v.toLongArray()
        return if (longArray.size > 0) {
            longArray[0]
        } else {
            0
        }
    }

    fun toByteArray(v: BitSet, bytes: Int): ByteArray {
        val rawByteArray = v.toByteArray()
        val rawLength = rawByteArray.size
        if (rawLength < bytes) {
            val byteArray = ByteArray(bytes)
            var i = 0
            while (i < rawLength) {
                byteArray[i] = rawByteArray[i]
                i++
            }
            while (i < bytes) {
                byteArray[i] = 0
                i++
            }
            return byteArray
        } else {
            return rawByteArray
        }
    }

    @JvmOverloads
    fun reverse(v: BitSet, bits: Int = v.size()): BitSet {
        if ((bits and 7) > 0) {
            val reversed = BitSet.valueOf(DataHelper.reverseByteArray(toByteArray(v, bits shr 3)))
            val remaining = v[bits and 7.inv(), v.length()]
            val result = BitSet(bits)
            var p = 0
            run {
                var i = remaining.length() - 1
                while (i >= 0) {
                    result[p] = remaining[i]
                    i--
                    p++
                }
            }
            var i = 0
            while (i < reversed.length()) {
                result[p] = reversed[i]
                i++
                p++
            }
            return result
        } else {
            return BitSet.valueOf(DataHelper.reverseByteArray(toByteArray(v, bits shr 3)))
        }
    }

    @JvmStatic
    fun fromBase64(v: String?): BitSet {
        return BitSet.valueOf(BASE64_DECODER.decode(v))
    }

    @JvmStatic
    fun toBase64(v: BitSet): String {
        return BASE64_ENCODER.encodeToString(v.toByteArray())
    }

    // useful when debugging
    fun toString(v: BitSet, bits: Int): String {
        val stringBuilder = StringBuilder(bits + (bits shr 3))
        var i = 0
        while (i < bits) {
            for (j in 0..7) {
                stringBuilder.append(if (v[i + j]) '1' else '0')
            }
            stringBuilder.append(' ')
            i += 8
        }
        return String.format("\"%s\"(%d)", stringBuilder.toString().substring(0, bits + (bits shr 3)),
                bits + (bits shr 3))
    }
}
