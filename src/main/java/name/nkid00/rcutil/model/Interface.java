package name.nkid00.rcutil.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import name.nkid00.rcutil.exception.BlockNotTargetException;
import name.nkid00.rcutil.helper.BlockPosHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.helper.TargetBlockHelper;
import name.nkid00.rcutil.util.Blocks;
import name.nkid00.rcutil.util.Enumerate;
import name.nkid00.rcutil.util.TargetBlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class Interface implements Iterable<TargetBlockPos> {
    public final String name;
    private final ServerWorld world;
    private final Blocks blocks;

    public Interface(String name, ServerWorld world, BlockPos lsb, Vec3i increment, int size) {
        this.name = name;
        this.world = world;
        blocks = new Blocks(lsb, increment, size);
    }

    public Interface(String name, ServerWorld world, Blocks blocks) {
        this.name = name;
        this.world = world;
        this.blocks = BlockPosHelper.copy(blocks);
    }

    public static Interface singleBit(String name, ServerWorld world, BlockPos pos) {
        var result = new Interface(name, world, Blocks.singleBlock(pos));
        if (result.valid()) {
            return result;
        } else {
            return null;
        }
    }

    public static Interface resolve(String name, ServerWorld world, BlockPos lsb, BlockPos msb) {
        if (lsb.equals(msb)) {
            return Interface.singleBit(name, world, lsb);
        }
        var blocks = new Blocks(lsb, msb);
        var targetBlockCount = 0;
        for (var pos : blocks) {
            if (TargetBlockHelper.is(world, pos)) {
                targetBlockCount++;
            }
        }
        var second = msb;
        for (var pos : new Blocks(BlockPosHelper.applyOffset(blocks.first(), blocks.increment()),
                blocks.increment(), blocks.size() - 1)) {
            if (TargetBlockHelper.is(world, pos)) {
                second = pos;
                break;
            }
        }
        var result = new Interface(name, world, new Blocks(lsb, second, msb));
        if (result.valid() && result.size() == targetBlockCount) {
            return result;
        } else {
            return null;
        }
    }

    public boolean valid() {
        for (var pos : this) {
            if (!pos.valid()) {
                return false;
            }
        }
        return true;
    }

    public BitSet readData() throws BlockNotTargetException {
        var bits = new BitSet(size());
        for (var ipos : new Enumerate<>(this)) {
            bits.set(ipos.index(), ipos.item().readDigital());
        }
        return bits;
    }

    public void writeData(BitSet bits) throws BlockNotTargetException {
        for (var ipos : new Enumerate<>(this)) {
            ipos.item().writeDigital(bits.get(ipos.index()));
        }
    }

    public Text info(UUID uuid) {
        return I18n.t(uuid, "rcutil.info.interface.detail", name, first(), size());
    }

    public TargetBlockPos get(int index) {
        return new TargetBlockPos(world, blocks.get(index));
    }

    public TargetBlockPos first() {
        return new TargetBlockPos(world, blocks.first());
    }

    public Vec3i increment() {
        return blocks.increment();
    }

    public int size() {
        return blocks.size();
    }

    public List<TargetBlockPos> toList() {
        var result = new ArrayList<TargetBlockPos>(size());
        forEach(result::add);
        return result;
    }

    public TargetBlockPos[] toArray() {
        return toList().toArray(new TargetBlockPos[0]);
    }

    @Override
    public Iterator<TargetBlockPos> iterator() {
        return new InterfaceIterator();
    }

    class InterfaceIterator implements Iterator<TargetBlockPos> {
        Iterator<BlockPos> iterator = blocks.iterator();

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public TargetBlockPos next() {
            return new TargetBlockPos(world, iterator.next());
        }
    }
}
