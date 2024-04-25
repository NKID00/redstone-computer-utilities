package name.nkid00.rcutil.helper

import name.nkid00.rcutil.exception.BlockNotTargetException
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

object TargetBlockHelper {
    @JvmStatic
    fun `is`(world: ServerWorld, pos: BlockPos?): Boolean {
        return world.getBlockState(pos).isOf(Blocks.TARGET)
    }

    @Throws(BlockNotTargetException::class)
    fun check(world: ServerWorld, pos: BlockPos?) {
        if (!`is`(world, pos)) {
            throw BlockNotTargetException()
        }
    }

    @Throws(BlockNotTargetException::class)
    fun check(world: ServerWorld, pos: BlockPos?, message: String?) {
        if (!`is`(world, pos)) {
            throw BlockNotTargetException(message)
        }
    }

    @JvmStatic
    @Throws(BlockNotTargetException::class)
    fun check(world: ServerWorld, pos: BlockPos?, message: Text?) {
        if (!`is`(world, pos)) {
            throw BlockNotTargetException(message)
        }
    }

    @JvmStatic
    @Throws(BlockNotTargetException::class)
    fun read(world: ServerWorld, pos: BlockPos?): Int {
        if (!`is`(world, pos)) {
            throw BlockNotTargetException()
        }
        return world.getReceivedRedstonePower(pos)
    }

    @JvmStatic
    @Throws(BlockNotTargetException::class)
    fun readDigital(world: ServerWorld, pos: BlockPos?): Boolean {
        return read(world, pos) > 0
    }

    @JvmStatic
    fun readOrZero(world: ServerWorld, pos: BlockPos?): Int {
        if (!`is`(world, pos)) {
            return 0
        }
        return world.getReceivedRedstonePower(pos)
    }

    @JvmStatic
    fun readDigitalOrZero(world: ServerWorld, pos: BlockPos?): Boolean {
        return readOrZero(world, pos) > 0
    }

    @JvmStatic
    fun readUnsafe(world: ServerWorld, pos: BlockPos?): Int {
        return world.getReceivedRedstonePower(pos)
    }

    @JvmStatic
    fun readDigitalUnsafe(world: ServerWorld, pos: BlockPos?): Boolean {
        return readUnsafe(world, pos) > 0
    }

    @JvmStatic
    @Throws(BlockNotTargetException::class)
    fun write(world: ServerWorld, pos: BlockPos?, power: Int) {
        if (!`is`(world, pos)) {
            throw BlockNotTargetException()
        }
        world.setBlockState(pos, world.getBlockState(pos).with(Properties.POWER, power), Block.NOTIFY_ALL)
    }

    @JvmStatic
    @Throws(BlockNotTargetException::class)
    fun writeDigital(world: ServerWorld, pos: BlockPos?, power: Boolean) {
        write(world, pos, if (power) 15 else 0)
    }

    @JvmStatic
    fun writeSuppress(world: ServerWorld, pos: BlockPos?, power: Int) {
        if (!`is`(world, pos)) {
            return
        }
        world.setBlockState(pos, world.getBlockState(pos).with(Properties.POWER, power), Block.NOTIFY_ALL)
    }

    @JvmStatic
    fun writeDigitalSuppress(world: ServerWorld, pos: BlockPos?, power: Boolean) {
        writeSuppress(world, pos, if (power) 15 else 0)
    }

    @JvmStatic
    fun writeUnsafe(world: ServerWorld, pos: BlockPos?, power: Int) {
        world.setBlockState(pos, world.getBlockState(pos).with(Properties.POWER, power), Block.NOTIFY_ALL)
    }

    @JvmStatic
    fun writeDigitalUnsafe(world: ServerWorld, pos: BlockPos?, power: Boolean) {
        writeUnsafe(world, pos, if (power) 15 else 0)
    }
}
