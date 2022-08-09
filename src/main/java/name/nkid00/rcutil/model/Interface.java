package name.nkid00.rcutil.model;

import java.util.BitSet;

import name.nkid00.rcutil.exception.BlockNotTargetException;
import name.nkid00.rcutil.helper.BlockPosHelper;
import name.nkid00.rcutil.helper.TargetBlockHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.dimension.DimensionType;

public record Interface(DimensionType dimensionType, BlockPos base, Vec3i gap, int size) {
    public static Interface tryFromSelection(Selection selection) {
        return new Interface(null, null, null, 0);
    }

    public BitSet readData(ServerWorld world) throws BlockNotTargetException {
        var bits = new BitSet(size);
        for (var ipos : new BlockPosHelper.BlocksInARowIterable(base, gap, size)) {
            bits.set(ipos.index, TargetBlockHelper.readDigitalTargetBlockPower(world, ipos.pos));
        }
        return bits;
    }

    public void writeData(ServerWorld world, BitSet bits) throws BlockNotTargetException {
        for (var ipos : new BlockPosHelper.BlocksInARowIterable(base, gap, size)) {
            TargetBlockHelper.writeDigitalTargetBlockPower(world, ipos.pos, bits.get(ipos.index));
        }
    }
}
