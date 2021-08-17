package name.nkid00.rcutil.rambus;

import java.io.IOException;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3i;

import name.nkid00.rcutil.MathUtil;

public class WoRamBusBuilder extends RamBusBuilder {
    @Override
    public boolean setFile(String filename) throws IOException {
        if (!super.setFile(filename)) {
            file.createNewFile();  // create if file does not exist
        }
        return true;
    }

    public void buildFancyName() {
        fancyName = new TranslatableText("rcutil.fileram.fancyname.builder", name, new TranslatableText("rcutil.fileram.fancyname.wo"), clockEdgeTriggering.getText(), filename);
    }

    @Override
    public BuildStatus buildAddress(ServerWorld world) {
        ram = new WoRamBus();
        return super.buildAddress(world);
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

        // test if there is non-redstone-dust block in the bus
        for (int i = 2; i < size; i++) {
            if (!world.getBlockState(MathUtil.applyOffset(ram.dataBase, MathUtil.scale(ram.dataGap, i))).isOf(Blocks.REDSTONE_WIRE)) {
                return BuildStatus.FailedWrongBlock;
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
}
