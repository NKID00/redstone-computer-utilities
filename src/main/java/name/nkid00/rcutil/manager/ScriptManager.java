package name.nkid00.rcutil.manager;

import java.util.concurrent.ConcurrentHashMap;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import name.nkid00.rcutil.helper.MapHelper;
import name.nkid00.rcutil.model.Script;

public class ScriptManager {
    private static ConcurrentHashMap<String, Script> scripts = new ConcurrentHashMap<>();

    public static Script script(String name) {
        return scripts.get(name);
    }

    public static boolean hasScript(String name) {
        return scripts.containsKey(name);
    }

    public static void registerScript(Script script) {
        scripts.put(script.name, script);
    }

    public static void suggest(SuggestionsBuilder builder) {
        MapHelper.forEachKeySynchronized(scripts, builder::suggest);
    }

    static {
        registerScript(new Script("test_script"));
        registerScript(new Script("42"));
    }
}
