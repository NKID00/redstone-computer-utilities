package name.nkid00.rcutil.command.argument;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.manager.ScriptManager;

public class ArgumentArgumentType implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("argument", "\" \"", "?!.:;", "\"\\\"\\\\\"", "\"蛤\"");

    public static ArgumentArgumentType argument() {
        return new ArgumentArgumentType();
    }

    public static <S> String getArgument(CommandContext<S> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        InterfaceManager.suggest(builder);
        ScriptManager.suggest(builder);
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}