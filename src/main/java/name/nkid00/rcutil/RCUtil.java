package name.nkid00.rcutil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import name.nkid00.rcutil.command.Command;
import name.nkid00.rcutil.command.CommandStatus;
import name.nkid00.rcutil.wires.Wires;
import name.nkid00.rcutil.wires.WiresBuilder;

public class RCUtil implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final int requiredPermissionLevel = 2;
    public static final int requiredFileOperationPermissionLevel = 4; // operations on files are dangerous
    public static final Item wandItem = Items.PINK_DYE;
    public static final Text wandItemHoverableText = new ItemStack(wandItem).toHoverableText();
    public static final HashMap<UUID, CommandStatus> commandStatus = new HashMap<>();
    public static File baseDirectory = null;
    public static File filesDirectory = null;
    // TODO: save & load
    public static final HashMap<UUID, HashMap<String, Wires>> wires = new HashMap<>();
    public static final HashMap<UUID, HashMap<String, Object>> bus = new HashMap<>();
    public static final HashMap<UUID, HashMap<String, Object>> addrbus = new HashMap<>();
    public static final HashMap<UUID, HashMap<String, Object>> ram = new HashMap<>();
    public static final HashMap<UUID, HashMap<String, Object>> fileram = new HashMap<>();
    public static final HashMap<UUID, HashMap<String, Object>> connection = new HashMap<>();
    public static final HashMap<UUID, WiresBuilder> wiresBuilder = new HashMap<>();
    public static final HashMap<UUID, Object> busBuilder = new HashMap<>();
    public static final HashMap<UUID, Object> addrbusBuilder = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            baseDirectory = new File(server.getRunDirectory(), "rcutil/");
            baseDirectory.mkdirs();
            filesDirectory = new File(baseDirectory, "files/");
            filesDirectory.mkdirs();
        });
        // handle realtime
        // ServerTickEvents.START_WORLD_TICK.register(Tick::onTick);
        // handle wand action
        UseBlockCallback.EVENT.register(Wand::onUse);
        // handle commands
        CommandRegistrationCallback.EVENT.register(Command::register);
    }

    // put and return the default value if key is not found
    public static <K, V> V getOrPutDefault(Map<K, V> map, K key, V defaultValue) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            map.put(key, defaultValue);
            return defaultValue;
        }
    }

    // put and return the new value if key is not found
    public static <K, K2, V> HashMap<K2, V> getOrPutNewHashMap(Map<K, HashMap<K2, V>> map, K key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            var newValue = new HashMap<K2, V>();
            map.put(key, newValue);
            return newValue;
        }
    }

    // put and return the new value if key is not found
    public static <K, V> V getOrPutNewValue(Map<K, V> map, K key, Class<V> valueClass) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            V newValue;
            try {
                newValue = valueClass.getConstructor().newInstance();
            } catch (Exception e) {
                return null;
            }
            map.put(key, newValue);
            return newValue;
        }
    }

    public static CommandStatus getCommandStatus(UUID uuid) {
        return getOrPutDefault(commandStatus, uuid, CommandStatus.Idle);
    }

    public static void setCommandStatus(UUID uuid, CommandStatus status) {
        commandStatus.put(uuid, status);
    }

    public static HashMap<String, Wires> getWires(UUID uuid) {
        return getOrPutNewHashMap(wires, uuid);
    }

    public static HashMap<String, Object> getBus(UUID uuid) {
        return getOrPutNewHashMap(bus, uuid);
    }

    public static HashMap<String, Object> getAddrbus(UUID uuid) {
        return getOrPutNewHashMap(addrbus, uuid);
    }

    public static HashMap<String, Object> getRam(UUID uuid) {
        return getOrPutNewHashMap(ram, uuid);
    }

    public static HashMap<String, Object> getFileram(UUID uuid) {
        return getOrPutNewHashMap(fileram, uuid);
    }

    public static HashMap<String, Object> getConnection(UUID uuid) {
        return getOrPutNewHashMap(connection, uuid);
    }

    public static WiresBuilder getWiresBuilder(UUID uuid) {
        return getOrPutNewValue(wiresBuilder, uuid, WiresBuilder.class);
    }

    public static Object getBusBuilder(UUID uuid) {
        return getOrPutNewValue(busBuilder, uuid, Object.class);
    }

    public static Object getAddrbusBuilder(UUID uuid) {
        return getOrPutNewValue(addrbusBuilder, uuid, Object.class);
    }
}
