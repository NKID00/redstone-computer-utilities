package name.nkid00.rcutil.manager;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.netty.channel.ChannelHandlerContext;
import name.nkid00.rcutil.event.ScriptInitializeEvent;
import name.nkid00.rcutil.exception.ApiException;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.helper.MapHelper;
import name.nkid00.rcutil.model.Script;
import net.minecraft.text.Text;

public class ScriptManager {
    private final static ConcurrentLinkedQueue<Script> uninitialized = new ConcurrentLinkedQueue<>();
    private final static ConcurrentHashMap<String, Script> nameScriptMap = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<String, Script> addrScriptMap = new ConcurrentHashMap<>();

    public static Script scriptByName(String name) {
        return nameScriptMap.get(name);
    }

    public static boolean nameExists(String name) {
        return nameScriptMap.containsKey(name);
    }

    public static Script scriptByAddr(String address) {
        return addrScriptMap.get(address);
    }

    public static void add(String name, String description, String addr, ChannelHandlerContext ctx) {
        var script = new Script(name, description, addr, ctx);
        uninitialized.add(script);
    }

    public static void initializeScripts() {
        while (true) {
            Script script;
            try {
                script = uninitialized.remove();
            } catch (NoSuchElementException e) {
                return;
            }
            nameScriptMap.put(script.name, script);
            addrScriptMap.put(script.addr, script);
            try {
                new ScriptInitializeEvent().publish(script);
            } catch (ApiException e) {
                Log.error("Failed to initialize, disconnecting");
                script.ctx.close();
                continue;
            }
            if (script.alive.get()) {
                Log.info("Script {} initialized", script.name);
            }
        }
    }

    public static void remove(String addr) {
        var script = scriptByAddr(addr);
        if (script == null) {
            return;
        }
        script.alive.set(false);
        nameScriptMap.remove(script.name);
        addrScriptMap.remove(script.addr);
        Log.info("Script {} removed", script.name);
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
