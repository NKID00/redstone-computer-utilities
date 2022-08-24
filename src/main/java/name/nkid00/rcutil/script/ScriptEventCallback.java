package name.nkid00.rcutil.script;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import name.nkid00.rcutil.io.ResponseException;
import name.nkid00.rcutil.io.ScriptServerIO;
import name.nkid00.rcutil.model.Script;

public class ScriptEventCallback {
    private static SetMultimap<String, Script> eventScript = Multimaps
            .synchronizedSetMultimap(LinkedHashMultimap.create());

    public static boolean eventValid(String event) {
        switch (event) {
            case "onScriptLoad":
            case "onScriptUnload":
            case "onScriptRun":
            case "onScriptInvoke":
            case "onGametickStart":
            case "onGametickEnd":
            case "onInterfaceRedstoneUpdate":
            case "onInterfaceRead":
            case "onInterfaceWrite":
            case "onInterfaceNew":
            case "onInterfaceRemove":
                return true;
            default:
                return false;
        }
    }

    public static void registerCallback(Script script, String event, String callback) {
        script.registerCallback(event, callback);
        eventScript.put(event, script);
    }

    public static void deregisterCallback(Script script, String event) {
        script.deregisterCallback(event);
        eventScript.remove(event, script);
    }

    public static void deregisterAllCallbacks(Script script) {
        for (var event : script.callbacks.keySet()) {
            eventScript.remove(event, script);
        }
        script.callbacks.clear();
    }

    private static JsonElement callSuppressError(String method, JsonObject params, String clientAddress) {
        try {
            return ScriptServerIO.send(method, params, clientAddress);
        } catch (ResponseException e) {
            return null;
        }
    }

    private static void broadcastEvent(String event, JsonObject params) {
        for (var script : eventScript.get(event)) {
            callSuppressError(script.callback(event), params, script.clientAddress);
        }
    }

    private static void broadcastEvent(String event) {
        broadcastEvent(event, new JsonObject());
    }

    public static void onGametickStart() {
        broadcastEvent("onGametickStart");
    }

    public static void onGametickEnd() {
        broadcastEvent("onGametickEnd");
    }
}
