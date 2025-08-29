package me.nk0.rcu.manager;

import me.nk0.rcu.event.AlarmEvent;
import me.nk0.rcu.event.AlarmEvent.At;
import me.nk0.rcu.helper.GametimeHelper;
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
