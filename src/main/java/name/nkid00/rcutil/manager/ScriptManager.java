package name.nkid00.rcutil.manager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.netty.channel.ChannelHandlerContext;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.helper.MapHelper;
import name.nkid00.rcutil.model.Script;
import name.nkid00.rcutil.script.ScriptEvent;
import net.minecraft.text.Text;

public class ScriptManager {
    private static ConcurrentHashMap<String, Script> nameScriptMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Script> addrScriptMap = new ConcurrentHashMap<>();

    public static Script scriptByName(String name) {
        return nameScriptMap.get(name);
    }

    public static boolean nameExists(String name) {
        return nameScriptMap.containsKey(name);
    }

    public static Script scriptByAddr(String address) {
        return addrScriptMap.get(address);
    }

    public static void register(String name, String description, String addr, ChannelHandlerContext ctx) {
        if (name == null) {
            return;
        }
        if (!CommandHelper.isLetterDigitUnderline(script)) {
            throw ApiException.ILLEGAL_NAME;
        }
        if (permissionLevel > 4 || permissionLevel < 2) {
            throw ApiException.INVALID_PERMISSION_LEVEL;
        }
        if (ScriptManager.nameExists(script)) {
            throw ApiException.NAME_EXISTS;
        }
        var script = new Script(name, description, addr, ctx);
        nameScriptMap.put(name, script);
        addrScriptMap.put(addr, script);
        Log.info("script {} registered", script.name);
    }

    public static void deregister(String addr) {
        var script = scriptByAddr(addr);
        nameScriptMap.remove(script.name);
        ScriptEvent.deregisterAllCallbacks(script);
        addrScriptMap.remove(script.addr);
        Log.info("script {} deregistered", script.name);
    }

    public static <S> CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context,
            final SuggestionsBuilder builder) throws CommandSyntaxException {
        MapHelper.forEachKeySynchronized(nameScriptMap, builder::suggest);
        return builder.buildFuture();
    }

    public static Iterable<Script> iterable() {
        return nameScriptMap.values();
    }

    public static int size() {
        return nameScriptMap.size();
    }

    public static Text info(UUID uuid) {
        if (size() == 0) {
            return I18n.t(uuid, "rcutil.info.script.empty");
        } else {

            return I18n.t(uuid, "rcutil.info.script", size(),
                    String.join(", ", nameScriptMap.keySet().toArray(new String[0])));
        }
    }
}
