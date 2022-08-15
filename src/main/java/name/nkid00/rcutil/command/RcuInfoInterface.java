package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;

import name.nkid00.rcutil.command.argument.InterfaceArgumentType;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.Log;
import net.minecraft.server.command.ServerCommandSource;

public class RcuInfoInterface {
    public static int execute(CommandContext<ServerCommandSource> c) {
        var args = CommandHelper.getOneOrMoreArguments(c, "interface name", InterfaceArgumentType::getInterface);
        Log.info("RcuInfoInterface::execute");
        Log.info("{}", args);
        return 0;
    }
    
}
