package name.nkid00.rcutil.helper;

import net.minecraft.server.MinecraftServer;

public class GametimeHelper {
    private static long gametime;

    public static synchronized boolean isFrozen(MinecraftServer server) {
        return gametime == server.getOverworld().getTime() + 1;
    }

    public static synchronized void updateGametime(MinecraftServer server) {
        // START_SERVER_TICK is called before world time increases
        gametime = server.getOverworld().getTime() + 1;
    }

    public static synchronized long gametime() {
        return gametime;
    }
}
