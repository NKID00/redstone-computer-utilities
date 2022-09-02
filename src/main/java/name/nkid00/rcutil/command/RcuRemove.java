package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.helper.ArgumentHelper;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.model.Event;
import name.nkid00.rcutil.script.ScriptEventCallback;
import net.minecraft.server.command.ServerCommandSource;

public class RcuRemove {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var uuid = CommandHelper.uuidOrNull(s);
        var args = ArgumentHelper.getMulti(c, "interface name...");
        int result = 0;
        for (String name : args) {
            if (InterfaceManager.hasInterface(name)) {
                ScriptEventCallback.broadcast(Event.ON_INTERFACE_REMOVE.withInterface(
                        InterfaceManager.interfaceByName(name)));
                InterfaceManager.remove(name);
                s.sendFeedback(I18n.t(uuid, "rcutil.command.rcu_remove.success", name), true);
                result++;
            } else {
                s.sendError(I18n.t(uuid, "rcutil.command.fail.interface_not_found", name));
            }
        }
        return result;
    }
}
