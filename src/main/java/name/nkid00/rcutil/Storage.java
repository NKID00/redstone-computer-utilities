package name.nkid00.rcutil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import name.nkid00.rcutil.helper.GsonHelper;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.manager.LanguageManager;
import name.nkid00.rcutil.manager.SelectionManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

public class Storage {
    private static Gson gson;
    private static File file;

    public static void init(MinecraftServer server) {
        file = server.getSavePath(WorldSavePath.ROOT).resolve("rcutil.json").toFile();
        gson = GsonHelper.gsonBuilder(server).create();
        load();
    }

    public static void load() {
        try (var reader = gson.newJsonReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.beginObject();
            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "selection":
                        SelectionManager.load(reader, gson);
                        break;
                    case "interface":
                        InterfaceManager.load(reader, gson);
                        break;
                    case "language":
                        LanguageManager.load(reader, gson);
                        break;
                    default:
                        Log.error("Read unknown field while loading");

                }
            }
            reader.endObject();
        } catch (IOException e) {
        } catch (JsonParseException e) {
            Log.error("Error occurred while loading", e);
        }
        save();
    }

    public static void save() {
        try (var writer = gson.newJsonWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.beginObject();
            writer.name("selection");
            SelectionManager.save(writer, gson);
            writer.name("interface");
            InterfaceManager.save(writer, gson);
            writer.name("language");
            LanguageManager.save(writer, gson);
            writer.endObject();
        } catch (IOException e) {
            Log.error("Error occurred while saving", e);
        }
    }
}
