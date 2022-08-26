package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;

public class RcuInfo {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        return 0;
    }
}
