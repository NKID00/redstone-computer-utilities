package me.nk0.rcu.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.nk0.rcu.helper.ArgumentHelper;
import me.nk0.rcu.helper.CommandHelper;
import me.nk0.rcu.helper.I18n;
import me.nk0.rcu.manager.ScriptManager;
import net.minecraft.server.command.ServerCommandSource;

public class RcuInfoScript {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var uuid = CommandHelper.uuidOrNull(s);
        I18n.sendFeedback(s, false, ScriptManager.info(uuid));
        return ScriptManager.size();
    }

    public static int executeDetail(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var uuid = CommandHelper.uuidOrNull(s);
        var args = ArgumentHelper.getMulti(c, "script name...");
        int result = 0;
        for (String name : args) {
            var script = ScriptManager.scriptByName(name);
            if (script == null) {
                I18n.sendError(s, "rcutil.command.fail.script_not_found", name);
            } else {
                I18n.sendFeedback(s, false, script.info(uuid));
                result++;
            }
        }
        return result;
    }
}
