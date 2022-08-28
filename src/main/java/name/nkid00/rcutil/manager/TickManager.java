package name.nkid00.rcutil.manager;

import name.nkid00.rcutil.helper.GametimeHelper;
import name.nkid00.rcutil.io.ScriptServerIO;
import name.nkid00.rcutil.script.ScriptEventCallback;
import net.minecraft.server.MinecraftServer;

public class TickManager {
    public static void onTickStart(MinecraftServer server) {
        GametimeHelper.onTickStart(server);
        ScriptEventCallback.onGametickStart();
    }

    public static void onTickEnd(MinecraftServer server) {
        ScriptEventCallback.onGametickEnd();
        ScriptServerIO.sync();
    }
}
