package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.Options;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;

public class Rcu {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var player = CommandHelper.requirePlayer(s);
        if (player.getInventory().insertStack(new ItemStack(Options.wandItem()))) {
            I18n.sendFeedback(s, true, "rcutil.command.rcu.success", Options.wandItemHoverableText());
            return 1;
        } else {
            I18n.sendError(s, "rcutil.command.rcu.fail.inventory_full");
            return 0;
        }
    }
}
