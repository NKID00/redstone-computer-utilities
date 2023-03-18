package name.nkid00.rcutil.manager;

import name.nkid00.rcutil.event.AlarmEvent;
import name.nkid00.rcutil.event.AlarmEvent.At;
import name.nkid00.rcutil.helper.GametimeHelper;
import net.minecraft.server.MinecraftServer;

public class GametimeManager {
    public static void gametickStart(MinecraftServer server) {
        if (GametimeHelper.isFrozen(server)) {
            return;
        }
        // before this gametick start, equivalent to previous gametick end
        new AlarmEvent(GametimeHelper.gametime(), At.End).broadcast();
        GametimeHelper.updateGametime(server);
        ScriptManager.initializeScripts();
        new AlarmEvent(GametimeHelper.gametime(), At.Start).broadcast();
    }
}
