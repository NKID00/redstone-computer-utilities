package name.nkid00.rcutil.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.Log;
import net.minecraft.server.command.ServerCommandSource;

public class RcuRemove {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var greedyString = StringArgumentType.getString(c, "interface name...");
        var args = CommandHelper.parseArguments(greedyString);
        Log.info("RcuRemove::execute");
        Log.info("{}", args);
        return 0;
    }
}
