package name.nkid00.rcutil.script;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import name.nkid00.rcutil.helper.BitSetHelper;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.GametimeHelper;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.helper.TextHelper;
import name.nkid00.rcutil.io.ResponseException;
import name.nkid00.rcutil.io.ScriptServerIO;
import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.manager.ScriptManager;
import name.nkid00.rcutil.model.Event;
import name.nkid00.rcutil.model.Script;
import net.minecraft.server.MinecraftServer;

public class ScriptApi {
    public static JsonElement dispatch(String method, JsonObject params, String clientAddress, MinecraftServer server)
            throws ResponseException {
        if (method.equals("registerScript")) {
            registerScript(params.get("script").getAsString(),
                    params.get("description").getAsString(),
                    params.get("permissionLevel").getAsInt(),
                    params.get("callback").getAsString(),
                    clientAddress);
            return null;
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
            case "deregisterCallback": {
                var eventJsonObject = params.get("event").getAsJsonObject();
                var name = eventJsonObject.get("name").getAsString();
                var param = eventJsonObject.get("param");
                var callback = params.get("callback").getAsString();
                var event = Event.fromRequest(name, param);
                if (event == null) {
                    throw ResponseException.EVENT_NOT_FOUND;
                }
                switch (method) {
                    case "registerCallback":
                        registerCallback(script, event, callback);
                        return null;
                    case "deregisterCallback":
                        deregisterCallback(script, event);
                        return null;
                }
            }
            case "listCallback":
                break;
            case "invokeCallback":
                break;
            case "newInterface":
                break;
            case "removeInterface":
            case "listInterface":
            case "infoInterface":
            case "readInterface":
            case "writeInterface": {
                var interfaze = InterfaceManager.interfaze(params.get("interface").getAsString());
                if (interfaze == null) {
                    throw ResponseException.INTERFACE_NOT_FOUND;
                }
                switch (method) {
                    case "removeInterface":
                        break;
                    case "listInterface":
                        break;
                    case "infoInterface":
                        break;
                    case "readInterface":
                        return new JsonPrimitive(BitSetHelper.toBase64(interfaze.readSuppress()));
                    case "writeInterface":
                        interfaze.writeSuppress(BitSetHelper.fromBase64(params.get("data").getAsString()));
                        return null;
                }
            }
                break;
            case "gametime":
                return new JsonPrimitive(gametime());
            case "info": {
                var message = params.get("message").getAsString();
                Log.info("({}) {}", script.name, message);
                Log.broadcastToOps(server, "(%s/INFO) %s".formatted(script.name, message));
                return null;
            }
            case "warn": {
                var message = params.get("message").getAsString();
                Log.warn("({}) {}", script.name, message);
                Log.broadcastToOps(server, TextHelper.warn(TextHelper.literal(
                        "(%s/WARN) %s".formatted(script.name, message))));
                return null;
            }
            case "error": {
                var message = params.get("message").getAsString();
                Log.error("({}) {}", script.name, message);
                Log.broadcastToOps(server, TextHelper.error(TextHelper.literal(
                        "(%s/ERROR) %s".formatted(script.name, message))));
                return null;
            }
        }
        throw ResponseException.METHOD_NOT_FOUND;
    }

    public static void registerScript(String script, String description, int permissionLevel, String callback,
            String clientAddress) throws ResponseException {
        if (!CommandHelper.isLetterDigitUnderline(script)) {
            throw ResponseException.ILLEGAL_NAME;
        }
        if (permissionLevel > 4 || permissionLevel < 2) {
            throw ResponseException.INVALID_PERMISSION_LEVEL;
        }
        if (ScriptManager.nameExists(script)) {
            throw ResponseException.NAME_EXISTS;
        }
        var authKey = ScriptManager.createScript(script, description, permissionLevel, clientAddress);
        var params = new JsonObject();
        params.addProperty("authKey", authKey);
        try {
            ScriptServerIO.send(callback, params, clientAddress);
        } catch (ResponseException e) {
            ScriptManager.deregisterScript(authKey);
            throw e;
        } catch (Exception e) {
            Log.error("Exception encountered while registering script " + script, e);
            ScriptManager.deregisterScript(authKey);
        }
        if (ScriptManager.nameExists(script)) {
            Log.info("Script {} is registered", script);
        }
    }

    public static void deregisterScript(String authKey) throws ResponseException {
        var name = ScriptManager.scriptByAuthKey(authKey).name;
        ScriptManager.deregisterScript(authKey);
        Log.info("Script {} is deregistered", name);
    }

    public static void registerCallback(Script script, Event event, String callback)
            throws ResponseException {
        if (script.callbackExists(event)) {
            throw ResponseException.EVENT_CALLBACK_ALREADY_REGISTERED;
        }
        ScriptEventCallback.registerCallback(script, event, callback);
    }

    public static void deregisterCallback(Script script, Event event) throws ResponseException {
        switch (event.name()) {
            case "onGametickStartDelay":
            case "onGametickEndDelay":
                throw ResponseException.EVENT_CALLBACK_CANNOT_DEREGISTER;
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
