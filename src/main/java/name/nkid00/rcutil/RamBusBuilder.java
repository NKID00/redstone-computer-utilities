package name.nkid00.rcutil;

import net.minecraft.util.math.BlockPos;

public class RamBusBuilder {
    public BlockPos addressLeastSignificantBit;
    public BlockPos address2ndLeastSignificantBit;
    public BlockPos addressMostSignificantBit;
    public BlockPos dataLeastSignificantBit;
    public BlockPos data2ndLeastSignificantBit;
    public BlockPos dataMostSignificantBit;
    public BlockPos clock;
    public EdgeTriggering clockEdgeTriggering;
}
