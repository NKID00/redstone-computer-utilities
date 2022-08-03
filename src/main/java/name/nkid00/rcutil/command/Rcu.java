package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;

import name.nkid00.rcutil.RCUtil;
import name.nkid00.rcutil.helper.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;

public class Rcu{
    public static int execute(CommandContext<ServerCommandSource> c) {
        var s = c.getSource();
        var player = Command.getPlayerOrNull(s);
        if (player == null) {
            s.sendError(I18n.t("rcutil.commands.rcu.failed.notplayerentity"));
            return 0;
        }
        var uuid = player.getUuid();
        // if (RCUtil.getCommandStatus(uuid).equals(CommandStatus.Idle)) {
        //     if (player.getInventory().insertStack(new ItemStack(RCUtil.wandItem))) {
        //         s.sendFeedback(new TranslatableText("rcutil.commands.rcu.success.item", RCUtil.wandItemHoverableText), true);
        //         return 1;
        //     } else {
        //         s.sendError(new TranslatableText("rcutil.commands.rcu.failed.item"));
        //         return 0;
        //     }
        // } else {
        //     RCUtil.setCommandStatus(uuid, CommandStatus.Idle);
        //     s.sendFeedback(new TranslatableText("rcutil.commands.rcu.success.stop"), true);
        //     return 1;
        // }
        return 1;
    }
}
