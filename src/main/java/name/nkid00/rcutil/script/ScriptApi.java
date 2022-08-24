package name.nkid00.rcutil.script;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.GametimeHelper;
import name.nkid00.rcutil.io.ResponseException;
import name.nkid00.rcutil.manager.ScriptManager;
import name.nkid00.rcutil.model.Script;

public class ScriptApi {
    public static JsonElement dispatch(String method, JsonObject params, String clientAddress)
            throws ResponseException {
        if (method.equals("registerScript")) {
            return new JsonPrimitive(registerScript(
                    params.get("script").getAsString(),
                    params.get("description").getAsString(),
                    params.get("permissionLevel").getAsInt(),
                    clientAddress));
        }
        var authKey = params.get("authKey").getAsString();
        if (!ScriptManager.authKeyValid(authKey)) {
            throw ResponseException.INVALID_AUTH_KEY;
        }
        if (method.equals("deregisterScript")) {
            deregisterScript(authKey);
            return null;
        }
        var script = ScriptManager.scriptByAuthKey(authKey);
        switch (method) {
            case "listScript":
                break;
            case "infoScript":
                break;
            case "invokeScript":
                break;
            case "registerCallback":
                registerCallback(script,
                        params.get("event").getAsString(),
                        params.get("callback").getAsString());
                return null;
            case "deregisterCallback":
                deregisterCallback(script,
                        params.get("event").getAsString());
                return null;
            case "listCallback":
                break;
            case "invokeCallback":
                break;
            case "newInterface":
                break;
            case "removeInterface":
                break;
            case "listInterface":
                break;
            case "infoInterface":
                break;
            case "readInterface":
                break;
            case "writeInterface":
                break;
            case "gametime":
                return new JsonPrimitive(gametime());
            case "info":
                break;
            case "warn":
                break;
            case "error":
                break;
        }
        throw ResponseException.METHOD_NOT_FOUND;
    }

    public static String registerScript(String script, String description, int permissionLevel, String clientAddress)
            throws ResponseException {
        if (!CommandHelper.isLetterDigitUnderline(script)) {
            throw ResponseException.ILLEGAL_NAME;
        }
        if (permissionLevel > 4 || permissionLevel < 2) {
            throw ResponseException.INVALID_PERMISSION_LEVEL;
        }
        if (ScriptManager.nameExists(script)) {
            throw ResponseException.NAME_EXISTS;
        }
        return ScriptManager.registerScript(new Script(script, description, permissionLevel), clientAddress);
    }

    public static void deregisterScript(String authKey) throws ResponseException {
        ScriptManager.deregisterScript(authKey);
    }

    public static void registerCallback(Script script, String event, String callback) throws ResponseException {
        if (!ScriptEventCallback.eventValid(event)) {
            throw ResponseException.EVENT_NOT_FOUND;
        }
        if (script.callbackExists(event)) {
            throw ResponseException.EVENT_CALLBACK_ALREADY_REGISTERED;
        }
        ScriptEventCallback.registerCallback(script, event, callback);
    }

    public static void deregisterCallback(Script script, String event) throws ResponseException {
        if (!ScriptEventCallback.eventValid(event)) {
            throw ResponseException.EVENT_NOT_FOUND;
        }
        if (!script.callbackExists(event)) {
            throw ResponseException.EVENT_CALLBACK_NOT_REGISTERED;
        }
        ScriptEventCallback.deregisterCallback(script, event);
    }

    public static long gametime() throws ResponseException {
        return GametimeHelper.gametime();
    }
}
