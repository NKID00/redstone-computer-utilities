package name.nkid00.rcutil.script;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.io.ResponseException;
import name.nkid00.rcutil.io.ScriptServerIO;
import name.nkid00.rcutil.manager.TimerManager;
import name.nkid00.rcutil.model.Clock;
import name.nkid00.rcutil.model.Event;
import name.nkid00.rcutil.model.Script;
import name.nkid00.rcutil.model.TimedEvent;
import name.nkid00.rcutil.model.Timer;

public class ScriptEventCallback {
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
        try {
            return call(script, event, new JsonObject());
        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            Log.error("Exception encountered while calling event callback", e);
        }
        return null;
    }

    private static JsonElement callSuppress(Script script, Event event) {
        try {
            return callSuppress(script, event, new JsonObject());
        } catch (Exception e) {
            Log.error("Exception encountered while calling event callback", e);
        }
        return null;
    }

    private static void broadcast(Event event, JsonObject params) {
        try {
            for (var script : registeredNonTimedEventScript.get(event)) {
                try {
                    callSuppress(script, event, params);
                } catch (Exception e) {
                    Log.error("Exception encountered while broadcasting event", e);
                }
            }
        } catch (Exception e) {
            Log.error("Exception encountered while broadcasting event", e);
        }
    }

    private static void broadcast(Event event) {
        broadcast(event, new JsonObject());
    }

    public static void onGametickStart() {
        broadcast(Event.ON_GAMETICK_START);
        ImmutableSet<Timer> timers;
        do {
            timers = TimerManager.onGametickStart();
            for (Timer timer : timers) {
                callSuppress(timer.script(), timer.event());
            }
        } while (!timers.isEmpty());
    }

    public static void onGametickEnd() {
        broadcast(Event.ON_GAMETICK_END);
        ImmutableSet<Timer> timers;
        do {
            timers = TimerManager.onGametickEnd();
            for (Timer timer : timers) {
                callSuppress(timer.script(), timer.event());
                if (!(timer instanceof Clock)) {
                    timer.script().deregisterCallback(timer.event());
                }
            }
        } while (!timers.isEmpty());

        // clear onGametickStartDelay(0)
        timers = TimerManager.onGametickStart();
        for (Timer timer : timers) {
            timer.script().deregisterCallback(timer.event());
        }
    }
}
