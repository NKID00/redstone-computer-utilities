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
        I18n.sendFeedback(s, false, InterfaceManager.info(uuid));
        return InterfaceManager.size();
    }

    public static int executeDetail(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var player = CommandHelper.playerOrNull(s);
        var uuid = CommandHelper.uuidOrNull(s);
        var args = ArgumentHelper.getMulti(c, "interface name...");
        int result = 0;
        for (String name : args) {
            var interfaze = InterfaceManager.interfaceByName(name);
            if (interfaze == null) {
                I18n.sendError(s, "rcutil.command.fail.interface_not_found", name);
            } else {
                I18n.sendFeedback(s, false, interfaze.info(uuid));
                if (player != null) {
                    interfaze.highlight(player);
                }
                result++;
            }
        }
        return result;
    }
}
