package name.nkid00.rcutil.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.network.ServerPlayerEntity;

import name.nkid00.rcutil.RCUtil;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            RegistrationEnvironment environment) {
        dispatcher.register(
                literal("rcu")
                        .requires((s) -> s.hasPermissionLevel(RCUtil.requiredPermissionLevel))
                        // /rcu
                        .executes(Rcu::execute)
                        // /rcu new
                        .then(literal("new")
                                .then(argument("component or connection selector",
                                        StringArgumentType.string())
                                        .executes(RcuNew::execute)
                                        .then(argument("option", StringArgumentType.greedyString())
                                                .executes(RcuNew::execute)))));
    }

    public static ServerPlayerEntity getPlayerOrNull(ServerCommandSource s) {
        var entity = s.getEntity();
        if (entity != null && entity instanceof ServerPlayerEntity) {
            return (ServerPlayerEntity) entity;
        } else {
            return null;
        }
    }
}
