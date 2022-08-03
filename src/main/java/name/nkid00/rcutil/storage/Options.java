package name.nkid00.rcutil.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import name.nkid00.rcutil.helper.Log;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

// TODO: save & load
public class Options {
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    private static File optionsFile;

    public static int requiredPermissionLevel;
    public static int fileOperationRequiredPermissionLevel;
    public static String wandItemName;
    public static Item wandItem;
    public static Text wandItemHoverableText;

    private static void clear() {
        requiredPermissionLevel = 2;
        fileOperationRequiredPermissionLevel = 4; // operations on files are dangerous
        wandItemName = "minecraft:pink_dye";
    }

    private static boolean init() {
        var identifier = Identifier.tryParse(wandItemName);
        if (identifier == null) {
            return false;
        }
        var optional = Registry.ITEM.getOrEmpty(identifier);
        if (optional.isEmpty()) {
            return false;
        }
        wandItem = optional.get();
        wandItemHoverableText = new ItemStack(wandItem).toHoverableText();
        return true;
    }

    public static void load(FabricLoader loader) {
        optionsFile = loader.getConfigDir().resolve("rcu.json").toFile();
        try (var reader = new FileReader(optionsFile, StandardCharsets.UTF_8)) {
            var element = GSON.fromJson(reader, JsonElement.class);
            var object = element.getAsJsonObject();
            requiredPermissionLevel = object.get("requiredPermissionLevel").getAsInt();
            fileOperationRequiredPermissionLevel = object.get("fileOperationRequiredPermissionLevel").getAsInt();
            wandItemName = object.get("wandItem").getAsString();
        } catch (FileNotFoundException e) {
            clear();
        } catch (Exception e) {
            clear();
            Log.error("Failed to load options, generating empty record", e);
        }
        init();
        save();
    }

    public static void save() {
        try (var writer = new FileWriter(optionsFile, StandardCharsets.UTF_8)) {
            var object = new JsonObject();
            object.addProperty("requiredPermissionLevel", requiredPermissionLevel);
            object.addProperty("fileOperationRequiredPermissionLevel", fileOperationRequiredPermissionLevel);
            object.addProperty("wandItem", wandItemName);
            GSON.toJson(object, writer);
        } catch (Exception e) {
            clear();
            Log.error("Failed to save options", e);
        }
    }
}
