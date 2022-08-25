package name.nkid00.rcutil;

import name.nkid00.rcutil.io.ScriptServerIO;
import name.nkid00.rcutil.manager.CommandManager;
import name.nkid00.rcutil.manager.TickManager;
import name.nkid00.rcutil.manager.WandManager;
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

        // storage handler
        ServerLifecycleEvents.SERVER_STARTING.register(Options::init);
        ServerLifecycleEvents.SERVER_STARTING.register(Storage::init);

        // tick handler
        ServerTickEvents.START_SERVER_TICK.register(TickManager::onTickStart);
        ServerTickEvents.END_SERVER_TICK.register(TickManager::onTickEnd);

        // wand handler
        AttackBlockCallback.EVENT.register(WandManager::onAttack);
        UseBlockCallback.EVENT.register(WandManager::onUse);

        // command handler
        CommandRegistrationCallback.EVENT.register(CommandManager::init);

        // script server handler
        ServerLifecycleEvents.SERVER_STARTING.register(server -> ScriptServerIO.init());
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ScriptServerIO.start());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ScriptServerIO.stop());
    }
}
