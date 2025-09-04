package me.nk0.rcu.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.nk0.rcu.helper.ArgumentHelper;
import me.nk0.rcu.helper.I18n;
import me.nk0.rcu.manager.InterfaceManager;
import net.minecraft.server.command.ServerCommandSource;

public class RcuRemove {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var args = ArgumentHelper.getMulti(c, "interface name...");
        int result = 0;
        for (String name : args) {
            if (InterfaceManager.remove(name) == null) {
                I18n.sendError(s, "rcutil.command.fail.interface_not_found", name);
            } else {
                I18n.sendFeedback(s, true, "rcutil.command.rcu_remove.success", name);
                result++;
            }
        }
        return result;
    }
}
