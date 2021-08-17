package name.nkid00.rcutil.rambus;

import java.io.File;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import name.nkid00.rcutil.enumeration.EdgeTriggering;

public abstract class RamBus {
    public MutableText fancyName;

    public BlockPos addressBase;
    public Vec3i addressGap;  // including one end
    public int addressSize;

    public BlockPos dataBase;
    public Vec3i dataGap;  // including one end
    public int dataSize;

    public BlockPos clock;
    public EdgeTriggering clockEdgeTriggering;

    public String filename;
    public File file;

    public boolean running;

    public abstract void tick(ServerWorld world);
}
