package name.nkid00.rcutil.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import name.nkid00.rcutil.command.argument.NameArgumentType;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.manager.SelectionManager;
import net.minecraft.server.command.ServerCommandSource;

public class RcuNew {
    public static int execute(CommandContext<ServerCommandSource> c) {
        var s = c.getSource();
        var player = s.getPlayer();
        if (player == null) {
            s.sendError(I18n.t("rcutil.command.rcu.new.failed.notplayerentity"));
            return 0;
        }
        var uuid = player.getUuid();
        if (!SelectionManager.selected(uuid)) {
            s.sendError(I18n.t("rcutil.command.rcu.new.failed.notselected"));
            return 0;
        }
        var name = NameArgumentType.getName(c, "interface name");
        if (InterfaceManager.hasInterface(name)) {
            s.sendError(I18n.t("rcutil.command.rcu.new.failed.exists"));
            return 0;
        }
        if (!SelectionManager.selected(uuid)) {
            s.sendError(I18n.t("rcutil.command.rcu.new.failed.notselected"));
            return 0;
        }
        var interfaze = InterfaceManager.tryNewinterface(name, uuid, StringArgumentType.getString(c, "option"));
        if (interfaze == null) {
            s.sendError(I18n.t("rcutil.command.rcu.new.failed.unknown"));
            return 0;
        } else {
            return 1;
        }
    }
}
