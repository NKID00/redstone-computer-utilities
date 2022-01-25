package name.nkid00.rcutil.wires;

import java.util.BitSet;

import name.nkid00.rcutil.MathUtil;
import name.nkid00.rcutil.TargetBlockUtil;
import name.nkid00.rcutil.TargetBlockUtil.NotTargetBlockException;
import name.nkid00.rcutil.exception.RCUtilException;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.dimension.DimensionType;

public class Wires {
    public class BrokenWiresException extends RCUtilException { }

    public String name = null;

    public DimensionType dimensionType = null;
    public BlockPos base = null;
    public Vec3i gap = null;
    public int size = 0;

    public boolean running = false;

    public BitSet readData(ServerWorld world) throws NotTargetBlockException {
        var bits = new BitSet(size);
        var isBroken = new Object(){ boolean value = false; };
        MathUtil.foreachBlock(base, gap, size, (i, pos) -> {
            if (isBroken.value) {
                return;
            }
            try {
                bits.set(i, TargetBlockUtil.readDigitalTargetBlockPower(world, pos));
            } catch (NotTargetBlockException e) {
                isBroken.value = true;
            }
        });
        if (isBroken.value) {
            throw new NotTargetBlockException();
        }
        return bits;
    }

    public void writeData(ServerWorld world, BitSet bits) throws NotTargetBlockException {
        var isBroken = new Object(){ boolean value = false; };
        MathUtil.foreachBlock(base, gap, size, (i, pos) -> {
            if (isBroken.value) {
                return;
            }
            try {
                TargetBlockUtil.writeDigitalTargetBlockPower(world, pos, bits.get(i));
            } catch (NotTargetBlockException e) {
                isBroken.value = true;
            }
        });
        if (isBroken.value) {
            throw new NotTargetBlockException();
        }
    }
}
