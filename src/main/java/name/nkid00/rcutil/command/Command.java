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

public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            RegistrationEnvironment environment) {
        // /rcu
        dispatcher.register(literal("rcu")
                .requires((s) -> s.hasPermissionLevel(Options.requiredPermissionLevel()))
                .executes(Rcu::execute));
        // /rcu new <interface name> [option...]
        dispatcher.register(literal("rcu")
                .then(literal("new")
                        .then(argument("interface name", StringArgumentType.word())
                                .executes(RcuNew::execute)
                                .then(argument("option",
                                        StringArgumentType.greedyString())
                                        .executes(RcuNew::execute)))));
        // /rcu remove <interface name...>
        var rcuRemoveNode = literal("remove").build();
        CommandHelper.addOneOrMoreArguments(rcuRemoveNode, "interface name", StringArgumentType.word(),
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
        CommandHelper.addOneOrMoreArguments(rcuInfoInterfaceNode, "interface name", StringArgumentType.word(),
                StringArgumentType::getString, RcuInfoInterface::execute);
        dispatcher.register(literal("rcu")
                .then(literal("info")
                        .then(rcuInfoInterfaceNode)));
        // /rcu info script
        dispatcher.register(literal("rcu")
                .then(literal("info")
                        .then(literal("script")
                                .executes(RcuInfoScript::execute))));
        // /rcu info script <script name...>
        var rcuInfoScriptNode = literal("script").build();
        CommandHelper.addOneOrMoreArguments(rcuInfoScriptNode, "script name", StringArgumentType.word(),
                StringArgumentType::getString, RcuInfoScript::execute);
        dispatcher.register(literal("rcu")
                .then(literal("info")
                        .then(rcuInfoScriptNode)));
        // /rcu run <script name> [argument...]
        var rcuRunScriptNameNode = argument("script name", StringArgumentType.word()).build();
        CommandHelper.addOneOrMoreArguments(rcuRunScriptNameNode, "argument", StringArgumentType.string(),
                StringArgumentType::getString, RcuRun::execute);
        dispatcher.register(literal("rcu")
                .then(literal("run")
                        .then(rcuRunScriptNameNode)));
        // /rcu reload
        dispatcher.register(literal("rcu")
                .then(literal("reload")
                        .executes(RcuReload::execute)));
    }
}
