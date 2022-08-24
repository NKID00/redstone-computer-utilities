package name.nkid00.rcutil.manager;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.helper.MapHelper;
import name.nkid00.rcutil.model.Script;

public class ScriptManager {
    private static ConcurrentHashMap<String, Script> scriptsNameMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Script> scriptsAuthKeyMap = new ConcurrentHashMap<>();

    public static Script scriptByName(String name) {
        return scriptsNameMap.get(name);
    }

    public static Script scriptByAuthKey(String authKey) {
        return scriptsAuthKeyMap.get(authKey);
    }

    public static boolean nameExists(String name) {
        return scriptsNameMap.containsKey(name);
    }

    public static boolean authKeyValid(String authKey) {
        return scriptsAuthKeyMap.containsKey(authKey);
    }

    public static String registerScript(Script script) {
        var name = script.name;
        scriptsNameMap.put(name, script);
        var authKey = UUID.randomUUID().toString();
        scriptsAuthKeyMap.put(authKey, script);
        Log.info("Script \"{}\" is registered", name);
        return authKey;
    }

    public static void deregister(String authKey) {
        var script = scriptsAuthKeyMap.remove(authKey);
        var name = script.name;
        scriptsNameMap.remove(name);
        Log.info("Script \"{}\" is deregistered", name);
    }

    public static void suggest(SuggestionsBuilder builder) {
        MapHelper.forEachKeySynchronized(scriptsNameMap, builder::suggest);
    }
}
