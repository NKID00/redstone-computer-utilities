package name.nkid00.rcutil.io;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ResponseException extends Exception {
    private int code;
    private String message;
    private JsonElement id;

    public ResponseException(int code, String message, JsonElement id) {
        this.code = code;
        this.message = message;
        this.id = id;
    }

    public static ResponseException fromResponse(JsonObject response) {
        var error = response.get("error").getAsJsonObject();
        return new ResponseException(error.get("code").getAsInt(),
                error.get("message").getAsString(), response.get("id"));
    }

    public JsonObject toResponse() {
        var error = new JsonObject();
        error.addProperty("code", code);
        error.addProperty("message", message);
        var response = new JsonObject();
        response.addProperty("jsonrpc", "2.0");
        response.add("error", error);
        response.add("id", id);
        return response;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public JsonElement id() {
        return id;
    }
}
