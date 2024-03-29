package name.nkid00.rcutil.manager;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
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
import name.nkid00.rcutil.util.Enumerate;
import name.nkid00.rcutil.util.IndexedObject;
import name.nkid00.rcutil.util.TargetBlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class InterfaceManager {
    private static ConcurrentHashMap<String, Interface> interfaces = new ConcurrentHashMap<>();
    private static SetMultimap<TargetBlockPos, IndexedObject<Interface>> blocks = Multimaps
            .synchronizedSetMultimap(HashMultimap.create());

    public static Interface interfaceByName(String name) {
        return interfaces.get(name);
    }

    public static Set<IndexedObject<Interface>> interfaceByBlockPos(TargetBlockPos pos) {
        return blocks.get(pos);
    }

    public static Set<IndexedObject<Interface>> interfaceByBlockPos(ServerWorld world, BlockPos pos) {
        return interfaceByBlockPos(new TargetBlockPos(world, pos));
    }

    public static Set<IndexedObject<Interface>> interfaceByBlockPos(ServerWorld world, int x, int y, int z) {
        return interfaceByBlockPos(new TargetBlockPos(world, x, y, z));
    }

    public static boolean nameExists(String name) {
        return interfaces.containsKey(name);
    }

    public static Interface tryCreate(String name, UUID uuid, Collection<String> option)
            throws BlockNotTargetException, IllegalArgumentException {
        var selection = SelectionManager.selection(uuid);
        var world = selection.world;
        var lsb = selection.lsb;
        var msb = selection.msb;
        TargetBlockHelper.check(world, lsb, I18n.t(uuid, "rcutil.command.rcu_new.fail.selection_incomplete"));
        TargetBlockHelper.check(world, msb, I18n.t(uuid, "rcutil.command.rcu_new.fail.selection_incomplete"));
        return tryCreate(name, uuid, world, lsb, msb, option);
    }

    public static Interface tryCreate(String name, UUID uuid, ServerWorld world, BlockPos lsb, BlockPos msb, Collection<String> option)
            throws BlockNotTargetException, IllegalArgumentException {
        var interfaze = Interface.resolve(uuid, name, world, lsb, msb, option);
        if (interfaze == null) {
            return null;
        }
        return register(name, interfaze);
    }

    private static Interface register(String name, Interface interfaze) {
        interfaces.put(name, interfaze);
        registerBlocks(interfaze);
        return interfaze;
    }

    private static void registerBlocks(Interface interfaze) {
        for (var ipos : new Enumerate<>(interfaze)) {
            blocks.put(ipos.object(), new IndexedObject<>(ipos.index(), interfaze));
        }
    }

    public static Interface remove(String name) {
        var interfaze = interfaces.remove(name);
        for (var ipos : new Enumerate<>(interfaze)) {
            blocks.remove(ipos.object(), new IndexedObject<>(ipos.index(), interfaze));
        }
        return interfaze;
    }

    public static <S> CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context,
            final SuggestionsBuilder builder) throws CommandSyntaxException {
        MapHelper.forEachKeySynchronized(interfaces, builder::suggest);
        return builder.buildFuture();
    }

    public static Iterable<Interface> iterable() {
        return interfaces.values();
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
        for (var interfaze : iterable()) {
            registerBlocks(interfaze);
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
