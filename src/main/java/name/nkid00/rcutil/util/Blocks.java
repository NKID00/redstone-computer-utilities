package name.nkid00.rcutil.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.primitives.Ints;

import name.nkid00.rcutil.helper.BlockPosHelper;
import name.nkid00.rcutil.helper.DataHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class Blocks implements Iterable<BlockPos> {
    private final BlockPos first;
    private final Vec3i increment;
    private final int size;

    public Blocks(BlockPos first, Vec3i increment, int size) {
        this.first = first;
        this.increment = increment;
        this.size = size;
    }

    public Blocks(BlockPos first, BlockPos last) {
        if (first.equals(last)) {
            this.first = first;
            increment = null;
            size = 1;
        } else {
            var offset = BlockPosHelper.getOffset(first, last);
            var nonzeroPositive = List.of(offset.getX(), offset.getY(), offset.getZ()).stream()
                    .filter(v -> v != 0)
                    .map(Math::abs)
                    .toList();
            var gcd = DataHelper.gcd(Ints.toArray(nonzeroPositive));
            this.first = first;
            increment = BlockPosHelper.divide(offset, gcd);
            size = gcd + 1;
        }
    }

    public Blocks(BlockPos first, BlockPos second, BlockPos last) {
        if (first.equals(last) || first.equals(second)) {
            this.first = first;
            increment = null;
            this.size = 1;
        } else if (second.equals(last)) {
            this.first = first;
            increment = BlockPosHelper.getOffset(first, last);
            this.size = 2;
        } else {
            this.first = first;
            increment = BlockPosHelper.getOffset(first, second);
            if (increment.getX() != 0) {
                this.size = (BlockPosHelper.getOffset(first, last).getX() / increment.getX()) + 1;
            } else if (increment.getY() != 0) {
                this.size = (BlockPosHelper.getOffset(first, last).getY() / increment.getY()) + 1;
            } else {
                this.size = (BlockPosHelper.getOffset(first, last).getZ() / increment.getZ()) + 1;
            }
        }
    }

    public static Blocks singleBlock(BlockPos pos) {
        return new Blocks(pos, null, 1);
    }

    public BlockPos get(int index) {
        if (index >= size) {
            return null;
        } else if (index == 0) {
            return first;
        } else {
            return BlockPosHelper.applyOffset(first, BlockPosHelper.scale(increment, index));
        }
    }

    public BlockPos first() {
        return BlockPosHelper.copy(first);
    }

    public Vec3i increment() {
        return BlockPosHelper.copy(increment);
    }

    public int size() {
        return size;
    }

    public List<BlockPos> toList() {
        var result = new ArrayList<BlockPos>(size());
        forEach(result::add);
        return result;
    }

    public BlockPos[] toArray() {
        return toList().toArray(new BlockPos[0]);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Blocks) {
            var other = (Blocks) obj;
            if (first == null ? other.first != null : !first.equals(other.first)) {
                return false;
            }
            if (increment == null ? other.increment != null : !increment.equals(other.increment)) {
                return false;
            }
            if (size != other.size) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public Iterator<BlockPos> iterator() {
        return new BlocksIterator();
    }

    private class BlocksIterator implements Iterator<BlockPos> {
        private BlockPos pos = BlockPosHelper.copy(first);
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
            var result = BlockPosHelper.copy(pos);
            index++;
            if (hasNext()) {
                pos = BlockPosHelper.applyOffset(pos, increment);
            }
            return result;
        }
    }
}
