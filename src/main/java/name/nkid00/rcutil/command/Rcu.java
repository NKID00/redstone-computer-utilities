package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;

import name.nkid00.rcutil.Options;
import name.nkid00.rcutil.helper.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;

public class Rcu {
    public static int execute(CommandContext<ServerCommandSource> c) {
        var s = c.getSource();
        var player = Command.getPlayerOrNull(s);
        if (player == null) {
            s.sendError(I18n.t("rcutil.commands.rcu.failed.notplayerentity"));
            return 0;
        }
        if (player.getInventory().insertStack(new ItemStack(Options.wandItem()))) {
            s.sendFeedback(I18n.t("rcutil.commands.rcu.success.item", Options.wandItemHoverableText()),
                    true);
            return 1;
        } else {
            s.sendError(I18n.t("rcutil.commands.rcu.failed.item"));
            return 0;
        }
    }
}
