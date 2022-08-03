package name.nkid00.rcutil;

import name.nkid00.rcutil.command.Command;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.storage.Options;
import name.nkid00.rcutil.storage.Storage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;

public class RCUtil implements ModInitializer {
    public static boolean isDedicatedServer = false;

    @Override
    public void onInitialize() {
        var loader = FabricLoader.getInstance();
        isDedicatedServer = loader.getEnvironmentType() == EnvType.SERVER;
        Options.load(loader);

        // storage handler
        ServerLifecycleEvents.SERVER_STARTED.register(Storage::load);

        // tick handler
        ServerTickEvents.START_WORLD_TICK.register(Tick::onTick);

        // wand handler
        AttackBlockCallback.EVENT.register(Wand::onBlockAttack);
        UseBlockCallback.EVENT.register(Wand::onBlockUse);
        AttackEntityCallback.EVENT.register(Wand::onItemAttack);
        UseItemCallback.EVENT.register(Wand::onItemUse);

        // command handler
        CommandRegistrationCallback.EVENT.register(Command::register);

        Log.info("Initialized");
    }
}
