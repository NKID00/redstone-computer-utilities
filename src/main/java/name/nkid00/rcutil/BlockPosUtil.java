package name.nkid00.rcutil;

import java.util.Iterator;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class BlockPosUtil {
    public static Vec3i BlockPos2Vec3i(BlockPos v) {
        return new Vec3i(v.getX(), v.getY(), v.getZ());
    }

    public static BlockPos Vec3i2BlockPos(Vec3i v) {
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

    public static boolean isBlocksInARow(BlockPos v1, BlockPos v2, BlockPos v3) {
        // TODO: fix sloping lines
        Vec3i offset12 = getOffset(v1, v2);
        Vec3i offset13 = getOffset(v1, v3);
        float x, y, z;
        if (offset12.getX() == 0) {
            if (offset13.getX() == 0) {
                x = 0F;
            } else {
                return false;
            }
        } else {
            x = offset13.getX() / offset12.getX();
        }
        if (offset12.getY() == 0) {
            if (offset13.getY() == 0) {
                y = 0F;
            } else {
                return false;
            }
        } else {
            y = offset13.getY() / offset12.getY();
        }
        if (offset12.getZ() == 0) {
            if (offset13.getZ() == 0) {
                z = 0F;
            } else {
                return false;
            }
        } else {
            z = offset13.getZ() / offset12.getZ();
        }
        if ((x == 0F && y == 0F) || (x == 0F && z == 0F) || (y == 0F && z == 0F)) {
            return true;
        } else {
            return MathUtil.isFloatEqual(x, y) && MathUtil.isFloatEqual(y, z);
        }
    }

    public static class IndexedBlockPos {
        public int index = 0;
        public BlockPos pos = null;
    } 

    public static class BlocksInARowIterable implements Iterable<IndexedBlockPos> {
        public static class BlocksInARowIterator implements Iterator<IndexedBlockPos> {
            private BlockPos pos = null;
            private Vec3i gap = null;
            private int i = 0;
            private int size = 0;

            public BlocksInARowIterator(BlockPos base, Vec3i gap, int size) {
                if (size <= 0) {
                    return;
                }
                this.pos = base;
                this.gap = gap;
                this.size = size;
            }

            public boolean hasNext() {
                return i < size;
            }

            public IndexedBlockPos next() {
                var result = new IndexedBlockPos();
                result.index = i;
                result.pos = pos;
                if (i < size) {
                    pos = applyOffset(pos, gap);
                    i++;
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
