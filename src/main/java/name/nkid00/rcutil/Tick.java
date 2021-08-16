package name.nkid00.rcutil;

import net.minecraft.server.world.ServerWorld;

public class Tick {
    public static void register(ServerWorld world) {
        RCUtil.roRams.forEach((k, v) -> {
            v.tick(world);
        });
        RCUtil.woRams.forEach((k, v) -> {
            v.tick(world);
        });
    }
}
