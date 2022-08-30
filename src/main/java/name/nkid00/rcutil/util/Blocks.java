package name.nkid00.rcutil.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.primitives.Ints;

import name.nkid00.rcutil.helper.PosHelper;
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
            var offset = PosHelper.getOffset(first, last);
            var nonzeroPositive = List.of(offset.getX(), offset.getY(), offset.getZ()).stream()
                    .filter(v -> v != 0)
                    .map(Math::abs)
                    .toList();
            var gcd = DataHelper.gcd(Ints.toArray(nonzeroPositive));
            this.first = first;
            increment = PosHelper.divide(offset, gcd);
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
            increment = PosHelper.getOffset(first, last);
            this.size = 2;
        } else {
            this.first = first;
            increment = PosHelper.getOffset(first, second);
            if (increment.getX() != 0) {
                this.size = (PosHelper.getOffset(first, last).getX() / increment.getX()) + 1;
            } else if (increment.getY() != 0) {
                this.size = (PosHelper.getOffset(first, last).getY() / increment.getY()) + 1;
            } else {
                this.size = (PosHelper.getOffset(first, last).getZ() / increment.getZ()) + 1;
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
            return PosHelper.applyOffset(first, PosHelper.scale(increment, index));
        }
    }

    public BlockPos first() {
        return PosHelper.copy(first);
    }

    public Vec3i increment() {
        return PosHelper.copy(increment);
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
    public int hashCode() {
        // some random prime number
        int result = 31 + (first == null ? 0 : first.hashCode());
        result = result * 31 + (increment == null ? 0 : increment.hashCode());
        result = result * 31 + size;
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
        private BlockPos pos = PosHelper.copy(first);
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
            var result = PosHelper.copy(pos);
            index++;
            if (hasNext()) {
                pos = PosHelper.applyOffset(pos, increment);
            }
            return result;
        }
    }
}
