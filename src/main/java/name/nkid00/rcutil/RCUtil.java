package name.nkid00.rcutil;

import name.nkid00.rcutil.command.Command;
import name.nkid00.rcutil.command.argument.Argument;
import name.nkid00.rcutil.helper.Log;
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
                ServerLifecycleEvents.SERVER_STARTED.register(Options::init);
                ServerLifecycleEvents.SERVER_STARTED.register(Storage::init);

                // tick handler
                ServerTickEvents.START_WORLD_TICK.register(Tick::onTick);

                // wand handler
                AttackBlockCallback.EVENT.register(Wand::onAttack);
                UseBlockCallback.EVENT.register(Wand::onUse);

                // command handler
                Argument.register();
                CommandRegistrationCallback.EVENT.register(Command::register);

                Log.info("Initialized");
        }
}
