package name.nkid00.rcutil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;

import name.nkid00.rcutil.helper.GsonHelper;
import name.nkid00.rcutil.helper.Log;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

public class Options {
    private static Gson gson;
    private static File file;
    private static Options instance;

    private Options() {
    }

    @Expose
    private String host = "";
    @Expose
    private int port = 37265;
    @Expose
    private int requiredPermissionLevel = 2;
    @Expose
    private Item wandItem = Items.PINK_DYE;
    private Text wandItemHoverableText;
    @Expose
    private boolean localhostOnly = true;

    public static String host() {
        return instance.host;
    }

    public static int port() {
        return instance.port;
    }

    public static int requiredPermissionLevel() {
        return instance.requiredPermissionLevel;
    }

    public static Item wandItem() {
        return instance.wandItem;
    }

    public static Text wandItemHoverableText() {
        return instance.wandItemHoverableText;
    }

    public static boolean localhostOnly() {
        return instance.localhostOnly;
    }

    public static void init(MinecraftServer server) {
        var loader = FabricLoader.getInstance();
        file = loader.getConfigDir().resolve("rcutil.json").toFile();
        gson = GsonHelper.gsonBuilder(server).excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

        try (var reader = new FileReader(file, StandardCharsets.UTF_8)) {
            instance = gson.fromJson(reader, Options.class);
        } catch (IOException e) {
            instance = new Options();
        } catch (JsonParseException e) {
            Log.error("Error occurred while loading options, generating empty record", e);
            instance = new Options();
        }
        instance.wandItemHoverableText = new ItemStack(wandItem()).toHoverableText();
        save();
    }

    public static void save() {
        try (var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(instance, writer);
        } catch (IOException e) {
            Log.error("Error occurred while saving options", e);
        } catch (JsonParseException e) {
            Log.error("Error occurred while saving options", e);
        }
    }
}
