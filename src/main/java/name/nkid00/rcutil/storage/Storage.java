package name.nkid00.rcutil.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import name.nkid00.rcutil.helper.Log;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

public class Storage {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static Path rootPath = null;
    private static Path playerDataMapPath = null;

    public static ConcurrentHashMap<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public static void load(MinecraftServer server) {
        Storage.rootPath = server.getSavePath(WorldSavePath.ROOT);
        Storage.playerDataMapPath = Storage.rootPath.resolve("rcutil.json");
        try {
            Storage.playerDataMap = Storage.MAPPER.readValue(Storage.playerDataMapPath.toFile(),
                    new TypeReference<ConcurrentHashMap<UUID, PlayerData>>() {
                    });
            Log.info("Loaded successfully");
        } catch (IOException e) {
            Log.error("Failed to load", e);
            Storage.playerDataMap = new ConcurrentHashMap<>();
        }
        Storage.save();
    }

    public static void save() {
        try {
            Storage.MAPPER.writeValue(Storage.playerDataMapPath.toFile(), Storage.playerDataMap);
            Log.info("Saved successfully");
        } catch (IOException e) {
            Log.error("Failed to save", e);
        }
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return Storage.playerDataMap.computeIfAbsent(uuid, _uuid -> new PlayerData());
    }
}
