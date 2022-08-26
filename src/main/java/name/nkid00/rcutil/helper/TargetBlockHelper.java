package name.nkid00.rcutil.helper;

import name.nkid00.rcutil.exception.BlockNotTargetException;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class TargetBlockHelper {
    public static boolean is(ServerWorld world, BlockPos pos) {
        return world.getBlockState(pos).isOf(Blocks.TARGET);
    }

    public static void check(ServerWorld world, BlockPos pos) throws BlockNotTargetException {
        if (!is(world, pos)) {
            throw new BlockNotTargetException();
        }
    }

    public static void check(ServerWorld world, BlockPos pos, String message)
            throws BlockNotTargetException {
        if (!is(world, pos)) {
            throw new BlockNotTargetException(message);
        }
    }

    public static void check(ServerWorld world, BlockPos pos, Text message)
            throws BlockNotTargetException {
        if (!is(world, pos)) {
            throw new BlockNotTargetException(message);
        }
    }

    public static int read(ServerWorld world, BlockPos pos) throws BlockNotTargetException {
        if (!is(world, pos)) {
            throw new BlockNotTargetException();
        }
        return world.getReceivedRedstonePower(pos);
    }

    public static boolean readDigital(ServerWorld world, BlockPos pos) throws BlockNotTargetException {
        return read(world, pos) > 0;
    }

    public static void write(ServerWorld world, BlockPos pos, int power)
            throws BlockNotTargetException {
        if (!is(world, pos)) {
            throw new BlockNotTargetException();
        }
        world.setBlockState(pos, world.getBlockState(pos).with(Properties.POWER, power), Block.NOTIFY_ALL);
    }

    public static void writeDigital(ServerWorld world, BlockPos pos, boolean power)
            throws BlockNotTargetException {
        write(world, pos, power ? 15 : 0);
    }
}
