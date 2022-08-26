package name.nkid00.rcutil.model;

import java.util.BitSet;
import java.util.Iterator;

import name.nkid00.rcutil.exception.BlockNotTargetException;
import name.nkid00.rcutil.helper.BlockPosHelper;
import name.nkid00.rcutil.helper.DataHelper;
import name.nkid00.rcutil.helper.TargetBlockHelper;
import name.nkid00.rcutil.helper.TextHelper;
import name.nkid00.rcutil.util.Enumerate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class Interface implements Iterable<BlockPos> {
    private ServerWorld world;
    private BlockPos base;
    private Vec3i gap;
    private int size;

    public Interface(ServerWorld world, BlockPos base, Vec3i gap, int size) {
        this.world = world;
        this.base = base;
        this.gap = gap;
        this.size = size;
    }

    public static Interface resolve(ServerWorld world, BlockPos lsb, BlockPos msb) {
        return resolve(world, lsb, null, msb);
    }

    public static Interface resolve(ServerWorld world, BlockPos lsb, BlockPos secondLsb, BlockPos msb) {
        if (lsb.equals(secondLsb)) {
            // lsb == secondLsb == msb -> size = 1
            // lsb == secondLsb != msb -> null
            return lsb.equals(msb) ? new Interface(world, lsb, null, 1) : null;
        }
        // lsb != secondLsb && lsb == msb
        if (msb.equals(lsb)) {
            return null;
        }
        var gap = BlockPosHelper.getOffset(lsb, secondLsb);
        // lsb != secondLsb && lsb != msb && secondLsb == msb -> size = 2
        if (secondLsb.equals(lsb)) {
            return new Interface(world, lsb, gap, 2);
        }
        // lsb != secondLsb && secondLsb != msb && lsb != msb
        var offset = BlockPosHelper.getOffset(lsb, msb);
        var gx = gap.getX();
        var gy = gap.getY();
        var gz = gap.getZ();
        var ox = offset.getX();
        var oy = offset.getY();
        var oz = offset.getZ();
        Integer size = null;
        // for each axis: size = offset / gap
        // - size is not integral -> null
        // - size is not equal to the sizes of other axises -> null
        // - gap and offset: one is zero, another is non-zero -> null
        if (gx != 0 && ox != 0) {
            var t = ((float) ox) / gx;
            if (DataHelper.isFloatIntegral(t)) {
                size = (int) t;
            } else {
                return null;
            }
        } else if (gx != 0 || ox != 0) {
            return null;
        }
        if (gy != 0 && oy != 0) {
            var t = ((float) oy) / gy;
            if (DataHelper.isFloatIntegral(t)) {
                if (size == null) {
                    size = (int) t;
                } else if (!size.equals((int) t)) {
                    return null;
                }
            } else {
                return null;
            }
        } else if (gy != 0 || oy != 0) {
            return null;
        }
        if (gz != 0 && oz != 0) {
            var t = ((float) oz) / gz;
            if (DataHelper.isFloatIntegral(t)) {
                if (size == null) {
                    size = (int) t;
                } else if (!size.equals((int) t)) {
                    return null;
                }
            } else {
                return null;
            }
        } else if (gz != 0 || oz != 0) {
            return null;
        }
        return size == null ? null : new Interface(world, lsb, gap, size);
    }

    public BitSet readData() throws BlockNotTargetException {
        var bits = new BitSet(size);
        for (var pos : new Enumerate<>(this)) {
            bits.set(pos.index(), TargetBlockHelper.readDigital(world, pos.item()));
        }
        return bits;
    }

    public void writeData(BitSet bits) throws BlockNotTargetException {
        for (var pos : new Enumerate<>(this)) {
            TargetBlockHelper.writeDigital(world, pos.item(), bits.get(pos.index()));
        }
    }

    public Text text() {
        return TextHelper.empty();
    }

    @Override
    public InterfaceIterator iterator() {
        return new InterfaceIterator();
    }

    private class InterfaceIterator implements Iterator<BlockPos> {
        private BlockPos pos = base;
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public BlockPos next() {
            if (!hasNext()) {
                return null;
            }
            var result = pos;
            index++;
            if (hasNext()) {
                pos = BlockPosHelper.applyOffset(pos, gap);
            }
            return result;
        }
    }
}
