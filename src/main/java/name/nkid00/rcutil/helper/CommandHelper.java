package name.nkid00.rcutil.helper;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.rcon.RconCommandOutput;

public class CommandHelper {
    public static boolean isLetterDigitUnderline(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    public static boolean isLetterDigitUnderline(String s) {
        return s.chars().mapToObj(i -> (char) i).allMatch(CommandHelper::isLetterDigitUnderline);
    }

    public static boolean isAllowedInUnquotedString(char c) {
        return !(Character.isWhitespace(c)
                || c == '\\' || c == '\"' || c == '\''
                || Character.isISOControl(c));
    }

    public static String getName(StringReader reader) {
        var begin = reader.getCursor();
        while (reader.canRead() && isLetterDigitUnderline(reader.peek())) {
            reader.skip();
        }
        return reader.getString().substring(begin, reader.getCursor());
    }

    public static boolean isConsole(ServerCommandSource s) {
        return s.output == s.server || s.output instanceof RconCommandOutput;
    }

    private static StringReader anyUnquotedStringReader(String s) {
        return new StringReader(s) {
            @Override
            public String readUnquotedString() {
                int begin = getCursor();
                while (canRead() && CommandHelper.isAllowedInUnquotedString(peek())) {
                    skip();
                }
                return getString().substring(begin, getCursor());
            }
        };
    }

    public static LinkedList<String> parseArguments(String greedyString) throws CommandSyntaxException {
        var reader = anyUnquotedStringReader(greedyString);
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

    public static <S> LinkedList<String> getArguments(CommandContext<S> context, String name) throws CommandSyntaxException {
        try {
            return parseArguments(StringArgumentType.getString(context, name));
        } catch (IllegalArgumentException e) {
            return new LinkedList<>();
        }
    }

    private static LinkedList<String> parseArgumentsInternal(String greedyString) throws CommandSyntaxException {
        var reader = anyUnquotedStringReader(greedyString);
        var result = new LinkedList<String>();
        while (reader.canRead()) {
            reader.skipWhitespace();
            if (!reader.canRead()) {
                result.add("");
                break;
            }
            result.add(reader.readString());
        }
        return result;
    }

    private static LinkedList<String> parseArgumentsInternalSuppress(String greedyString) {
        var reader = anyUnquotedStringReader(greedyString);
        var result = new LinkedList<String>();
        while (reader.canRead()) {
            reader.skipWhitespace();
            if (!reader.canRead()) {
                result.add("");
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
            cursor = reader.getCursor() + 1;
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

    public static List<String> suggestUniqueArguments(String greedyString, Suggestions suggestions) {
        LinkedList<String> arguments;
        try {
            arguments = parseArgumentsInternal(greedyString);
        } catch (CommandSyntaxException e) {
            arguments = parseArgumentsInternalSuppress(greedyString);
        }
        if (arguments.size() == 0) {
            return suggestions.getList().stream()
                    .map(s -> s.getText())
                    .toList();
        }
        arguments.removeLast();
        var makeCompilerHappy = arguments;
        var previousArguments = greedyString.substring(0, splitLastArgument(greedyString));
        return suggestions.getList().stream()
                .map(s -> s.getText())
                .filter(s -> !makeCompilerHappy.contains(s))
                .map(s -> previousArguments + s)
                .toList();
    }

    public static <S> SuggestionProvider<S> uniqueArgumentsWrapper(SuggestionProvider<S> provider) {
        return (context, builder) -> {
            var input = builder.getInput();
            var start = builder.getStart();
            var emptyBuilder = new SuggestionsBuilder(input, start);
            Suggestions suggestions;
            try {
                suggestions = provider.getSuggestions(context, emptyBuilder).get();
            } catch (InterruptedException | ExecutionException e) {
                return builder.buildFuture();
            }
            suggestUniqueArguments(builder.getRemaining(), suggestions).forEach(s -> {
                builder.suggest(s);
            });
            return builder.buildFuture();
        };
    }
}
