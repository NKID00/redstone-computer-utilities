package me.nk0.rcu.helper

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import me.nk0.rcu.util.Blocks
import me.nk0.rcu.util.TargetBlockPos
import me.nk0.rcu.util.Vec3f
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i
import org.joml.Vector3f

object PosHelper {
    @JvmStatic
    fun toVec3i(v: BlockPos?): Vec3i? {
        if (v == null) {
            return null
        }
        return Vec3i(v.x, v.y, v.z)
    }

    @JvmStatic
    fun toBlockPos(v: Vec3i?): BlockPos? {
        if (v == null) {
            return null
        }
        return BlockPos(v)
    }

    fun toVec3i(v: Vec3f?): Vec3i? {
        if (v == null) {
            return null
        }
        return Vec3i(v.getX().toInt(), v.getY().toInt(), v.getZ().toInt())
    }

    fun toVec3f(v: Vec3i?): Vec3f? {
        if (v == null) {
            return null
        }
        return Vec3f(v.x.toFloat(), v.y.toFloat(), v.z.toFloat())
    }

    fun toVec3f(v: Vec3f): Vec3f {
        return v
    }

    fun toVec3f(v: Vector3f?): Vec3f {
        return Vec3f(v)
    }

    fun fromJson(v: JsonElement): Vec3i {
        return fromJson(v.asJsonArray)
    }

    @JvmStatic
    fun fromJson(v: JsonArray): Vec3i {
        return Vec3i(v[0].asInt, v[1].asInt, v[2].asInt)
    }

    fun center(v: BlockPos): Vec3f {
        return Vec3f(v.x + 0.5f, v.y + 0.5f, v.z + 0.5f)
    }

    @JvmStatic
    fun copy(v: BlockPos?): BlockPos? {
        if (v == null) {
            return null
        }
        return v.mutableCopy()
    }

    fun copy(v: TargetBlockPos?): TargetBlockPos? {
        if (v == null) {
            return null
        }
        return v.copy()
    }

    @JvmStatic
    fun copy(v: Vec3i?): Vec3i? {
        if (v == null) {
            return null
        }
        return Vec3i(v.x, v.y, v.z)
    }

    fun copy(v: Vec3f?): Vec3f? {
        if (v == null) {
            return null
        }
        return Vec3f(v.getX(), v.getY(), v.getZ())
    }

    @JvmStatic
    fun copy(v: Blocks?): Blocks? {
        if (v == null) {
            return null
        }
        return Blocks(v.first(), v.increment(), v.size())
    }

    @JvmStatic
    fun scale(v: Vec3i, factor: Int): Vec3i? {
        if (factor == 1) {
            return copy(v)
        }
        return v.multiply(factor)
    }

    @JvmStatic
    fun divide(v: Vec3i, divisor: Int): Vec3i? {
        if (divisor == 1) {
            return copy(v)
        }
        return Vec3i(v.x / divisor, v.y / divisor, v.z / divisor)
    }

    fun scale(v: Vec3f?, factor: Float): Vec3f? {
        if (DataHelper.isFloatEqual(factor, 1.0f)) {
            return copy(v)
        }
        return Vec3f(v!!.getX() * factor, v.getY() * factor, v.getZ() * factor)
    }

    fun divide(v: Vec3f, divisor: Float): Vec3f? {
        if (DataHelper.isFloatEqual(divisor, 1.0f)) {
            return copy(v)
        }
        return Vec3f(v.getX() / divisor, v.getY() / divisor, v.getZ() / divisor)
    }

    @JvmStatic
    fun getOffset(begin: BlockPos, end: BlockPos): Vec3i {
        return Vec3i(end.x - begin.x, end.y - begin.y, end.z - begin.z)
    }

    @JvmStatic
    fun applyOffset(v: BlockPos, offset: Vec3i): BlockPos {
        return BlockPos(v.x + offset.x, v.y + offset.y, v.z + offset.z)
    }

    fun applyOffset(v: TargetBlockPos, offset: Vec3i): TargetBlockPos {
        return TargetBlockPos(v.world(), v.x + offset.x, v.y + offset.y,
                v.z + offset.z)
    }

    fun applyOffset(v: Vec3f, offset: Vec3i): Vec3f {
        return Vec3f(v.getX() + offset.x, v.getY() + offset.y, v.getZ() + offset.z)
    }

    fun applyOffset(v: Vec3f?, offset: Vec3f?): Vec3f {
        return Vec3f(v!!.getX() + offset!!.getX(), v.getY() + offset.getY(), v.getZ() + offset.getZ())
    }

    fun getPerpendicularVector(v: Direction): Vec3f {
        return Vec3f((1 - v.offsetX).toFloat(), (1 - v.offsetY).toFloat(), (1 - v.offsetZ).toFloat())
    }

    fun toString(v: BlockPos): String {
        return "%s, %s, %s".format(v.x, v.y, v.z)
    }

    @JvmStatic
    fun toString(v: TargetBlockPos): String {
        return v.toString()
    }
}
