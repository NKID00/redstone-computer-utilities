package name.nkid00.rcutil;

import java.io.File;
import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import name.nkid00.rcutil.enumeration.Status;
import name.nkid00.rcutil.rambus.RamBusBuilder;
import name.nkid00.rcutil.rambus.RamBus;

public class RCUtil implements DedicatedServerModInitializer {
    public static final int requiredPermissionLevel = 2;
    public static final Item wandItem = Items.PINK_DYE;
    public static final Text wandItemHoverableText = new ItemStack(wandItem).toHoverableText();
    public static Status status = Status.Idle;
    public static RamBusBuilder ramBusBuilder = null;
    public static File baseDirectory = null;
    public static HashMap<String, RamBus> rams = new HashMap<>();

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            baseDirectory = new File(server.getRunDirectory(), "rcutil/fileram/");
        });
        // handle realtime input
        ServerTickEvents.START_WORLD_TICK.register(Tick::register);
        // handle wand
        UseItemCallback.EVENT.register(Wand::register);
        // handle commands
        CommandRegistrationCallback.EVENT.register(Command::register);
    }
}
