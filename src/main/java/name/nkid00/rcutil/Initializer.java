package name.nkid00.rcutil;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class Initializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // handle realtime input
        ServerTickEvents.START_WORLD_TICK.register((world) -> {

        });

        // handle commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            Command.register(dispatcher);
        });
    }
}
