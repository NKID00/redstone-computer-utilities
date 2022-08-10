package name.nkid00.rcutil.helper;

import static net.minecraft.server.command.CommandManager.argument;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.BiFunction;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.server.command.ServerCommandSource;

public class CommandHelper {
    public static boolean isLetterDigitUnderline(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
    }

    public static boolean isLetterDigitUnderline(String s) {
        return s.chars().mapToObj(i -> (char) i).allMatch(CommandHelper::isLetterDigitUnderline);
    }

    @SuppressWarnings("unchecked")
    public static <T> void addMultipleArguments(CommandNode<ServerCommandSource> node, String name,
            ArgumentType<T> type, BiFunction<CommandContext<ServerCommandSource>, String, T> argGetter,
            Command<ServerCommandSource> command) {
        node.addChild(argument(name, type)
                .redirect(node, c -> {
                    var rawSource = c.getSource();
                    MultipleArgumentsServerCommandSource<T> s;
                    if (rawSource instanceof MultipleArgumentsServerCommandSource) {
                        s = (MultipleArgumentsServerCommandSource<T>) rawSource;
                    } else {
                        s = new MultipleArgumentsServerCommandSource<>(rawSource);
                    }
                    s.args.add(argGetter.apply(c, name));
                    return s;
                })
                .executes(command)
                .build());
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> getMultipleArguments(CommandContext<ServerCommandSource> c, String name,
            BiFunction<CommandContext<ServerCommandSource>, String, T> argGetter)
            throws CommandSyntaxException {
        var rawSource = c.getSource();
        if (rawSource instanceof MultipleArgumentsServerCommandSource) {
            var s = (MultipleArgumentsServerCommandSource<T>) rawSource;
            var args = s.args;
            args.add(argGetter.apply(c, name));
            return args;
        } else {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        }
    }

    private static class MultipleArgumentsServerCommandSource<T> extends ServerCommandSource {
        public LinkedList<T> args = new LinkedList<T>();;

        public MultipleArgumentsServerCommandSource(ServerCommandSource s) {
            super(s.output, s.position, s.rotation, s.world, s.level, s.name, s.displayName, s.server, s.entity,
                    s.silent, s.resultConsumer, s.entityAnchor, s.signedArguments, s.messageChainTaskQueue);
        }
    }
}
