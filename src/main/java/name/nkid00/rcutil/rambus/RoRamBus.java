package name.nkid00.rcutil.rambus;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;

public class RoRamBus extends RamBus {
    public void buildFancyName() {
        fancyName = new TranslatableText("rcutil.fileram.fancyname", name, new TranslatableText("rcutil.fileram.fancyname.ro"), clockEdgeTriggering.getText(), filename, addressSize, dataSize);
    }

    public void tick(ServerWorld world) {

    }
}
