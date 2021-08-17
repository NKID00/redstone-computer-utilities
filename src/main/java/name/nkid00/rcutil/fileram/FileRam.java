package name.nkid00.rcutil.fileram;

import java.io.File;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import name.nkid00.rcutil.enumeration.FileRamEdgeTriggering;
import name.nkid00.rcutil.enumeration.FileRamType;

public class FileRam {
    public String name = null;
    public MutableText fancyName = null;

    public FileRamType type = null;

    public BlockPos addressBase = null;
    public Vec3i addressGap = null;  // including one end
    public int addressSize = 0;

    public BlockPos dataBase = null;
    public Vec3i dataGap = null;  // including one end
    public int dataSize = 0;

    public BlockPos clock = null;
    public FileRamEdgeTriggering clockEdgeTriggering = null;

    public String filename = null;
    public File file = null;

    public boolean running = false;

    public void buildFancyName() {
        TranslatableText typeText = type.toText(), edgeText = clockEdgeTriggering.toText();
        fancyName = new TranslatableText("rcutil.fileram.fancyname", name, typeText, edgeText, filename, addressSize, dataSize);
    }

    public void tick(ServerWorld world) {
        // TODO: Implement
    }

    public void setRunning(boolean v) {
        if (running != v) {
            running = v;
            if (v) {
                fancyName.setStyle(Style.EMPTY.withBold(true).withUnderline(true));
            } else {
                fancyName.setStyle(Style.EMPTY);
            }
        }
    }
}
