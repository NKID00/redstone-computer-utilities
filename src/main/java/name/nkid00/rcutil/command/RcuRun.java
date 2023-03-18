package name.nkid00.rcutil.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.event.ScriptRunEvent;
import name.nkid00.rcutil.exception.ApiException;
import name.nkid00.rcutil.helper.ArgumentHelper;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.manager.ScriptManager;
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
        var argument = ArgumentHelper.getTypedMulti(c, "argument...");
        try {
            var result = new ScriptRunEvent().publish(argument, script);
            s.sendFeedback(I18n.t(uuid, "rcutil.command.rcu_run.success", script.name), true);
            return result;
        } catch (ClassCastException | IllegalStateException | UnsupportedOperationException | NullPointerException e) {
            s.sendError(I18n.t(uuid, "rcutil.command.rcu_run.fail.invalid_response"));
            return 0;
        } catch (ApiException e) {
            if (e.equals(ApiException.GENERAL_ERROR)) {
                s.sendError(I18n.t(uuid, "rcutil.command.rcu_run.fail.script_not_runnable"));
            } else if (e.equals(ApiException.ARGUMENT_INVALID)) {
                s.sendError(I18n.t(uuid, "rcutil.command.rcu_run.fail.illegal_argument"));
            } else if (e.equals(ApiException.INTERNAL_ERROR)) {
                s.sendError(I18n.t(uuid, "rcutil.command.rcu_run.fail.script_internal_error"));
            } else {
                s.sendError(I18n.t(uuid, "rcutil.command.rcu_run.fail.invalid_response"));
            }
            return 0;
        }
    }
}
