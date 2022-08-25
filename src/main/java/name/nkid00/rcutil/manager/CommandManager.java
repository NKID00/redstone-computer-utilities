package name.nkid00.rcutil.manager;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import name.nkid00.rcutil.Options;
import name.nkid00.rcutil.command.Rcu;
import name.nkid00.rcutil.command.RcuInfo;
import name.nkid00.rcutil.command.RcuInfoInterface;
import name.nkid00.rcutil.command.RcuInfoScript;
import name.nkid00.rcutil.command.RcuNew;
import name.nkid00.rcutil.command.RcuReload;
import name.nkid00.rcutil.command.RcuRemove;
import name.nkid00.rcutil.command.RcuRun;
import name.nkid00.rcutil.helper.CommandHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;

public class CommandManager {
    public static void init(CommandDispatcher<ServerCommandSource> dispatcher,
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
                                .then(argument("option...",
                                        StringArgumentType.greedyString())
                                        .executes(RcuNew::execute)))));
        // /rcu remove <interface name...>
        dispatcher.register(literal("rcu")
                .then(literal("remove")
                        .then(argument("interface name...", StringArgumentType.greedyString())
                                .suggests(CommandHelper.uniqueArgumentsWrapper((context, builder) -> {
                                    for (int i = 0; i < 10; i++) {
                                        builder.suggest("argument_" + i);
                                    }
                                    return builder.buildFuture();
                                }))
                                .executes(RcuRemove::execute))));
        // /rcu info
        dispatcher.register(literal("rcu")
                .then(literal("info")
                        .executes(RcuInfo::execute)));
        // /rcu info interface [interface name...]
        dispatcher.register(literal("rcu")
                .then(literal("info")
                        .then(literal("interface")
                                .executes(RcuInfoInterface::execute)
                                .then(argument("interface name...",
                                        StringArgumentType.greedyString())
                                        .executes(RcuInfoInterface::execute)))));
        // /rcu info script [script name...]
        dispatcher.register(literal("rcu")
                .then(literal("info")
                        .then(literal("script")
                                .executes(RcuInfoScript::execute)
                                .then(argument("script name...",
                                        StringArgumentType.greedyString())
                                        .executes(RcuInfoInterface::execute)))));
        // /rcu run <script name> [argument...]
        dispatcher.register(literal("rcu")
                .then(literal("run")
                        .then(argument("script name", StringArgumentType.word())
                                .executes(RcuRun::execute)
                                .then(argument("argument...",
                                        StringArgumentType.greedyString())
                                        .executes(RcuRun::execute)))));
        // /rcu reload
        dispatcher.register(literal("rcu")
                .then(literal("reload")
                        .executes(RcuReload::execute)));
    }
}
