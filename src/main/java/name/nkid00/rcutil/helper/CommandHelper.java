package name.nkid00.rcutil.helper;

import static net.minecraft.server.command.CommandManager.argument;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.BiFunction;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import name.nkid00.rcutil.command.argument.NamedArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.rcon.RconCommandOutput;

public class CommandHelper {
    public static boolean isLetterDigitUnderline(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
    }

    public static boolean isLetterDigitUnderline(String s) {
        return s.chars().mapToObj(i -> (char) i).allMatch(CommandHelper::isLetterDigitUnderline);
    }

    public static String getName(StringReader reader) {
        var begin = reader.getCursor();
        while (reader.canRead() && isLetterDigitUnderline(reader.peek())) {
            reader.skip();
        }
        return reader.getString().substring(begin, reader.getCursor());
    }

    @SuppressWarnings("unchecked")
    public static <T> void addOneOrMoreArguments(CommandNode<ServerCommandSource> node, String name,
            ArgumentType<T> type, BiFunction<CommandContext<ServerCommandSource>, String, T> argGetter,
            Command<ServerCommandSource> command) {
        node.addChild(argument(name, type)
                .redirect(node, c -> {
                    var rawSource = c.getSource();
                    OneOrMoreArgumentsServerCommandSource<T> s;
                    if (rawSource instanceof OneOrMoreArgumentsServerCommandSource) {
                        s = (OneOrMoreArgumentsServerCommandSource<T>) rawSource;
                    } else {
                        s = new OneOrMoreArgumentsServerCommandSource<>(rawSource);
                    }
                    s.args.add(argGetter.apply(c, name));
                    return s;
                })
                .executes(command)
                .build());
    }

    public static <T> void addOneOrMoreArguments(CommandNode<ServerCommandSource> node,
            NamedArgumentType<T> type, BiFunction<CommandContext<ServerCommandSource>, String, T> argGetter,
            Command<ServerCommandSource> command) {
        addOneOrMoreArguments(node, type.argumentName(), type, argGetter, command);
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> getOneOrMoreArguments(CommandContext<ServerCommandSource> c, String name,
            BiFunction<CommandContext<ServerCommandSource>, String, T> argGetter) {
        var rawSource = c.getSource();
        LinkedList<T> args;
        if (rawSource instanceof OneOrMoreArgumentsServerCommandSource) {
            args = ((OneOrMoreArgumentsServerCommandSource<T>) rawSource).args;
        } else {
            args = new LinkedList<T>();
        }
        args.add(argGetter.apply(c, name));
        return args;
    }

    private static class OneOrMoreArgumentsServerCommandSource<T> extends ServerCommandSource {
        public LinkedList<T> args = new LinkedList<T>();

        public OneOrMoreArgumentsServerCommandSource(ServerCommandSource s) {
            super(s.output, s.position, s.rotation, s.world, s.level, s.name, s.displayName, s.server, s.entity,
                    s.silent, s.resultConsumer, s.entityAnchor, s.signedArguments, s.messageChainTaskQueue);
        }
    }

    public static boolean isConsole(ServerCommandSource s) {
        return s.output == s.server || s.output instanceof RconCommandOutput;
    }

    public static LinkedList<String> parseArguments(String greedyString) throws CommandSyntaxException {
        var reader = new StringReader(greedyString);
        var result = new LinkedList<String>();
        while (reader.canRead()) {
            reader.skipWhitespace();
            if (!reader.canRead()) {
                break;
            }
            result.add(reader.readString());
        }
        return result;
    }

    private static LinkedList<String> parseArgumentsSuppress(String greedyString) {
        var reader = new StringReader(greedyString);
        var result = new LinkedList<String>();
        while (reader.canRead()) {
            reader.skipWhitespace();
            if (!reader.canRead()) {
                break;
            }
            var remaining = reader.getRemaining();
            try {
                result.add(reader.readString());
            } catch (CommandSyntaxException e) {
                result.add(remaining);
                break;
            }
        }
        return result;
    }

    private static int splitLastArgument(String greedyString) {
        var reader = new StringReader(greedyString);
        int cursor = 0;
        while (reader.canRead()) {
            reader.skipWhitespace();
            if (!reader.canRead()) {
                break;
            }
            cursor = reader.getCursor();
            try {
                reader.readString();
            } catch (CommandSyntaxException e) {
                break;
            }
        }
        return cursor;
    }

    public static Collection<String> suggestUniqueArguments(String greedyString, Collection<String> suggestion) {
        LinkedList<String> arguments;
        try {
            arguments = parseArguments(greedyString);
        } catch (Exception e) {
            arguments = parseArgumentsSuppress(greedyString);
        }
        if (arguments.size() == 0) {
            return suggestion;
        }
        arguments.removeLast();
        var makeCompilerHappy = arguments;
        var previousArguments = greedyString.substring(0, splitLastArgument(greedyString));
        return suggestion.stream()
                .filter(s -> !makeCompilerHappy.contains(s))
                .map(s -> previousArguments + s)
                .toList();
    }
}
