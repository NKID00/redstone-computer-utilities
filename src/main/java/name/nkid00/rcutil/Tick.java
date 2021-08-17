package name.nkid00.rcutil;

import net.minecraft.server.world.ServerWorld;

public class Tick {
    public static void register(ServerWorld world) {
        RCUtil.fileRams.forEach((k, v) -> {
            v.tick(world);
        });
    }
}
