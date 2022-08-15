package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;

import name.nkid00.rcutil.command.argument.ArgumentArgumentType;
import name.nkid00.rcutil.command.argument.ScriptArgumentType;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.Log;
import net.minecraft.server.command.ServerCommandSource;

public class RcuRun {
    public static int execute(CommandContext<ServerCommandSource> c) {
        var script = ScriptArgumentType.getScript(c, "script name");
        var args = CommandHelper.getOneOrMoreArguments(c, "argument", ArgumentArgumentType::getArgument);
        Log.info("RcuRun::execute");
        Log.info("{}", args);
        return 0;
    }
}
