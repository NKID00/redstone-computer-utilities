package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.model.Event;
import name.nkid00.rcutil.script.ScriptEventCallback;
import net.minecraft.server.command.ServerCommandSource;

public class RcuReload {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var uuid = CommandHelper.uuidOrNull(s);
        var result = ScriptEventCallback.broadcast(Event.ON_SCRIPT_RELOAD);
        s.sendFeedback(I18n.t(uuid, "rcutil.command.rcu_reload.success"), true);
        return result;
    }
}
