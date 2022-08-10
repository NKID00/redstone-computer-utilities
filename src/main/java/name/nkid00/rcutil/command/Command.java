package name.nkid00.rcutil.command;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import name.nkid00.rcutil.Options;
import name.nkid00.rcutil.helper.CommandHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            RegistrationEnvironment environment) {
        // /rcu
        dispatcher.register(literal("rcu")
                .requires((s) -> s.hasPermissionLevel(Options.requiredPermissionLevel()))
                .executes(Rcu::execute));
        // /rcu new <interface name> [option...]
        dispatcher.register(literal("rcu")
                .then(literal("new")
                        .then(argument("interface name", StringArgumentType.string())
                                .executes(RcuNew::execute)
                                .then(argument("option", StringArgumentType.greedyString())
                                        .executes(RcuNew::execute)))));
        // /rcu remove <interface name...>
        var rcuRemoveNode = literal("remove").build();
        CommandHelper.addMultipleArguments(rcuRemoveNode, "interface name", StringArgumentType.string(),
                StringArgumentType::getString, RcuRemove::execute);
        dispatcher.register(literal("rcu")
                .then(rcuRemoveNode));
        // /rcu info
        dispatcher.register(literal("rcu")
                .then(literal("info")
                        .executes(RcuInfo::execute)));
        // /rcu info interface
        dispatcher.register(literal("rcu")
                .then(literal("info")
                        .then(literal("interface")
                                .executes(RcuInfoInterface::execute))));
        // /rcu info interface <interface name...>
        var rcuInfoInterfaceNode = literal("interface").build();
        CommandHelper.addMultipleArguments(rcuInfoInterfaceNode, "interface name", StringArgumentType.string(),
                StringArgumentType::getString, RcuRemove::execute);
        dispatcher.register(literal("rcu")
                .then(literal("info")
                        .then(rcuInfoInterfaceNode)));
        // /rcu info script <script name...>
        var rcuInfoScriptNode = literal("script").build();
        CommandHelper.addMultipleArguments(rcuInfoInterfaceNode, "script name", StringArgumentType.string(),
                StringArgumentType::getString, RcuRemove::execute);
        dispatcher.register(literal("rcu")
                .then(literal("info")
                        .then(rcuInfoScriptNode)));
        // /rcu run <script name> [argument...]
        dispatcher.register(literal("rcu")
                .then(literal("run")
                        .executes(RcuRun::execute)));
        // /rcu reload
        dispatcher.register(literal("rcu")
                .then(literal("reload")
                        .executes(RcuReload::execute)));
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
