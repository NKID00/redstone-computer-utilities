package name.nkid00.rcutil.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import name.nkid00.rcutil.util.BlockPosWithWorld;

public class Event {
    private final String name;
    private final JsonObject param;

    private static final Event SCRIPT_INITIALIZE = new Event("scriptInitialize");
    private static final Event SCRIPT_RUN = new Event("scriptRun");
    private static final Event INTERFACE_CHANGE = new Event("interfaceChange");
    private static final Event REDSTONE_CHANGE = new Event("redstoneChange");
    private static final Event BLOCK_CHANGE = new Event("blockChange");
    private static final Event BLOCK_UPDATE = new Event("blockUpdate");
    private static final Event ALARM = new Event("alarm");

    private Event(String name) {
        this.name = name;
        this.param = new JsonObject();
    }

    public Event(String name, JsonObject param) {
        this.name = name;
        this.param = param;
    }

    private Event withParam(JsonObject param) {
        return new Event(name, param);
    }

    public static Event scriptInitialize() {
        return SCRIPT_INITIALIZE;
    }

    public static Event scriptRun(JsonObject[] argument) {
        var param = new JsonObject();
        var argumentArray = new JsonArray();
        for (var arg : argument) {
            argumentArray.add(arg);
        }
        param.add("argument", argumentArray);
        return SCRIPT_RUN.withParam(param);
    }

    public static Event interfaceChange(Interface interfaze) {
        var param = new JsonObject();
        param.addProperty("name", interfaze.name());
        return INTERFACE_CHANGE.withParam(param);
    }

    public static Event redstoneChange(BlockPosWithWorld pos) {
        var param = new JsonObject();
        param.add("pos", pos.toJson());
        return REDSTONE_CHANGE.withParam(param);
    }

    public static Event blockChange(BlockPosWithWorld pos) {
        var param = new JsonObject();
        param.add("pos", pos.toJson());
        return BLOCK_CHANGE.withParam(param);
    }

    public static Event blockUpdate(BlockPosWithWorld pos, BlockUpdateType type) {
        var param = new JsonObject();
        param.add("pos", pos.toJson());
        param.addProperty("type", type.toString());
        return BLOCK_UPDATE.withParam(param);
    }

    public enum BlockUpdateType {
        NeighborUpdate, PostPlacement;

        @Override
        public String toString() {
            switch (this) {
                default:
                case NeighborUpdate:
                    return "neighborUpdate";
                case PostPlacement:
                    return "postPlacement";
            }
        }
    }

    public static Event alarm(long gametime, alarmAt at) {
        var param = new JsonObject();
        param.addProperty("gametime", gametime);
        param.addProperty("at", at.toString());
        return ALARM.withParam(param);
    }

    public enum alarmAt {
        Start, End;

        @Override
        public String toString() {
            switch (this) {
                default:
                case Start:
                    return "start";
                case End:
                    return "end";
            }
        }
    }

    public String name() {
        return name;
    }

    public JsonObject param() {
        return param;
    }

    public boolean isValid() {
        // TODO: verify event param
        switch (name) {
            case "scriptInitialize": {
                return true;
            }
            case "scriptRun": {
                return true;
            }
            case "interfaceChange": {
                return true;
            }
            case "redstoneChange": {
                return true;
            }
            case "blockChange": {
                return true;
            }
            case "blockUpdate": {
                return true;
            }
            case "alarm": {
                return true;
            }
            default:
                return false;
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

    public void broadcast(JsonObject content) {
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

    public void broadcast() {
        broadcast(new JsonObject());
    }

    @Override
    public int hashCode() {
        // some random prime number
        int result = 31 + (name == null ? 0 : name.hashCode());
        result = result * 31 + (param == null ? 0 : param.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Event) {
            var other = (Event) obj;
            if (name == null ? other.name != null : !name.equals(other.name)) {
                return false;
            }
            if (param == null ? other.param != null : !param.equals(other.param)) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        if (param == null) {
            return name;
        } else {
            return "%s %s".formatted(name, param);
        }
    }
}
