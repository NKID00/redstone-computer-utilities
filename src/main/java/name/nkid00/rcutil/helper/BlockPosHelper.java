package name.nkid00.rcutil.helper;

import java.util.Iterator;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class BlockPosHelper {
    public static Vec3i toVec3i(BlockPos v) {
        return new Vec3i(v.getX(), v.getY(), v.getZ());
    }

    public static BlockPos fromVec3i(Vec3i v) {
        return new BlockPos(v);
    }

    public static Vec3i scale(Vec3i v, int s) {
        return new Vec3i(v.getX() * s, v.getY() * s, v.getZ() * s);
    }

    public static Vec3i getOffset(BlockPos begin, BlockPos end) {
        return new Vec3i(end.getX() - begin.getX(), end.getY() - begin.getY(), end.getZ() - begin.getZ());
    }

    public static BlockPos applyOffset(BlockPos v, Vec3i offset) {
        return new BlockPos(v.getX() + offset.getX(), v.getY() + offset.getY(), v.getZ() + offset.getZ());
    }

    @Nullable
    public static BlocksInARowIterable resolveBlockPos(BlockPos lsb, BlockPos secondLsb, BlockPos msb) {
        if (lsb.equals(secondLsb)) {
            // lsb == secondLsb == msb -> size = 1
            // lsb == secondLsb != msb -> null
            return lsb.equals(msb) ? new BlocksInARowIterable(lsb, null, 1) : null;
        }
        // lsb != secondLsb && lsb == msb
        if (msb.equals(lsb)) {
            return null;
        }
        var gap = getOffset(lsb, secondLsb);
        // lsb != secondLsb && lsb != msb && secondLsb == msb -> size = 2
        if (secondLsb.equals(lsb)) {
            return new BlocksInARowIterable(lsb, gap, 2);
        }
        // lsb != secondLsb && secondLsb != msb && lsb != msb
        var offset = getOffset(lsb, msb);
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
        return size == null ? null : new BlocksInARowIterable(lsb, gap, size);
    }

    public static class IndexedBlockPos {
        public int index = 0;
        public BlockPos pos = null;
    }

    public static class BlocksInARowIterable implements Iterable<IndexedBlockPos> {
        public static class BlocksInARowIterator implements Iterator<IndexedBlockPos> {
            private BlockPos pos = null;
            private Vec3i gap = null;
            private int index = 0;
            private int size = 0;

            public BlockPos getPos() {
                return pos;
            }

            public Vec3i getGap() {
                return gap;
            }

            public int getIndex() {
                return index;
            }

            public int getSize() {
                return size;
            }

            public BlocksInARowIterator(BlockPos base, Vec3i gap, int size) {
                if (size <= 0) {
                    return;
                }
                this.pos = base;
                this.gap = gap;
                this.size = size;
            }

            public boolean hasNext() {
                return index < size;
            }

            public IndexedBlockPos next() {
                var result = new IndexedBlockPos();
                result.index = index;
                result.pos = pos;
                index++;
                if (index < size) {
                    pos = applyOffset(pos, gap);
                }
                return result;
            }
        }

        private BlocksInARowIterator iter = null;

        public BlocksInARowIterable(BlockPos base, Vec3i gap, int size) {
            this.iter = new BlocksInARowIterator(base, gap, size);
        }

        public BlocksInARowIterator iterator() {
            return iter;
        }
    }
}
