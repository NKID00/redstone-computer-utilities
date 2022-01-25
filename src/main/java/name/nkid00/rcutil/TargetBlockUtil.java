package name.nkid00.rcutil;

import name.nkid00.rcutil.exception.RCUtilException;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

public class TargetBlockUtil {
    public static class NotTargetBlockException extends RCUtilException { }

    public static boolean isTargetBlock(ServerWorld world, BlockPos pos) {
        return world.getBlockState(pos).isOf(Blocks.TARGET);
    }

    public static int readTargetBlockPower(ServerWorld world, BlockPos pos) throws NotTargetBlockException {
        if (!isTargetBlock(world, pos)) {
            throw new NotTargetBlockException();
        }
        return world.getBlockState(pos).get(Properties.POWER);
    }

    public static boolean readDigitalTargetBlockPower(ServerWorld world, BlockPos pos) throws NotTargetBlockException {
        return readTargetBlockPower(world, pos) > 0;
    }

    public static void writeTargetBlockPower(ServerWorld world, BlockPos pos, int power) throws NotTargetBlockException {
        if (!isTargetBlock(world, pos)) {
            throw new NotTargetBlockException();
        }
        world.setBlockState(pos, world.getBlockState(pos).with(Properties.POWER, power), Block.NOTIFY_ALL);
    }

    public static void writeDigitalTargetBlockPower(ServerWorld world, BlockPos pos, boolean power) throws NotTargetBlockException {
        writeTargetBlockPower(world, pos, power ? 15 : 0);
    }
}
