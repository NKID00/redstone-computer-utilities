package name.nkid00.rcutil.manager;

import name.nkid00.rcutil.helper.GametimeHelper;
import name.nkid00.rcutil.io.ScriptServerIO;
import name.nkid00.rcutil.script.ScriptEventCallback;
import net.minecraft.server.MinecraftServer;

public class TickManager {
    public static void onTickStart(MinecraftServer server) {
        if (!GametimeHelper.isFrozen(server)) {
            // before next gametick start, equivalent to this gametick end
            ScriptEventCallback.onGametickEnd();
            GametimeHelper.updateGametime(server);
            ScriptServerIO.sync();
            ScriptEventCallback.onGametickStart();
        }
    }
}
