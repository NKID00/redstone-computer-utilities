package name.nkid00.rcutil.rambus;

import java.io.File;
import java.io.IOException;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import name.nkid00.rcutil.MathUtil;
import name.nkid00.rcutil.RCUtil;
import name.nkid00.rcutil.enumeration.EdgeTriggering;

public abstract class RamBusBuilder {
    public String name = null;
    public MutableText fancyName = null;
    public BlockPos addressLeastSignificantBit = null;
    public BlockPos address2ndLeastSignificantBit = null;
    public BlockPos addressMostSignificantBit = null;
    public BlockPos dataLeastSignificantBit = null;
    public BlockPos data2ndLeastSignificantBit = null;
    public BlockPos dataMostSignificantBit = null;
    public BlockPos clock = null;
    public EdgeTriggering clockEdgeTriggering = null;
    public String filename = null;
    public File file = null;

    public RamBus ram = null;

    public enum BuildStatus {
        Success,
        FailedNotAligned,
        FailedWrongBlock,
        WarningNotAligned
    }
    public abstract void buildFancyName();
    public abstract BuildStatus buildData(ServerWorld world);
    public abstract BuildStatus build();

    public boolean setFile(String filename) throws IOException {
        this.filename = filename;
        file = new File(RCUtil.baseDirectory, filename);
        return file.exists();
    }

    public BuildStatus buildAddress(ServerWorld world) {
        if (!MathUtil.onSameLine(addressLeastSignificantBit, address2ndLeastSignificantBit, addressMostSignificantBit)) {
            return BuildStatus.FailedNotAligned;
        }

        ram.addressBase = addressLeastSignificantBit;
        ram.addressGap = MathUtil.getOffset(addressLeastSignificantBit, address2ndLeastSignificantBit);  // including one end
        Vec3i addr = MathUtil.getOffset(addressLeastSignificantBit, addressMostSignificantBit);
        float size = 1F;
        if (addr.getX() != ram.addressGap.getX()) {
            size = ((float)addr.getX()) / ram.addressGap.getX();
        } else if (addr.getY() != ram.addressGap.getY()) {
            size = ((float)addr.getY()) / ram.addressGap.getY();
        } else if (addr.getZ() != ram.addressGap.getZ()) {
            size = ((float)addr.getZ()) / ram.addressGap.getZ();
        }
        ram.addressSize = (int)size;

        // test if there is non-redstone-dust block in the bus
        for (int i = 2; i < size; i++) {
            if (!world.getBlockState(MathUtil.applyOffset(ram.addressBase, MathUtil.scale(ram.addressGap, i))).isOf(Blocks.REDSTONE_WIRE)) {
                return BuildStatus.FailedWrongBlock;
            }
        }

        if (!MathUtil.isFloatIntegral(size)) {
            return BuildStatus.WarningNotAligned;
        }

        return BuildStatus.Success;
    }
}
