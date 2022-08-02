package name.nkid00.rcutil;

import name.nkid00.rcutil.command.Command;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.storage.Storage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

public class RCUtil implements ModInitializer {

    public Storage data;

    @Override
    public void onInitialize() {
        // storage handler
        ServerLifecycleEvents.SERVER_STARTED.register(Storage::load);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> Storage.save());

        // tick handler
        ServerTickEvents.START_WORLD_TICK.register(Tick::onTick);

        // wand handler
        AttackBlockCallback.EVENT.register(Wand::onAttack);
        UseBlockCallback.EVENT.register(Wand::onUse);

        // command handler
        CommandRegistrationCallback.EVENT.register(Command::register);

        Log.info("Initialized");
    }
}
