package name.nkid00.rcutil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import name.nkid00.rcutil.command.Command;
import name.nkid00.rcutil.command.CommandStatus;
import name.nkid00.rcutil.fileram.FileRam;
import name.nkid00.rcutil.fileram.FileRamBuilder;

public class RCUtil implements ModInitializer {
    public static final int requiredPermissionLevel = 2;
    public static final int requiredFileOperationPermissionLevel = 4;  // operations on files are dangerous
    public static final Item wandItem = Items.PINK_DYE;
    public static final Text wandItemHoverableText = new ItemStack(wandItem).toHoverableText();
    public static Map<UUID,CommandStatus> commandStatus = null;
    public static FileRamBuilder fileRamBuilder = null;
    public static File baseDirectory = null;
    public static File fileRamBaseDirectory = null;
    // TODO: save & load
    public static HashMap<String, FileRam> fileRams = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            baseDirectory = new File(server.getRunDirectory(), "rcutil/");
            baseDirectory.mkdirs();
            fileRamBaseDirectory = new File(baseDirectory, "fileram/");
            fileRamBaseDirectory.mkdirs();
        });
        // handle realtime input
        ServerTickEvents.START_WORLD_TICK.register(Tick::register);
        // handle wand
        UseBlockCallback.EVENT.register(Wand::register);
        // handle commands
        CommandRegistrationCallback.EVENT.register(Command::register);
    }

    public static CommandStatus getCommandStatus(UUID uuid) {
        if (RCUtil.commandStatus.containsKey(uuid)) {
            return RCUtil.commandStatus.get(uuid);
        } else {
            setCommandStatus(uuid, CommandStatus.Idle);
            return CommandStatus.Idle;
        }
    }

    public static void setCommandStatus(UUID uuid, CommandStatus status) {
        RCUtil.commandStatus.put(uuid, status);
    }
}
