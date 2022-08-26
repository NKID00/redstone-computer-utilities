package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.helper.ArgumentHelper;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.manager.InterfaceManager;
import net.minecraft.server.command.ServerCommandSource;

public class RcuInfoInterface {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var uuid = CommandHelper.uuidOrNull(s);
        s.sendFeedback(InterfaceManager.info(uuid), false);
        return InterfaceManager.size();
    }

    public static int executeDetail(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var uuid = CommandHelper.uuidOrNull(s);
        var args = ArgumentHelper.getMulti(c, "interface name...");
        int result = 0;
        for (String name : args) {
            var interfaze = InterfaceManager.interfaze(name);
            if (interfaze == null) {
                s.sendError(I18n.t(uuid, "rcutil.command.fail.interface_not_found", name));
            } else {
                s.sendFeedback(interfaze.info(uuid), false);
                result++;
            }
        }
        return result;
    }
}
