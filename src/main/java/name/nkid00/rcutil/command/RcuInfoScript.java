package name.nkid00.rcutil.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.helper.ArgumentHelper;
import name.nkid00.rcutil.helper.Log;
import net.minecraft.server.command.ServerCommandSource;

public class RcuInfoScript {
    public static int execute(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var args = ArgumentHelper.getMulti(c, "script name...");
        Log.info("RcuInfoScript::execute");
        Log.info("{}", args);
        return 0;
    }
}
