package name.nkid00.rcutil.event;

import com.google.gson.JsonObject;

import name.nkid00.rcutil.exception.ApiException;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.manager.ScriptManager;
import name.nkid00.rcutil.model.Script;
import name.nkid00.rcutil.server.ApiServer;

public abstract class Event {
    public static Event fromJson(String name, JsonObject param) throws ApiException {
        Event event;
        switch (name) {
            case "scriptInitialize":
                event = new ScriptInitializeEvent(param);
                break;
            case "scriptRun":
                event = new ScriptRunEvent(param);
                break;
            case "interfaceChange":
                event = new InterfaceChangeEvent(param);
                break;
            case "blockUpdate":
                event = new BlockUpdateEvent(param);
                break;
            case "alarm":
                event = new AlarmEvent(param);
                break;
            default:
                throw ApiException.NAME_NOT_FOUND;
        }
        if (!event.isValid()) {
            throw ApiException.ARGUMENT_INVALID;
        }
        return event;
    }

    public abstract String name();

    public abstract JsonObject param();

    public abstract boolean isValid();

    protected JsonObject publish(JsonObject content, Script script) throws ApiException {
        try {
            return ApiServer.publishEvent(this, content, script);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            Log.error("Exception encountered while publishing event", e);
            return null;
        }
    }

    protected JsonObject publishSuppress(JsonObject content, Script script) {
        try {
            return publish(content, script);
        } catch (ApiException e) {
            return null;
        }
    }

    protected void broadcast(JsonObject content) {
        try {
            for (var script : ScriptManager.iterable()) {
                if (script.eventExists(this)) {
                    publishSuppress(content, script);
                }
            }
        } catch (Exception e) {
            Log.error("Exception encountered while broadcasting event", e);
        }
    }

    @Override
    public int hashCode() {
        // some random prime number
        int result = 31 + (name() == null ? 0 : name().hashCode());
        result = result * 31 + (param() == null ? 0 : param().hashCode());
        return result;
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public String toString() {
        if (param() == null) {
            return name();
        } else {
            return "%s %s".formatted(name(), param());
        }
    }
}
