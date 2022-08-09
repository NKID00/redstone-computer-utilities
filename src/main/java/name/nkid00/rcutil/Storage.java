package name.nkid00.rcutil;

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
import name.nkid00.rcutil.model.Interface;
import name.nkid00.rcutil.model.Selection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

public class Storage {
    private static Gson gson;
    private static Path rootPath = null;
    private static File rootFile = null;
    private static File selectionsFile = null;
    private static File interfacesFile = null;

    private static ConcurrentHashMap<UUID, Selection> selections;

    public static Selection selection(UUID uuid) {
        return selections.computeIfAbsent(uuid, _uuid -> new Selection());
    }

    public static ConcurrentHashMap<String, Interface> interfaces;

    public static void load(MinecraftServer server) {
        rootPath = server.getSavePath(WorldSavePath.ROOT).resolve("rcutil");
        rootFile = rootPath.toFile();
        selectionsFile = rootPath.resolve("selections.json").toFile();
        interfacesFile = rootPath.resolve("interfaces.json").toFile();
        gson = GsonHelper.gsonBuilder(server).create();

        try (var reader = new FileReader(selectionsFile, StandardCharsets.UTF_8)) {
            selections = gson.fromJson(reader,
                    new TypeToken<ConcurrentHashMap<UUID, Selection>>() {
                    }.getType());
        } catch (IOException e) {
            selections = new ConcurrentHashMap<>();
        } catch (JsonParseException e) {
            Log.error("Failed to load selections, generating empty record", e);
            selections = new ConcurrentHashMap<>();
        }
        try (var reader = new FileReader(interfacesFile, StandardCharsets.UTF_8)) {
            interfaces = gson.fromJson(reader,
                    new TypeToken<ConcurrentHashMap<String, Interface>>() {
                    }.getType());
        } catch (IOException e) {
            interfaces = new ConcurrentHashMap<>();
        } catch (JsonParseException e) {
            Log.error("Failed to load interfaces, generating empty record", e);
            interfaces = new ConcurrentHashMap<>();
        }

        save();
    }

    public static void save() {
        rootFile.mkdir();
        try (var writer = new FileWriter(selectionsFile, StandardCharsets.UTF_8)) {
            gson.toJson(selections, writer);
        } catch (IOException e) {
            Log.error("Failed to save player data", e);
        }
        try (var writer = new FileWriter(interfacesFile, StandardCharsets.UTF_8)) {
            gson.toJson(interfaces, writer);
        } catch (IOException e) {
            Log.error("Failed to save global data", e);
        }
    }
}
