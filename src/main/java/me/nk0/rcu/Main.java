package me.nk0.rcu;

import me.nk0.rcu.helper.GsonHelper;
import me.nk0.rcu.helper.WorldHelper;
import me.nk0.rcu.manager.CommandManager;
import me.nk0.rcu.manager.GametimeManager;
import me.nk0.rcu.manager.StorageManager;
import me.nk0.rcu.manager.WandManager;
import me.nk0.rcu.server.ApiServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;

public class Main implements ModInitializer {
    public static boolean isDedicatedServer = false;

    @Override
    public void onInitialize() {
        var loader = FabricLoader.getInstance();
        isDedicatedServer = loader.getEnvironmentType() == EnvType.SERVER;

        ServerLifecycleEvents.SERVER_STARTING.register(GsonHelper::init);
        ServerLifecycleEvents.SERVER_STARTING.register(WorldHelper::init);
        ServerLifecycleEvents.SERVER_STARTING.register(Options::init);
        // worlds is required to load selections
        ServerLifecycleEvents.SERVER_STARTED.register(StorageManager::init);

        ServerTickEvents.START_SERVER_TICK.register(GametimeManager::gametickStart);

        AttackBlockCallback.EVENT.register(WandManager::onAttack);
        UseBlockCallback.EVENT.register(WandManager::onUse);

        CommandRegistrationCallback.EVENT.register(CommandManager::init);

        ServerLifecycleEvents.SERVER_STARTING.register(ApiServer::init);
        ServerLifecycleEvents.SERVER_STARTED.register(ApiServer::start);
        ServerLifecycleEvents.SERVER_STOPPING.register(ApiServer::stop);
    }
}
