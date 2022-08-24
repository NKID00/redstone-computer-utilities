package name.nkid00.rcutil.script;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.io.ResponseException;
import name.nkid00.rcutil.manager.ScriptManager;
import name.nkid00.rcutil.model.Script;

public class ScriptApi {
    public static JsonElement dispatch(String method, JsonObject params) throws ResponseException {
        if (method.equals("registerScript")) {
            return new JsonPrimitive(registerScript(
                    params.get("script").getAsString(),
                    params.get("description").getAsString(),
                    params.get("permissionLevel").getAsInt()));
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
        throw ResponseException.METHOD_NOT_FOUND;
    }

    public static String registerScript(String script, String description, int permissionLevel)
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
        return ScriptManager.registerScript(new Script(script, description, permissionLevel));
    }

    public static void deregisterScript(String authKey) throws ResponseException {
        ScriptManager.deregister(authKey);
    }
}
