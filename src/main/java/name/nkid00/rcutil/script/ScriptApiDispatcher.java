package name.nkid00.rcutil.script;

import com.google.gson.JsonObject;

import name.nkid00.rcutil.script.exception.ScriptException;

public class ScriptApiDispatcher {
    public static JsonObject dispatch(JsonObject msg) {
        var result = new JsonObject();
        result.addProperty("jsonrpc", "2.0");
        result.addProperty("id", "1");
        result.addProperty("id", "2");
        // try {
        //     if (msg.get("jsonrpc").getAsString() != "2.0") {

        //     }
        //     var method = msg.get("method").getAsString();
        //     var params = msg.get("params").getAsJsonObject();
        //     result.add("id", msg.get("id"));
        // } catch (IllegalStateException | ClassCastException | NullPointerException e) {
        //     var error = new JsonObject();
        //     result.add("error", error);
        //     result.add("id", null);
        // }
        return result;
    }

    public static JsonObject dispatch(String method, JsonObject params) throws ScriptException {
        return new JsonObject();
    }
}
