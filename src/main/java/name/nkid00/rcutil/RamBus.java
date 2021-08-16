package name.nkid00.rcutil;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public abstract class RamBus {
    public BlockPos addressBase;
    public Vec3i addressGap;
    public int addressSize;
    public BlockPos dataBase;
    public Vec3i dataGap;
    public int dataSize;
    public BlockPos clock;
    public EdgeTriggering clockEdgeTriggering;
}
