package name.nkid00.rcutil.script;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import name.nkid00.rcutil.io.ResponseException;
import name.nkid00.rcutil.io.ScriptServerIO;
import name.nkid00.rcutil.model.Event;
import name.nkid00.rcutil.model.Script;

public class ScriptEventCallback {
    private static SetMultimap<Event, Script> registeredScript = Multimaps
            .synchronizedSetMultimap(LinkedHashMultimap.create());

    public static void registerCallback(Script script, Event event, String callback) {
        script.registerCallback(event, callback);
        registeredScript.put(event, script);
    }

    public static void deregisterCallback(Script script, Event event) {
        script.deregisterCallback(event);
        registeredScript.remove(event, script);
    }

    public static void deregisterAllCallbacks(Script script) {
        for (var event : script.callbacks.keySet()) {
            registeredScript.remove(event, script);
        }
        script.callbacks.clear();
    }

    private static JsonElement call(Script script, Event event, JsonObject params) throws ResponseException {
        if (script.callbackExists(event)) {
            return ScriptServerIO.send(script.callback(event), params, script.clientAddress);
        }
        return null;
    }

    private static JsonElement callSuppress(Script script, Event event, JsonObject params) {
        try {
            return call(script, event, params);
        } catch (ResponseException e) {
            return null;
        }
    }

    private static JsonElement call(Script script, Event event) throws ResponseException {
        return call(script, event, new JsonObject());
    }

    private static JsonElement callSuppress(Script script, Event event) {
        return callSuppress(script, event, new JsonObject());
    }

    private static void broadcast(Event event, JsonObject params) throws ResponseException {
        for (var script : registeredScript.get(event)) {
            call(script, event, params);
        }
    }

    private static void broadcastSuppress(Event event, JsonObject params) {
        for (var script : registeredScript.get(event)) {
            callSuppress(script, event, params);
        }
    }

    private static void broadcast(Event event) throws ResponseException {
        broadcast(event, new JsonObject());
    }

    private static void broadcastSuppress(Event event) {
        broadcastSuppress(event, new JsonObject());
    }

    public static void onGametickStart() {
        broadcastSuppress(Event.ON_GAMETICK_START);
    }

    public static void onGametickEnd() {
        broadcastSuppress(Event.ON_GAMETICK_END);
    }
}
