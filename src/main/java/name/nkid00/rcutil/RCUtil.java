package name.nkid00.rcutil;

import name.nkid00.rcutil.helper.GametimeHelper;
import name.nkid00.rcutil.helper.WorldHelper;
import name.nkid00.rcutil.manager.CommandManager;
import name.nkid00.rcutil.manager.StorageManager;
import name.nkid00.rcutil.manager.WandManager;
import name.nkid00.rcutil.script.ScriptEvent;
import name.nkid00.rcutil.server.ApiServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;

public class RCUtil implements ModInitializer {
    public static boolean isDedicatedServer = false;

    @Override
    public void onInitialize() {
        var loader = FabricLoader.getInstance();
        isDedicatedServer = loader.getEnvironmentType() == EnvType.SERVER;

        ServerLifecycleEvents.SERVER_STARTING.register(WorldHelper::init);
        ServerLifecycleEvents.SERVER_STARTING.register(Options::init);

        // worlds is required to load selections
        ServerLifecycleEvents.SERVER_STARTED.register(StorageManager::init);

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (!GametimeHelper.isFrozen(server)) {
                // before this gametick start, equivalent to previous gametick end
                ScriptEvent.onGametickEnd();
                GametimeHelper.updateGametime(server);
                ScriptEvent.onGametickStart();
            }
        });

        AttackBlockCallback.EVENT.register(WandManager::onAttack);
        UseBlockCallback.EVENT.register(WandManager::onUse);

        CommandRegistrationCallback.EVENT.register(CommandManager::init);

        ServerLifecycleEvents.SERVER_STARTING.register(ApiServer::init);
        ServerLifecycleEvents.SERVER_STARTED.register(ApiServer::start);
        ServerLifecycleEvents.SERVER_STOPPING.register(ApiServer::stop);
    }
}
