package name.nkid00.rcutil.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.exception.ResponseException;
import name.nkid00.rcutil.helper.ArgumentHelper;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.manager.ScriptManager;
import name.nkid00.rcutil.model.Event;
import name.nkid00.rcutil.script.ScriptEventCallback;
import net.minecraft.server.command.ServerCommandSource;

public class RcuRun {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var uuid = CommandHelper.uuidOrNull(s);
        var name = StringArgumentType.getString(c, "script name");
        var script = ScriptManager.scriptByName(name);
        if (script == null) {
            s.sendError(I18n.t(uuid, "rcutil.command.fail.script_not_found", name));
            return 0;
        }
        var args = ArgumentHelper.getTypedMulti(c, "argument...");
        var eventArgs = new JsonObject();
        eventArgs.addProperty("uuid", uuid == null ? null : uuid.toString());
        var argsJsonArray = new JsonArray(args.size());
        for (var arg : args) {
            var argJsonObject = new JsonObject();
            argJsonObject.addProperty("type", arg.type().toString());
            argJsonObject.addProperty("value", arg.value());
            argsJsonArray.add(argJsonObject);
        }
        eventArgs.add("runArgs", argsJsonArray);
        try {
            var result = ScriptEventCallback.call(script, Event.ON_SCRIPT_RUN, eventArgs).getAsInt();
            s.sendFeedback(I18n.t(uuid, "rcutil.command.rcu_run.success"), true);
            return result;
        } catch (ClassCastException | IllegalStateException | UnsupportedOperationException | NullPointerException e) {
            s.sendError(I18n.t(uuid, "rcutil.command.rcu_run.fail.invalid_response"));
            return 0;
        } catch (ResponseException e) {
            if (e.equals(ResponseException.EVENT_CALLBACK_NOT_REGISTERED)) {
                s.sendError(I18n.t(uuid, "rcutil.command.rcu_run.fail.script_not_runnable"));
            } else if (e.equals(ResponseException.ILLEGAL_ARGUMENT)) {
                s.sendError(I18n.t(uuid, "rcutil.command.rcu_run.fail.illegal_argument"));
            } else if (e.equals(ResponseException.SCRIPT_INTERNAL_ERROR)) {
                s.sendError(I18n.t(uuid, "rcutil.command.rcu_run.fail.script_internal_error"));
            } else if (e.equals(ResponseException.ACCESS_DENIED)) {
                s.sendError(I18n.t(uuid, "rcutil.command.rcu_run.fail.access_denied"));
            } else {
                s.sendError(I18n.t(uuid, "rcutil.command.rcu_run.fail.invalid_response"));
            }
            return 0;
        }
    }
}
