package me.nk0.rcu.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.nk0.rcu.exception.BlockNotTargetException;
import me.nk0.rcu.helper.*;
import me.nk0.rcu.manager.InterfaceManager;
import me.nk0.rcu.manager.SelectionManager;
import me.nk0.rcu.model.Interface;
import net.minecraft.server.command.ServerCommandSource;

public class RcuNew {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var player = CommandHelper.requirePlayer(s);
        var uuid = player.getUuid();
        if (!SelectionManager.selected(uuid)) {
            I18n.sendError(s, "rcutil.command.rcu_new.fail.selection_not_found");
            return 0;
        }
        var name = StringArgumentType.getString(c, "interface name");
        if (!CommandHelper.isLetterDigitUnderline(name)) {
            I18n.sendError(s, "rcutil.command.rcu_new.fail.invalid_name", name);
            return 0;
        }
        if (InterfaceManager.nameExists(name)) {
            I18n.sendError(s, "rcutil.command.rcu_new.fail.exists", name);
            return 0;
        }
        var options = ArgumentHelper.getMulti(c, "option...");
        Interface interfaze;
        try {
            interfaze = InterfaceManager.tryCreate(name, uuid, options);
        } catch (BlockNotTargetException e) {
            I18n.sendError(s, e.text());
            return 0;
        } catch (IllegalArgumentException e) {
            I18n.sendError(s, "rcutil.command.rcu_new.fail.invalid_option", e.getMessage());
            return 0;
        }
        if (interfaze == null) {
            I18n.sendError(s, "rcutil.command.rcu_new.fail.invalid_selection");
            return 0;
        } else {
            for (var pos : interfaze) {
                var interfaces = InterfaceManager.interfaceByBlockPos(pos);
                if (interfaces.size() > 1) {
                    var interfacesString = String.join(", ", interfaces.stream()
                            .map(i -> "%s[%d]".formatted(i.object().name(), i.index()))
                            .toList());
                    I18n.sendFeedback(s, false, TextHelper.warn(I18n.t(uuid, "rcutil.command.rcu_new.warn.shared_block",
                            PosHelper.toString(pos), interfacesString)));
                }
            }
            I18n.sendFeedback(s, true, "rcutil.command.rcu_new.success", interfaze.info(uuid));
            interfaze.highlight(player);
            return 1;
        }
    }
}
