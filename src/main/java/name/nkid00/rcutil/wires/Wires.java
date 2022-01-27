package name.nkid00.rcutil.wires;

import java.util.BitSet;

import name.nkid00.rcutil.BlockPosUtil;
import name.nkid00.rcutil.TargetBlockUtil;
import name.nkid00.rcutil.TargetBlockUtil.NotTargetBlockException;
import name.nkid00.rcutil.component.Component;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.dimension.DimensionType;

public class Wires extends Component {
    public DimensionType dimensionType = null;
    public BlockPos base = null;
    public Vec3i gap = null;
    public int size = 0;

    public Component.ComponentStatus status = null;

    public BitSet readData(ServerWorld world) throws NotTargetBlockException {
        var bits = new BitSet(size);
        for (var ipos : new BlockPosUtil.BlocksInARowIterable(base, gap, size)) {
            bits.set(ipos.index, TargetBlockUtil.readDigitalTargetBlockPower(world, ipos.pos));
        }
        return bits;
    }

    public void writeData(ServerWorld world, BitSet bits) throws NotTargetBlockException {
        for (var ipos : new BlockPosUtil.BlocksInARowIterable(base, gap, size)) {
            TargetBlockUtil.writeDigitalTargetBlockPower(world, ipos.pos, bits.get(ipos.index));
        }
    }
}
