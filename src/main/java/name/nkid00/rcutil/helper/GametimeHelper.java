package name.nkid00.rcutil.helper;

import net.minecraft.server.MinecraftServer;

public class GametimeHelper {
    private static long gametime;

    public static synchronized void onTickStart(MinecraftServer server) {
        // START_SERVER_TICK is called before world time increases
        gametime = server.getOverworld().getTime() + 1;
    }

    public static synchronized long gametime() {
        return gametime;
    }
}
