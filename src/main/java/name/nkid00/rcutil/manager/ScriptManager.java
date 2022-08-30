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

import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.helper.MapHelper;
import name.nkid00.rcutil.model.Script;
import name.nkid00.rcutil.script.ScriptEventCallback;
import net.minecraft.text.Text;

public class ScriptManager {
    private static ConcurrentHashMap<String, Script> nameScript = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Script> authKeyScript = new ConcurrentHashMap<>();
    private static SetMultimap<String, Script> clientAddressScript = Multimaps
            .synchronizedSetMultimap(HashMultimap.create());

    public static Script scriptByName(String name) {
        return nameScript.get(name);
    }

    public static Script scriptByAuthKey(String authKey) {
        return authKeyScript.get(authKey);
    }

    public static boolean nameExists(String name) {
        return nameScript.containsKey(name);
    }

    public static boolean authKeyValid(String authKey) {
        return authKeyScript.containsKey(authKey);
    }

    public static String createScript(String name, String description, int permissionLevel, String clientAddress) {
        String authKey;
        do {
            authKey = UUID.randomUUID().toString();
        } while (authKeyValid(authKey)); // avoid auth key collision
        var script = new Script(name, description, permissionLevel, authKey, clientAddress);
        nameScript.put(name, script);
        authKeyScript.put(authKey, script);
        clientAddressScript.get(clientAddress).add(script);
        return authKey;
    }

    public static void deregisterScript(String authKey) {
        var script = scriptByAuthKey(authKey);
        nameScript.remove(script.name);
        authKeyScript.remove(script.authKey);
        ScriptEventCallback.deregisterAllCallbacks(script);
        clientAddressScript.remove(script.clientAddress, script);
    }

    public static void deregisterClientAddress(String clientAddress) {
        for (var script : clientAddressScript.get(clientAddress)) {
            nameScript.remove(script.name);
            authKeyScript.remove(script.authKey);
            ScriptEventCallback.deregisterAllCallbacks(script);
            Log.info("Script {} is deregistered due to disconnection", script.name);
        }
        clientAddressScript.removeAll(clientAddress);
    }

    public static <S> CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context,
            final SuggestionsBuilder builder) throws CommandSyntaxException {
        MapHelper.forEachKeySynchronized(nameScript, builder::suggest);
        return builder.buildFuture();
    }

    public static int size() {
        return nameScript.size();
    }

    public static Text info(UUID uuid) {
        if (size() == 0) {
            return I18n.t(uuid, "rcutil.info.script.empty");
        } else {

            return I18n.t(uuid, "rcutil.info.script", size(),
                    String.join(", ", nameScript.keySet().toArray(new String[0])));
        }
    }
}
