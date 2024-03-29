package name.nkid00.rcutil.helper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.util.TypedArgument;
import name.nkid00.rcutil.util.TypedArgumentType;
import net.minecraft.util.Identifier;

public class ArgumentHelper {
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

            @Override
            public String readString() throws CommandSyntaxException {
                if (canRead()) {
                    var c = peek();
                    if (isQuotedStringStart(c)) {
                        skip();
                        return readStringUntil(c);
                    }
                    var result = readUnquotedString();
                    if ((!canRead()) || peek() == CommandDispatcher.ARGUMENT_SEPARATOR_CHAR) {
                        return result;
                    } else {
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                                .dispatcherExpectedArgumentSeparator().createWithContext(this);
                    }
                }
                return "";
            }
        };
    }

    private static LinkedList<String> parseMulti(String greedyString) throws CommandSyntaxException {
        var reader = anyUnquotedStringReader(greedyString);
        var result = new LinkedList<String>();
        var watchdogCursor = -1;
        while (reader.canRead()) {
            if (reader.getCursor() == watchdogCursor) {
                Log.error("Dead loop encountered with {} at {}", greedyString, watchdogCursor);
                break;
            } else {
                watchdogCursor = reader.getCursor();
            }
            reader.skipWhitespace();
            if (!reader.canRead()) {
                break;
            }
            result.add(reader.readString());
        }
        return result;
    }

    public static <S> LinkedList<String> getMulti(CommandContext<S> context, String name)
            throws CommandSyntaxException {
        try {
            return parseMulti(StringArgumentType.getString(context, name));
        } catch (IllegalArgumentException e) {
            return new LinkedList<>();
        }
    }

    private static StringReader anyUnquotedKeepQuotationMarkStringReader(String s) {
        return new StringReader(s) {
            @Override
            public String readUnquotedString() {
                int begin = getCursor();
                while (canRead() && CommandHelper.isAllowedInUnquotedString(peek())) {
                    skip();
                }
                return getString().substring(begin, getCursor());
            }

            @Override
            public String readString() throws CommandSyntaxException {
                if (canRead()) {
                    var c = peek();
                    if (isQuotedStringStart(c)) {
                        skip();
                        return CommandHelper.quoted(readStringUntil(c));
                    }
                    var result = readUnquotedString();
                    if ((!canRead()) || peek() == CommandDispatcher.ARGUMENT_SEPARATOR_CHAR) {
                        return result;
                    } else {
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                                .dispatcherExpectedArgumentSeparator().createWithContext(this);
                    }
                }
                return "";
            }
        };
    }

    private static LinkedList<TypedArgument> parseTypedMulti(String greedyString) throws CommandSyntaxException {
        var reader = anyUnquotedKeepQuotationMarkStringReader(greedyString);
        var result = new LinkedList<TypedArgument>();
        var watchdogCursor = -1;
        while (reader.canRead()) {
            if (reader.getCursor() == watchdogCursor) {
                Log.error("Dead loop encountered with {} at {}", greedyString, watchdogCursor);
                break;
            } else {
                watchdogCursor = reader.getCursor();
            }
            reader.skipWhitespace();
            if (!reader.canRead()) {
                break;
            }
            var s = reader.readString();
            if (CommandHelper.isQuoted(s)) {
                result.add(new TypedArgument(TypedArgumentType.Literal, CommandHelper.unquoted(s)));
            } else if (s.contains(":")) {
                var identifier = Identifier.tryParse(s);
                if (identifier == null) {
                    result.add(new TypedArgument(TypedArgumentType.Literal, s));
                } else {
                    var type = TypedArgumentType.fromString(identifier.getNamespace());
                    if (type == TypedArgumentType.Literal) {
                        result.add(new TypedArgument(TypedArgumentType.Literal, s));
                    } else {
                        result.add(new TypedArgument(type, identifier.getPath()));
                    }
                }
            } else if (CommandHelper.isLetterDigitUnderline(s)) {
                if (InterfaceManager.nameExists(s)) {
                    result.add(new TypedArgument(TypedArgumentType.Interface, s));
                } else {
                    result.add(new TypedArgument(TypedArgumentType.Literal, s));
                }
            } else {
                result.add(new TypedArgument(TypedArgumentType.Literal, s));
            }
        }
        return result;
    }

    public static <S> LinkedList<TypedArgument> getTypedMulti(CommandContext<S> context, String name)
            throws CommandSyntaxException {
        try {
            return parseTypedMulti(StringArgumentType.getString(context, name));
        } catch (IllegalArgumentException e) {
            return new LinkedList<>();
        }
    }

    private static LinkedList<String> parseMultiInternal(String greedyString) throws CommandSyntaxException {
        var reader = anyUnquotedStringReader(greedyString);
        var result = new LinkedList<String>();
        var watchdogCursor = -1;
        while (reader.canRead()) {
            if (reader.getCursor() == watchdogCursor) {
                Log.error("Dead loop encountered with {} at {}", greedyString, watchdogCursor);
                break;
            } else {
                watchdogCursor = reader.getCursor();
            }
            reader.skipWhitespace();
            if (!reader.canRead()) {
                result.add("");
                break;
            }
            result.add(reader.readString());
        }
        return result;
    }

    private static LinkedList<String> parseMultiInternalSuppress(String greedyString) {
        var reader = anyUnquotedStringReader(greedyString);
        var result = new LinkedList<String>();
        var watchdogCursor = -1;
        while (reader.canRead()) {
            if (reader.getCursor() == watchdogCursor) {
                Log.error("Dead loop encountered with {} at {}", greedyString, watchdogCursor);
                break;
            } else {
                watchdogCursor = reader.getCursor();
            }
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

    private static int splitLast(String greedyString) {
        var reader = anyUnquotedStringReader(greedyString);
        var cursor = 0;
        var watchdogCursor = -1;
        while (reader.canRead()) {
            if (reader.getCursor() == watchdogCursor) {
                Log.error("Dead loop encountered with {} at {}", greedyString, watchdogCursor);
                break;
            } else {
                watchdogCursor = reader.getCursor();
            }
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

    public static <S> List<Suggestion> fetch(CommandContext<S> context, SuggestionsBuilder builder,
            SuggestionProvider<S> provider) throws CommandSyntaxException {
        var emptyBuilder = new SuggestionsBuilder(builder.getInput(), builder.getStart());
        try {
            return provider.getSuggestions(context, emptyBuilder).get().getList();
        } catch (InterruptedException | ExecutionException e) {
            return Collections.emptyList();
        }
    }

    private static List<String> uniqueMulti(String greedyString, List<Suggestion> suggestions) {
        LinkedList<String> arguments;
        try {
            arguments = parseMultiInternal(greedyString);
        } catch (CommandSyntaxException e) {
            arguments = parseMultiInternalSuppress(greedyString);
        }
        if (arguments.size() == 0) {
            return suggestions.stream()
                    .map(s -> s.getText())
                    .toList();
        }
        arguments.removeLast();
        var makeCompilerHappy = arguments;
        var previousArguments = greedyString.substring(0, splitLast(greedyString));
        return suggestions.stream()
                .map(s -> s.getText())
                .filter(s -> !makeCompilerHappy.contains(s))
                .map(s -> previousArguments + s)
                .toList();
    }

    public static <S> SuggestionProvider<S> uniqueMulti(SuggestionProvider<S> provider) {
        return (context, builder) -> {
            uniqueMulti(builder.getRemaining(), fetch(context, builder, provider)).forEach(s -> {
                builder.suggest(s);
            });
            return builder.buildFuture();
        };
    }

    public static <S> SuggestionProvider<S> repeatableMulti(SuggestionProvider<S> provider) {
        return (context, builder) -> {
            var remaining = builder.getRemaining();
            var previousArguments = remaining.substring(0, splitLast(remaining));
            fetch(context, builder, provider).forEach(s -> {
                builder.suggest(previousArguments + s.getText());
            });
            return builder.buildFuture();
        };
    }

    @SafeVarargs
    public static <S> SuggestionProvider<S> merge(SuggestionProvider<S> provider, SuggestionProvider<S>... providers) {
        return (context, builder) -> {
            fetch(context, builder, provider).forEach(s -> {
                builder.suggest(s.getText());
            });
            for (SuggestionProvider<S> p : providers) {
                fetch(context, builder, p).forEach(s -> {
                    builder.suggest(s.getText());
                });
            }
            return builder.buildFuture();
        };
    }

    public static <S> SuggestionProvider<S> map(SuggestionProvider<S> provider, Function<String, String> callable) {
        return (context, builder) -> {
            fetch(context, builder, provider).forEach(s -> {
                builder.suggest(callable.apply(s.getText()));
            });
            return builder.buildFuture();
        };
    }
}
