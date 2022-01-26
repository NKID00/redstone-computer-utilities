package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;

import name.nkid00.rcutil.RCUtil;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class Rcu{
    public static int execute(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        if ( true /* TODO: no command is running */) {
            Entity entity = s.getEntity();
            if (entity != null && entity instanceof ServerPlayerEntity) {
                if (((ServerPlayerEntity)entity).inventory.insertStack(new ItemStack(RCUtil.wandItem))) {
                    s.sendFeedback(new TranslatableText("rcutil.commands.rcu.success.item", RCUtil.wandItemHoverableText), true);
                    return 1;
                } else {
                    s.sendError(new TranslatableText("rcutil.commands.rcu.failed.item"));
                    return 0;
                }
            } else {
                s.sendError(new TranslatableText("rcutil.commands.rcu.failed.notfound"));
                return 0;
            }
        } else {
            // TODO: stop running command
            return 1;
        }
        return 0;
    }
}
