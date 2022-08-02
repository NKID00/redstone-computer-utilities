package name.nkid00.rcutil.helper;

import name.nkid00.rcutil.exception.BlockNotTargetException;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

public class TargetBlockHelper {
    public static boolean isTargetBlock(ServerWorld world, BlockPos pos) {
        return world.getBlockState(pos).isOf(Blocks.TARGET);
    }

    public static int readTargetBlockPower(ServerWorld world, BlockPos pos) throws BlockNotTargetException {
        if (!isTargetBlock(world, pos)) {
            throw new BlockNotTargetException();
        }
        return world.getReceivedRedstonePower(pos);
    }

    public static boolean readDigitalTargetBlockPower(ServerWorld world, BlockPos pos) throws BlockNotTargetException {
        return readTargetBlockPower(world, pos) > 0;
    }

    public static void writeTargetBlockPower(ServerWorld world, BlockPos pos, int power)
            throws BlockNotTargetException {
        if (!isTargetBlock(world, pos)) {
            throw new BlockNotTargetException();
        }
        world.setBlockState(pos, world.getBlockState(pos).with(Properties.POWER, power), Block.NOTIFY_ALL);
    }

    public static void writeDigitalTargetBlockPower(ServerWorld world, BlockPos pos, boolean power)
            throws BlockNotTargetException {
        writeTargetBlockPower(world, pos, power ? 15 : 0);
    }
}
