package name.nkid00.rcutil.rambus;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3i;

import name.nkid00.rcutil.MathUtil;

public class RoRamBusBuilder extends RamBusBuilder {
    public void buildFancyName() {
        fancyName = new TranslatableText("rcutil.fileram.fancyname.builder", name, new TranslatableText("rcutil.fileram.fancyname.ro"), clockEdgeTriggering.getText(), filename);
    }

    @Override
    public BuildStatus buildAddress(ServerWorld world) {
        ram = new RoRamBus();
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
