package name.nkid00.rcutil.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import name.nkid00.rcutil.Options;
import name.nkid00.rcutil.helper.Log;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            RegistrationEnvironment environment) {
        dispatcher.register(literal("rcu")
                .requires((s) -> s.hasPermissionLevel(Options.requiredPermissionLevel()))
                .executes(Rcu::execute));
        dispatcher.register(literal("rcu")
                .then(literal("new")
                        .then(argument("interface name", StringArgumentType.string())
                                .executes(RcuNew::execute)
                                .then(argument("option", StringArgumentType.greedyString())
                                        .executes(RcuNew::execute)))));
        var rcuRemoveNode = dispatcher.register(literal("rcu")
                .then(literal("remove")));
        dispatcher.register(literal("remove")
                .then(argument("interface name", StringArgumentType.string())
                        //.executes(RcuRemove::execute)
                        .redirect(rcuRemoveNode, context -> {
                            Log.info("{}", StringArgumentType.getString(context, "interface name"));
                            return context.getSource();
                        })));
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
