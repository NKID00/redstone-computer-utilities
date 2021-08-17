package name.nkid00.rcutil;

import net.fabricmc.api.DedicatedServerModInitializer;

public class DedicatedServerUtil implements DedicatedServerModInitializer {
    public static boolean isDedicatedServer = false;

    public void onInitializeServer() {
        isDedicatedServer = true;
    }
}
