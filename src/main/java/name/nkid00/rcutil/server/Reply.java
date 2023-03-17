package name.nkid00.rcutil.server;

import com.google.gson.JsonObject;

public record Reply(Type type, JsonObject msg) {

    public static Reply ApiCall(JsonObject msg) {
        return new Reply(Type.ApiCall, msg);
    }

    public static Reply EventFinish(JsonObject msg) {
        return new Reply(Type.EventFinish, msg);
    }

    public boolean isApiCall() {
        return type == Type.ApiCall;
    }

    public boolean isEventFinish() {
        return type == Type.EventFinish;
    }

    public enum Type {
        ApiCall, EventFinish;
    }
}
