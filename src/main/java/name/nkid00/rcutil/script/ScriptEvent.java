package name.nkid00.rcutil.script;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import name.nkid00.rcutil.exception.ApiException;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.manager.TimerManager;
import name.nkid00.rcutil.model.Clock;
import name.nkid00.rcutil.model.Event;
import name.nkid00.rcutil.model.Script;
import name.nkid00.rcutil.model.TimedEvent;
import name.nkid00.rcutil.model.Timer;
import name.nkid00.rcutil.server.ApiServer;

public class ScriptEvent {
    private static SetMultimap<Event, Script> registeredNonTimedEventScript = Multimaps
            .synchronizedSetMultimap(HashMultimap.create());

    public static void registerCallback(Script script, Event event, String callback) {
        script.registerCallback(event, callback);
        if (!(event instanceof TimedEvent)) {
            registeredNonTimedEventScript.put(event, script);
        }
    }

    public static void deregisterCallback(Script script, Event event) {
        script.deregisterCallback(event);
        if (!(event instanceof TimedEvent)) {
            registeredNonTimedEventScript.remove(event, script);
        }
    }

    public static void deregisterAllCallbacks(Script script) {
        for (var event : ImmutableSet.copyOf(script.callbacks.keySet())) {
            script.deregisterCallback(event);
            if (!(event instanceof TimedEvent)) {
                registeredNonTimedEventScript.remove(event, script);
            }
        }
    }

    public static JsonElement call(Script script, String method, JsonObject args) throws ApiException {
        try {
            return ApiServer.publishEvent(method, args, script.clientAddress);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            Log.error("Exception encountered while calling event callback", e);
            return null;
        }
    }

    public static JsonElement callSuppress(Script script, String method, JsonObject args) {
        try {
            return call(script, method, args);
        } catch (ApiException e) {
            return null;
        }
    }

    public static JsonElement call(Script script, String method) throws ApiException {
        return call(script, method, new JsonObject());
    }

    public static JsonElement callSuppress(Script script, String method) {
        return callSuppress(script, method, new JsonObject());
    }

    public static JsonElement call(Script script, Event event, JsonObject args) throws ApiException {
        if (script.callbackExists(event)) {
            return call(script, script.callback(event), args);
        }
        throw ApiException.EVENT_CALLBACK_NOT_REGISTERED;
    }

    public static JsonElement callSuppress(Script script, Event event, JsonObject args) {
        try {
            return call(script, event, args);
        } catch (ApiException e) {
            return null;
        }
    }

    public static JsonElement call(Script script, Event event) throws ApiException {
        return call(script, event, new JsonObject());
    }

    public static JsonElement callSuppress(Script script, Event event) {
        return callSuppress(script, event, new JsonObject());
    }

    public static int broadcast(Event event, JsonObject args) {
        int result = 0;
        try {
            for (var script : registeredNonTimedEventScript.get(event)) {
                callSuppress(script, event, args);
                result++;
            }
        } catch (Exception e) {
            Log.error("Exception encountered while broadcasting event", e);
        }
        return result;
    }

    public static int broadcast(Event event) {
        return broadcast(event, new JsonObject());
    }

    public static void onGametickStart() {
        broadcast(Event.ON_GAMETICK_START);
        ImmutableSet<Timer> timers;
        do {
            timers = TimerManager.onGametickStart();
            for (var timer : timers) {
                var script = timer.script();
                var method = script.callback(timer.event());
                if (!(timer instanceof Clock)) {
                    script.deregisterCallback(timer.event());
                }
                callSuppress(script, method);
            }
        } while (!timers.isEmpty());
    }

    public static void onGametickEnd() {
        broadcast(Event.ON_GAMETICK_END);
        ImmutableSet<Timer> timers;
        do {
            timers = TimerManager.onGametickEnd();
            for (var timer : timers) {
                var script = timer.script();
                var method = script.callback(timer.event());
                if (!(timer instanceof Clock)) {
                    script.deregisterCallback(timer.event());
                }
                callSuppress(script, method);
            }
        } while (!timers.isEmpty());

        // clear onGametickStartDelay(0)
        for (var timer : TimerManager.onGametickStart()) {
            timer.script().deregisterCallback(timer.event());
        }

        for (var interfaze : InterfaceManager.resetUpdated()) {
            broadcast(Event.ON_INTERFACE_UPDATE.withInterface(interfaze));
        }
    }
}
