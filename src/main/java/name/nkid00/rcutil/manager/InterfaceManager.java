package name.nkid00.rcutil.manager;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import name.nkid00.rcutil.exception.BlockNotTargetException;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.helper.MapHelper;
import name.nkid00.rcutil.helper.TargetBlockHelper;
import name.nkid00.rcutil.model.Interface;
import net.minecraft.text.Text;

public class InterfaceManager {
    private static ConcurrentHashMap<String, Interface> interfaces = new ConcurrentHashMap<>();

    public static Interface interfaze(String name) {
        return interfaces.get(name);
    }

    public static boolean hasInterface(String name) {
        return interfaces.containsKey(name);
    }

    public static Interface tryCreate(String name, UUID uuid, Collection<String> options)
            throws BlockNotTargetException {
        var selection = SelectionManager.selection(uuid);
        var world = selection.world;
        var lsb = selection.lsb;
        var msb = selection.msb;
        TargetBlockHelper.check(world, lsb, I18n.t(uuid, "rcutil.command.rcu_new.fail.not_selected"));
        TargetBlockHelper.check(world, msb, I18n.t(uuid, "rcutil.command.rcu_new.fail.not_selected"));
        var interfaze = Interface.resolve(name, world, lsb, msb);
        if (interfaze == null) {
            return null;
        }
        interfaces.put(name, interfaze);
        return interfaze;
    }

    public static Interface remove(String name) {
        return interfaces.remove(name);
    }

    public static <S> CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context,
            final SuggestionsBuilder builder) throws CommandSyntaxException {
        MapHelper.forEachKeySynchronized(interfaces, builder::suggest);
        return builder.buildFuture();
    }

    public static int size() {
        return interfaces.size();
    }

    public static Text info(UUID uuid) {
        if (size() == 0) {
            return I18n.t(uuid, "rcutil.info.interface.empty");
        } else {
            return I18n.t(uuid, "rcutil.info.interface", size(),
                    String.join(", ", interfaces.keySet().toArray(new String[0])));
        }
    }

    public static void load(JsonReader reader, Gson gson) {
        try {
            interfaces = gson.fromJson(reader,
                    new TypeToken<ConcurrentHashMap<String, Interface>>() {
                    }.getType());
        } catch (JsonParseException e) {
            Log.error("Error occurred when loading interfaces, generating empty record", e);
            interfaces = new ConcurrentHashMap<>();
        }
    }

    public static void save(JsonWriter writer, Gson gson) {
        try {
            gson.toJson(interfaces, new TypeToken<ConcurrentHashMap<String, Interface>>() {
            }.getType(), writer);
        } catch (JsonParseException e) {
            Log.error("Error occurred when saving interfaces", e);
        }
    }
}
