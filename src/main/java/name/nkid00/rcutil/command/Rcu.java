package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.Options;
import name.nkid00.rcutil.helper.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;

public class Rcu {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var player = s.getPlayer();
        if (player == null) {
            s.sendError(I18n.t("rcutil.command.fail.not_player_entity"));
            return 0;
        }
        var uuid = player.getUuid();
        if (player.getInventory().insertStack(new ItemStack(Options.wandItem()))) {
            s.sendFeedback(I18n.t(uuid, "rcutil.command.rcu.success", Options.wandItemHoverableText()),
                    true);
            return 1;
        } else {
            s.sendError(I18n.t(uuid, "rcutil.command.rcu.fail.inventory_full"));
            return 0;
        }
    }
}
