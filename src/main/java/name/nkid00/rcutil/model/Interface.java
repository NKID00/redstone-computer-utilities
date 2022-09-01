package name.nkid00.rcutil.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import name.nkid00.rcutil.exception.BlockNotTargetException;
import name.nkid00.rcutil.helper.PosHelper;
import name.nkid00.rcutil.helper.DataHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.helper.ParticleHelper;
import name.nkid00.rcutil.helper.TargetBlockHelper;
import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.script.ScriptEventCallback;
import name.nkid00.rcutil.util.Blocks;
import name.nkid00.rcutil.util.Enumerate;
import name.nkid00.rcutil.util.TargetBlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class Interface implements Iterable<TargetBlockPos> {
    private final String name;
    private final ServerWorld world;
    private final Blocks blocks;
    private BitSet lastValue;

    public Interface(String name, ServerWorld world, BlockPos lsb, Vec3i increment, int size) {
        this.name = name;
        this.world = world;
        blocks = new Blocks(lsb, increment, size);
        lastValue = readSuppress();
    }

    public Interface(String name, ServerWorld world, Blocks blocks) {
        this.name = name;
        this.world = world;
        this.blocks = PosHelper.copy(blocks);
        lastValue = readSuppress();
    }

    public static Interface singleBit(UUID uuid, String name, ServerWorld world, BlockPos pos)
            throws BlockNotTargetException {
        var result = new Interface(name, world, Blocks.singleBlock(pos));
        if (!result.valid()) {
            throw new BlockNotTargetException(I18n.t(uuid, "rcutil.command.rcu_new.fail.selection_incomplete"));
        }
        return result;
    }

    public static Interface resolve(UUID uuid, String name, ServerWorld world, BlockPos lsb, BlockPos msb)
            throws BlockNotTargetException {
        if (lsb.equals(msb)) {
            return Interface.singleBit(uuid, name, world, lsb);
        }
        var blocks = new Blocks(lsb, msb);
        var targetBlockCount = 0;
        for (var pos : blocks) {
            if (TargetBlockHelper.is(world, pos)) {
                targetBlockCount++;
            }
        }
        var second = msb;
        for (var pos : new Blocks(PosHelper.applyOffset(blocks.first(), blocks.increment()),
                blocks.increment(), blocks.size() - 1)) {
            if (TargetBlockHelper.is(world, pos)) {
                second = pos;
                break;
            }
        }
        var result = new Interface(name, world, new Blocks(lsb, second, msb));
        if ((!result.valid()) || result.size() != targetBlockCount) {
            throw new BlockNotTargetException(I18n.t(uuid, "rcutil.command.rcu_new.fail.selection_incomplete"));
        }
        return result;
    }

    public boolean valid() {
        for (var pos : this) {
            if (!pos.valid()) {
                return false;
            }
        }
        return true;
    }

    public BitSet read() throws BlockNotTargetException {
        var bits = new BitSet(size());
        for (var ipos : new Enumerate<>(this)) {
            bits.set(ipos.index(), ipos.object().readDigital());
        }
        return bits;
    }

    public BitSet readSuppress() {
        var bits = new BitSet(size());
        for (var ipos : new Enumerate<>(this)) {
            bits.set(ipos.index(), ipos.object().readDigitalOrZero());
        }
        return bits;
    }

    public void write(BitSet value) throws BlockNotTargetException {
        for (var ipos : new Enumerate<>(this)) {
            ipos.object().writeDigital(value.get(ipos.index()));
        }
    }

    public void writeSuppress(BitSet value) {
        for (var ipos : new Enumerate<>(this)) {
            ipos.object().writeDigitalSuppress(value.get(ipos.index()));
        }
    }

    public void targetBlockNeighborUpdate(BlockPos pos, int index) {
        var lastBit = lastValue.get(index);
        var newBit = TargetBlockHelper.readDigitalUnsafe(world, pos);
        if (lastBit != newBit) {
            lastValue = readSuppress();
            ScriptEventCallback.onInterfaceUpdateImmediate(this);
            InterfaceManager.markUpdated(this);
        }
    }

    public Text info(UUID uuid) {
        return I18n.t(uuid, "rcutil.info.interface.detail", name, lsb(), size());
    }

    public void highlight(ServerPlayerEntity viewer) {
        if (size() == 1) {
            ParticleHelper.highlight(world, viewer, DataHelper.HSV2RGBVec3f(
                    304f, 1.0f, 1.0f), blocks.first());
        } else {
            for (var ipos : new Enumerate<>(this)) {
                var v = ((float) ipos.index()) / (size() - 1);
                ParticleHelper.highlight(world, viewer, DataHelper.HSV2RGBVec3f(
                        DataHelper.linearMap(225f, 306f, v),
                        DataHelper.linearMap(0.6f, 1.0f, v),
                        1.0f), ipos.object());
            }
        }
    }

    public TargetBlockPos get(int index) {
        return new TargetBlockPos(world, blocks.get(index));
    }

    public TargetBlockPos lsb() {
        return new TargetBlockPos(world, blocks.first());
    }

    public Vec3i increment() {
        return blocks.increment();
    }

    public int size() {
        return blocks.size();
    }

    public String name() {
        return name;
    }

    public ServerWorld world() {
        return world;
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
    public int hashCode() {
        // some random prime number
        int result = 31 + (name == null ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Interface) {
            var other = (Interface) obj;
            if (name == null ? other.name != null : !name.equals(other.name)) {
                return false;
            }
            return true;
        }
        return false;
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

    @Override
    public String toString() {
        return name;
    }
}
