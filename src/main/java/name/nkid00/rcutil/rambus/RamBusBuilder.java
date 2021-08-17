package name.nkid00.rcutil.rambus;

import java.io.File;
import java.io.IOException;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import name.nkid00.rcutil.MathUtil;
import name.nkid00.rcutil.RCUtil;
import name.nkid00.rcutil.enumeration.RamBusEdgeTriggering;
import name.nkid00.rcutil.enumeration.RamBusType;

public class RamBusBuilder {
    public String name = null;
    public MutableText fancyName = null;

    public RamBusType type = null;

    public BlockPos addressLeastSignificantBit = null;
    public BlockPos address2ndLeastSignificantBit = null;
    public BlockPos addressMostSignificantBit = null;

    public BlockPos dataLeastSignificantBit = null;
    public BlockPos data2ndLeastSignificantBit = null;
    public BlockPos dataMostSignificantBit = null;

    public BlockPos clock = null;

    public RamBusEdgeTriggering clockEdgeTriggering = null;

    public String filename = null;
    public File file = null;

    public RamBus ram = null;

    public enum BuildStatus {
        Success,
        FailedNotAligned,
        FailedWrongBlock,
        WarningNotAligned
    }

    public void buildFancyName() {
        TranslatableText typeText = type.toText(), edgeText = clockEdgeTriggering.toText();
        fancyName = new TranslatableText("rcutil.fileram.fancyname.builder", name, typeText, edgeText, filename);
    }

    public BuildStatus buildAddress(ServerWorld world) {
        if (!MathUtil.onSameLine(addressLeastSignificantBit, address2ndLeastSignificantBit, addressMostSignificantBit)) {
            return BuildStatus.FailedNotAligned;
        }

        ram = new RamBus();
        ram.type = type;

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

    public BuildStatus buildData(ServerWorld world) {
        if (!MathUtil.onSameLine(dataLeastSignificantBit, data2ndLeastSignificantBit, dataMostSignificantBit)) {
            return BuildStatus.FailedNotAligned;
        }

        ram.dataBase = dataLeastSignificantBit;
        ram.dataGap = MathUtil.getOffset(dataLeastSignificantBit, data2ndLeastSignificantBit);  // including one end
        Vec3i data = MathUtil.getOffset(dataLeastSignificantBit, dataMostSignificantBit);
        float size = 1F;
        if (data.getX() != ram.dataGap.getX()) {
            size = ((float)data.getX()) / ram.dataGap.getX();
        } else if (data.getY() != ram.dataGap.getY()) {
            size = ((float)data.getY()) / ram.dataGap.getY();
        } else if (data.getZ() != ram.dataGap.getZ()) {
            size = ((float)data.getZ()) / ram.dataGap.getZ();
        }
        ram.dataSize = (int)size;

        if (type.equals(RamBusType.WriteOnly)) {
            // test if there is non-redstone-dust block in the bus
            for (int i = 2; i < size; i++) {
                if (!world.getBlockState(MathUtil.applyOffset(ram.dataBase, MathUtil.scale(ram.dataGap, i))).isOf(Blocks.REDSTONE_WIRE)) {
                    return BuildStatus.FailedWrongBlock;
                }
            }
        }

        if (!MathUtil.isFloatIntegral(size)) {
            return BuildStatus.WarningNotAligned;
        }

        return BuildStatus.Success;
    }

    public BuildStatus build() {
        ram.clock = clock;
        ram.clockEdgeTriggering = clockEdgeTriggering;

        ram.filename = filename;
        ram.file = file;

        ram.buildFancyName();

        return BuildStatus.Success;

    }

    public boolean setFile(String filename) throws IOException {
        this.filename = filename;
        file = new File(RCUtil.baseDirectory, filename);
        return file.exists();
    }
}
