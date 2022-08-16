package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;

import name.nkid00.rcutil.Options;
import name.nkid00.rcutil.helper.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;

public class Rcu {
    public static int execute(CommandContext<ServerCommandSource> c) {
        var s = c.getSource();
        var player = s.getPlayer();
        if (player == null) {
            s.sendError(I18n.t("rcutil.command.rcu.fail.notplayerentity"));
            return 0;
        }
        if (player.getInventory().insertStack(new ItemStack(Options.wandItem()))) {
            s.sendFeedback(I18n.t("rcutil.command.rcu.success.item", Options.wandItemHoverableText()),
                    true);
            return 1;
        } else {
            s.sendError(I18n.t("rcutil.command.rcu.fail.item"));
            return 0;
        }
    }
}
