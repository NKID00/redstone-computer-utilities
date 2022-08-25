package name.nkid00.rcutil.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.helper.ArgumentHelper;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.manager.SelectionManager;
import name.nkid00.rcutil.model.Interface;
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
        var options = ArgumentHelper.getMulti(c, "option...");
        Interface interfaze;
        try {
            interfaze = InterfaceManager.tryNewinterface(name, uuid, options);
        } catch (IllegalArgumentException e) {
            s.sendError(I18n.t("rcutil.command.rcu_new.fail.invalid_option", e.getMessage()));
            return 0;
        }
        if (interfaze == null) {
            s.sendError(I18n.t("rcutil.command.rcu_new.fail.invalid_selection"));
            return 0;
        } else {
            return 1;
        }
    }
}
