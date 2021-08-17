package name.nkid00.rcutil.rambus;

import java.io.File;

import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;

import name.nkid00.rcutil.RCUtil;
import name.nkid00.rcutil.enumeration.EdgeTriggering;

public abstract class RamBusBuilder {
    public String name;
    public MutableText fancyName;
    public BlockPos addressLeastSignificantBit;
    public BlockPos address2ndLeastSignificantBit;
    public BlockPos addressMostSignificantBit;
    public BlockPos dataLeastSignificantBit;
    public BlockPos data2ndLeastSignificantBit;
    public BlockPos dataMostSignificantBit;
    public BlockPos clock;
    public EdgeTriggering clockEdgeTriggering;
    public String filename;
    public File file;

    public boolean setFile(String path) {
        file = new File(RCUtil.baseDirectory, path);
        return file.exists();
    }
    public abstract RamBus build();
}
