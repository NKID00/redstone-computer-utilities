package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;

import name.nkid00.rcutil.command.argument.ScriptArgumentType;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.Log;
import net.minecraft.server.command.ServerCommandSource;

public class RcuInfoScript {
    public static int execute(CommandContext<ServerCommandSource> c) {
        var args = CommandHelper.getOneOrMoreArguments(c, "script name", ScriptArgumentType::getScript);
        Log.info("RcuInfoScript::execute");
        Log.info("{}", args);
        return 0;
    }
}
