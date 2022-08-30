package name.nkid00.rcutil;

import name.nkid00.rcutil.helper.GametimeHelper;
import name.nkid00.rcutil.io.ScriptServerIO;
import name.nkid00.rcutil.manager.CommandManager;
import name.nkid00.rcutil.manager.StorageManager;
import name.nkid00.rcutil.manager.WandManager;
import name.nkid00.rcutil.script.ScriptEventCallback;
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

        ServerLifecycleEvents.SERVER_STARTING.register(Options::init);

        // worlds is required to load selections
        ServerLifecycleEvents.SERVER_STARTED.register(StorageManager::init);

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (!GametimeHelper.isFrozen(server)) {
                // before next gametick start, equivalent to this gametick end
                ScriptEventCallback.onGametickEnd();
                GametimeHelper.updateGametime(server);
                ScriptServerIO.sync();
                ScriptEventCallback.onGametickStart();
            }
        });

        AttackBlockCallback.EVENT.register(WandManager::onAttack);
        UseBlockCallback.EVENT.register(WandManager::onUse);

        CommandRegistrationCallback.EVENT.register(CommandManager::init);

        ServerLifecycleEvents.SERVER_STARTING.register(ScriptServerIO::init);
        ServerLifecycleEvents.SERVER_STARTED.register(ScriptServerIO::start);
        ServerLifecycleEvents.SERVER_STOPPING.register(ScriptServerIO::stop);
    }
}
