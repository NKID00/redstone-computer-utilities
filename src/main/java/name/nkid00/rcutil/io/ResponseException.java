package name.nkid00.rcutil.io;

import com.google.gson.JsonObject;

public class ResponseException extends Exception {
    private int code;
    private String message;
    private String id;

    public static final ResponseException INVALID_REQUEST = new ResponseException(
            -32600, "Invalid Request");
    public static final ResponseException METHOD_NOT_FOUND = new ResponseException(
            -32601, "Method not found");
    public static final ResponseException INVALID_AUTH_KEY = new ResponseException(
            -1, "Invalid authorization key");
    public static final ResponseException ILLEGAL_NAME = new ResponseException(
            -2, "Illegal name");
    public static final ResponseException NAME_EXISTS = new ResponseException(
            -3, "Target with the name already exists");
    public static final ResponseException INVALID_PERMISSION_LEVEL = new ResponseException(
            -4, "Invalid permission level");
    public static final ResponseException SCRIPT_NOT_REGISTERED = new ResponseException(
            -5, "Script is not registered");
    public static final ResponseException ILLEGAL_ARGUMENT = new ResponseException(
            -6, "Illegal argument");
    public static final ResponseException SCRIPT_INTERNAL_ERROR = new ResponseException(
            -7, "Script internal error");
    public static final ResponseException EVENT_NOT_FOUND = new ResponseException(
            -8, "Event cannot be found");
    public static final ResponseException EVENT_CALLBACK_ALREADY_REGISTERED = new ResponseException(
            -9, "Event callback is already registered");
    public static final ResponseException EVENT_CALLBACK_NOT_REGISTERED = new ResponseException(
            -10, "Event callback is not registered");
    public static final ResponseException ACCESS_DENIED = new ResponseException(
            -11, "Access denied");
    public static final ResponseException INTERFACE_NOT_FOUND = new ResponseException(
            -12, "Interface cannot be found");

    public ResponseException(int code, String message, String id) {
        this.code = code;
        this.message = message;
        this.id = id;
    }

    public ResponseException(int code, String message) {
        this(code, message, null);
    }

    public static ResponseException fromResponse(JsonObject response) {
        var error = response.get("error").getAsJsonObject();
        return new ResponseException(
                error.get("code").getAsInt(),
                error.get("message").getAsString(),
                response.get("id").getAsString());
    }

    public JsonObject toResponse() {
        var error = new JsonObject();
        error.addProperty("code", code);
        error.addProperty("message", message);
        var response = new JsonObject();
        response.addProperty("jsonrpc", "2.0");
        response.add("error", error);
        response.addProperty("id", id);
        return response;
    }

    public JsonObject toResponse(String id) {
        return withId(id).toResponse();
    }

    public ResponseException withId(String id) {
        return new ResponseException(code, message, id);
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public String id() {
        return id;
    }

    public boolean equals(ResponseException e) {
        if (this == e) {
                return true;
        }
        return e.code == code;
    }

    public boolean equals(JsonObject response) {
        return equals(ResponseException.fromResponse(response));
    }
}
