package name.nkid00.rcutil.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.exception.BlockNotTargetException;
import name.nkid00.rcutil.helper.ArgumentHelper;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.helper.PosHelper;
import name.nkid00.rcutil.helper.TextHelper;
import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.manager.SelectionManager;
import name.nkid00.rcutil.model.Interface;
import net.minecraft.server.command.ServerCommandSource;

public class RcuNew {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var player = CommandHelper.requirePlayer(s);
        var uuid = player.getUuid();
        if (!SelectionManager.selected(uuid)) {
            s.sendError(I18n.t(uuid, "rcutil.command.rcu_new.fail.selection_not_found"));
            return 0;
        }
        var name = StringArgumentType.getString(c, "interface name");
        if (!CommandHelper.isLetterDigitUnderline(name)) {
            s.sendError(I18n.t(uuid, "rcutil.command.rcu_new.fail.invalid_name", name));
            return 0;
        }
        if (InterfaceManager.hasInterface(name)) {
            s.sendError(I18n.t(uuid, "rcutil.command.rcu_new.fail.exists", name));
            return 0;
        }
        var options = ArgumentHelper.getMulti(c, "option...");
        Interface interfaze;
        try {
            interfaze = InterfaceManager.tryCreate(name, uuid, options);
        } catch (BlockNotTargetException e) {
            s.sendError(e.text());
            return 0;
        } catch (IllegalArgumentException e) {
            s.sendError(I18n.t(uuid, "rcutil.command.rcu_new.fail.invalid_option", e.getMessage()));
            return 0;
        }
        if (interfaze == null) {
            s.sendError(I18n.t(uuid, "rcutil.command.rcu_new.fail.invalid_selection"));
            return 0;
        } else {
            for (var pos : interfaze) {
                var interfaces = InterfaceManager.interfaceByBlockPos(pos);
                if (interfaces.size() > 1) {
                    var interfacesString = String.join(", ", interfaces.stream()
                            .map(i -> "%s[%d]".formatted(i.object().name(), i.index()))
                            .toList());
                    s.sendFeedback(TextHelper.warn(I18n.t(uuid, "rcutil.command.rcu_new.warn.shared_block",
                            PosHelper.toString(pos), interfacesString)), false);
                }
            }
            s.sendFeedback(I18n.t(uuid, "rcutil.command.rcu_new.success", interfaze.info(uuid)), true);
            interfaze.highlight(player);
            return 1;
        }
    }
}
