package name.nkid00.rcutil.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.manager.SelectionManager;
import net.minecraft.server.command.ServerCommandSource;

public class RcuNew {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var player = s.getPlayer();
        if (player == null) {
            s.sendError(I18n.t("rcutil.command.rcu_new.fail.not_player_entity"));
            return 0;
        }
        var uuid = player.getUuid();
        if (!SelectionManager.selected(uuid)) {
            s.sendError(I18n.t("rcutil.command.rcu_new.fail.not_selected"));
            return 0;
        }
        var name = StringArgumentType.getString(c, "interface name");
        if (!CommandHelper.isLetterDigitUnderline(name)) {
            s.sendError(I18n.t("rcutil.command.rcu_new.fail.invalid_name"));
            return 0;
        }
        if (InterfaceManager.hasInterface(name)) {
            s.sendError(I18n.t("rcutil.command.rcu_new.fail.exists"));
            return 0;
        }
        var options = CommandHelper.getArguments(c, "option...");
        var interfaze = InterfaceManager.tryNewinterface(name, uuid, options);
        if (interfaze == null) {
            s.sendError(I18n.t("rcutil.command.rcu_new.fail.invalid_option"));
            return 0;
        } else {
            return 1;
        }
    }
}
