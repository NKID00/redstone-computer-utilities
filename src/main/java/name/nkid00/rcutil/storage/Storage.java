package name.nkid00.rcutil.storage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import name.nkid00.rcutil.helper.GsonHelper;
import name.nkid00.rcutil.helper.Log;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

public class Storage {
    private static Gson gson;

    private static Path rootPath = null;
    private static File rootFile = null;
    private static File playerDataMapFile = null;
    private static File globalDataFile = null;

    public static ConcurrentHashMap<UUID, DataRecord> playerDataMap = new ConcurrentHashMap<>();
    public static DataRecord globalData = new DataRecord();

    public static void load(MinecraftServer server) {
        rootPath = server.getSavePath(WorldSavePath.ROOT).resolve("rcu");
        rootFile = rootPath.toFile();
        playerDataMapFile = rootPath.resolve("player.json").toFile();
        globalDataFile = rootPath.resolve("global.json").toFile();
        gson = GsonHelper.gsonBuilder(server).create();

        try (var reader = new FileReader(playerDataMapFile, StandardCharsets.UTF_8)) {
            playerDataMap = gson.fromJson(reader,
                    new TypeToken<ConcurrentHashMap<UUID, DataRecord>>() {
                    }.getType());
        } catch (IOException e) {
            playerDataMap = new ConcurrentHashMap<>();
        } catch (JsonParseException e) {
            Log.error("Failed to load player data, generating empty record", e);
            playerDataMap = new ConcurrentHashMap<>();
        }
        try (var reader = new FileReader(globalDataFile, StandardCharsets.UTF_8)) {
            globalData = gson.fromJson(reader, DataRecord.class);
        } catch (IOException e) {
            globalData = new DataRecord();
        } catch (JsonParseException e) {
            Log.error("Failed to load global data, generating empty record", e);
            globalData = new DataRecord();
        }

        save();
    }

    public static void save() {
        rootFile.mkdir();
        try (var writer = new FileWriter(playerDataMapFile, StandardCharsets.UTF_8)) {
            gson.toJson(playerDataMap, writer);
        } catch (IOException e) {
            Log.error("Failed to save player data", e);
        }
        try (var writer = new FileWriter(globalDataFile, StandardCharsets.UTF_8)) {
            gson.toJson(globalData, writer);
        } catch (IOException e) {
            Log.error("Failed to save global data", e);
        }
    }

    public static DataRecord getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, _uuid -> new DataRecord());
    }
}
