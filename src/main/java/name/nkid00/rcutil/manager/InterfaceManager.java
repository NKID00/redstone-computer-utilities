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
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.helper.MapHelper;
import name.nkid00.rcutil.model.Interface;

public class InterfaceManager {
    private static ConcurrentHashMap<String, Interface> interfaces = new ConcurrentHashMap<>();

    public static Interface interfaze(String name) {
        return interfaces.get(name);
    }

    public static boolean hasInterface(String name) {
        return interfaces.containsKey(name);
    }

    public static Interface tryNewinterface(String name, UUID uuid, Collection<String> options) {
        var interfaze = Interface.resolve(SelectionManager.selection(uuid));
        if (interfaze == null) {
            return null;
        }
        interfaces.put(name, interfaze);
        return interfaze;
    }

    public static <S> CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context,
            final SuggestionsBuilder builder) {
        MapHelper.forEachKeySynchronized(interfaces, builder::suggest);
        return builder.buildFuture();
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
