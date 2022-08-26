package name.nkid00.rcutil.manager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.helper.MapHelper;
import name.nkid00.rcutil.model.Script;
import name.nkid00.rcutil.script.ScriptEventCallback;

public class ScriptManager {
    private static ConcurrentHashMap<String, Script> nameScript = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Script> authKeyScript = new ConcurrentHashMap<>();
    private static SetMultimap<String, Script> clientAddressScript = Multimaps
            .synchronizedSetMultimap(LinkedHashMultimap.create());

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

    public static String registerScript(Script script, String clientAddress) {
        var name = script.name;
        String authKey;
        do {
            authKey = UUID.randomUUID().toString();
        } while (authKeyValid(authKey)); // avoid auth key collision
        script.authKey = authKey;
        script.clientAddress = clientAddress;
        nameScript.put(name, script);
        authKeyScript.put(authKey, script);
        clientAddressScript.get(clientAddress).add(script);
        Log.info("Script \"{}\" is registered", name);
        return authKey;
    }

    public static void deregisterScript(String authKey) {
        var script = scriptByAuthKey(authKey);
        nameScript.remove(script.name);
        authKeyScript.remove(script.authKey);
        ScriptEventCallback.deregisterAllCallbacks(script);
        clientAddressScript.remove(script.clientAddress, script);
        Log.info("Script \"{}\" is deregistered", script.name);
    }

    public static void deregisterClientAddress(String clientAddress) {
        for (var script : clientAddressScript.get(clientAddress)) {
            nameScript.remove(script.name);
            authKeyScript.remove(script.authKey);
            ScriptEventCallback.deregisterAllCallbacks(script);
            Log.info("Script \"{}\" is deregistered due to disconnection", script.name);
        }
        clientAddressScript.removeAll(clientAddress);
    }

    public static <S> CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context,
            final SuggestionsBuilder builder) throws CommandSyntaxException {
        MapHelper.forEachKeySynchronized(nameScript, builder::suggest);
        return builder.buildFuture();
    }
}
