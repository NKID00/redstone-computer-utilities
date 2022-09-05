package name.nkid00.rcutil.script;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import name.nkid00.rcutil.exception.BlockNotTargetException;
import name.nkid00.rcutil.exception.ResponseException;
import name.nkid00.rcutil.helper.BitSetHelper;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.GametimeHelper;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.helper.PosHelper;
import name.nkid00.rcutil.helper.TextHelper;
import name.nkid00.rcutil.helper.WorldHelper;
import name.nkid00.rcutil.io.ScriptServerIO;
import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.manager.ScriptManager;
import name.nkid00.rcutil.model.Event;
import name.nkid00.rcutil.model.Interface;
import name.nkid00.rcutil.model.Script;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3i;

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
            case "listScript": {
                var scripts = new JsonObject();
                for (var scriptTarget : ScriptManager.iterable()) {
                    var scriptJsonObject = new JsonObject();
                    scriptJsonObject.addProperty("description", scriptTarget.description);
                    scriptJsonObject.addProperty("permissionLevel", scriptTarget.permissionLevel);
                    scripts.add(scriptTarget.name, scriptJsonObject);
                }
                return scripts;
            }
            case "registerCallback": {
                var event = Event.fromRequest(params.get("event").getAsJsonObject());
                var callback = params.get("callback").getAsString();
                registerCallback(script, event, callback);
                return null;
            }
            case "deregisterCallback": {
                var event = Event.fromRequest(params.get("event").getAsJsonObject());
                deregisterCallback(script, event);
                return null;
            }
            case "invokeScript":
            case "listCallback":
            case "invokeCallback": {
                var scriptTarget = ScriptManager.scriptByName(params.get("script").getAsString());
                if (scriptTarget == null) {
                    throw ResponseException.SCRIPT_NOT_FOUND;
                }
                switch (method) {
                    case "invokeScript": {
                        var args = params.get("args").getAsJsonObject();
                        return ScriptEventCallback.call(scriptTarget, Event.ON_SCRIPT_INVOKE, args);
                    }
                    case "listCallback": {
                        var events = new JsonArray(scriptTarget.callbacks.size());
                        for (var event : scriptTarget.callbacks.keySet()) {
                            var eventJsonObject = new JsonObject();
                            eventJsonObject.addProperty("name", event.name());
                            eventJsonObject.add("param", event.jsonParam());
                            events.add(eventJsonObject);
                        }
                        return events;
                    }
                    case "invokeCallback": {
                        var event = Event.fromRequest(params.get("event").getAsJsonObject());
                        var args = params.get("args").getAsJsonObject();
                        return ScriptEventCallback.call(scriptTarget, event, args);
                    }
                }
            }
            case "newInterface": {
                var name = params.get("interface").getAsString();
                if (!CommandHelper.isLetterDigitUnderline(name)) {
                    throw ResponseException.ILLEGAL_NAME;
                }
                if (InterfaceManager.nameExists(name)) {
                    throw ResponseException.NAME_EXISTS;
                }
                var world = WorldHelper.fromString(server, params.get("world").getAsString());
                if (world == null) {
                    throw ResponseException.WORLD_NOT_FOUND;
                }
                var lsb = PosHelper.toBlockPos(PosHelper.fromJson(params.get("lsb")));
                var increment = PosHelper.fromJson(params.get("increment"));
                var size = params.get("size").getAsInt();
                if (size < 1) {
                    throw ResponseException.INVALID_SIZE;
                }
                if (size != 1 && increment.equals(Vec3i.ZERO)) {
                    throw ResponseException.INVALID_SIZE;
                }
                var args = params.get("args").getAsJsonObject();
                Interface interfaze;
                try {
                    interfaze = InterfaceManager.tryCreate(name, world, lsb, increment, size, args);
                } catch (BlockNotTargetException e) {
                    throw ResponseException.BLOCK_NOT_TARGET;
                } catch (IllegalArgumentException e) {
                    throw ResponseException.ILLEGAL_ARGUMENT;
                }
                if (interfaze == null) {
                    throw ResponseException.ILLEGAL_ARGUMENT;
                }
                return null;
            }
            case "listInterface": {
                var interfaces = new JsonObject();
                for (var interfazeTarget : InterfaceManager.iterable()) {
                    var interfaceJsonObject = new JsonObject();
                    interfaceJsonObject.addProperty("world", WorldHelper.toString(interfazeTarget.world()));
                    var lsb = interfazeTarget.lsb();
                    var lsbJsonArray = new JsonArray(3);
                    lsbJsonArray.add(lsb.getX());
                    lsbJsonArray.add(lsb.getY());
                    lsbJsonArray.add(lsb.getZ());
                    interfaceJsonObject.add("lsb", lsbJsonArray);
                    var increment = interfazeTarget.increment();
                    var incrementJsonArray = new JsonArray(3);
                    incrementJsonArray.add(increment.getX());
                    incrementJsonArray.add(increment.getY());
                    incrementJsonArray.add(increment.getZ());
                    interfaceJsonObject.add("increment", incrementJsonArray);
                    interfaces.add(interfazeTarget.name(), interfaceJsonObject);
                }
                return interfaces;
            }
            case "removeInterface":
            case "readInterface":
            case "writeInterface": {
                var interfaze = InterfaceManager.interfaceByName(params.get("interface").getAsString());
                if (interfaze == null) {
                    throw ResponseException.INTERFACE_NOT_FOUND;
                }
                var args = new JsonObject();
                args.addProperty("script", script.name);
                switch (method) {
                    case "removeInterface":
                        InterfaceManager.remove(interfaze.name());
                        return null;
                    case "readInterface":
                        ScriptEventCallback.broadcast(Event.ON_INTERFACE_READ.withInterface(interfaze), args);
                        return new JsonPrimitive(BitSetHelper.toBase64(interfaze.readSuppress()));
                    case "writeInterface":
                        ScriptEventCallback.broadcast(Event.ON_INTERFACE_WRITE.withInterface(interfaze), args);
                        interfaze.writeSuppress(BitSetHelper.fromBase64(params.get("data").getAsString()));
                        return null;
                }
            }
                break;
            case "gametime":
                return new JsonPrimitive(GametimeHelper.gametime());
            case "listPlayer": {
                var players = new JsonObject();
                for (var player : server.getPlayerManager().getPlayerList()) {
                    var playerJsonObject = new JsonObject();
                    var profile = player.getGameProfile();
                    playerJsonObject.addProperty("name", profile.getName());
                    playerJsonObject.addProperty("permissionLevel", server.getPermissionLevel(profile));
                    players.add(profile.getId().toString(), playerJsonObject);
                }
                return players;
            }
            case "info":
            case "warn":
            case "error": {
                var message = params.get("message").getAsString();
                switch (method) {
                    case "info":
                        Log.info("({}) {}", script.name, message);
                        Log.broadcastToOps(server, "(%s/INFO) %s".formatted(script.name, message));
                        return null;
                    case "warn":
                        Log.warn("({}) {}", script.name, message);
                        Log.broadcastToOps(server, TextHelper.warn(TextHelper.literal(
                                "(%s/WARN) %s".formatted(script.name, message))));
                        return null;
                    case "error":
                        Log.error("({}) {}", script.name, message);
                        Log.broadcastToOps(server, TextHelper.error(TextHelper.literal(
                                "(%s/ERROR) %s".formatted(script.name, message))));
                        return null;
                }
            }
            case "sendInfo":
            case "sendWarn":
            case "sendError": {
                var uuid = params.get("uuid").getAsString();
                var player = server.getPlayerManager().getPlayer(uuid);
                if (player == null) {
                    throw ResponseException.PLAYER_NOT_FOUND;
                }
                var message = params.get("message").getAsString();
                switch (method) {
                    case "sendInfo":
                        Log.send(player, "(%s/INFO) %s".formatted(script.name, message));
                        return null;
                    case "sendWarn":
                        Log.send(player, TextHelper.warn(TextHelper.literal(
                                "(%s/WARN) %s".formatted(script.name, message))));
                        return null;
                    case "sendError":
                        Log.send(player, TextHelper.error(TextHelper.literal(
                                "(%s/ERROR) %s".formatted(script.name, message))));
                        return null;
                }
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
            Log.error("Exception encountered while registering script:" + script, e);
            ScriptManager.deregisterScript(authKey);
        }
        if (ScriptManager.nameExists(script)) {
            Log.info("script:{} is registered", script);
        }
    }

    public static void deregisterScript(String authKey) throws ResponseException {
        var name = ScriptManager.scriptByAuthKey(authKey).name;
        ScriptManager.deregisterScript(authKey);
        Log.info("script:{} is deregistered", name);
    }

    public static void registerCallback(Script script, Event event, String callback)
            throws ResponseException {
        if (script.callbackExists(event)) {
            throw ResponseException.EVENT_CALLBACK_ALREADY_REGISTERED;
        }
        ScriptEventCallback.registerCallback(script, event, callback);
    }

    public static void deregisterCallback(Script script, Event event) throws ResponseException {
        if (!script.callbackExists(event)) {
            throw ResponseException.EVENT_CALLBACK_NOT_REGISTERED;
        }
        ScriptEventCallback.deregisterCallback(script, event);
    }
}
