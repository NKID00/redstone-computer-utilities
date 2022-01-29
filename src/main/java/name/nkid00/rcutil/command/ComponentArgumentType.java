package name.nkid00.rcutil.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import name.nkid00.rcutil.component.ComponentSelector;
import name.nkid00.rcutil.component.ComponentType;
import net.minecraft.text.TranslatableText;

public class ComponentArgumentType implements ArgumentType<ComponentSelector> {
    private static final Collection<String> EXAMPLES = Arrays.asList("addrbus:Input1", "fileram:new_executable", "wires:1", "ram:ram");
    public static final SimpleCommandExceptionType TYPE_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText(""));
    public static final SimpleCommandExceptionType ALREADY_EXISTS_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText(""));
    public static final SimpleCommandExceptionType INVALID_COMPONENT_SELECTOR_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText(""));

    @Override
    public ComponentSelector parse(StringReader reader) throws CommandSyntaxException {
        var typeString = reader.readStringUntil(':');
        if (typeString.isEmpty()) {
            throw new CommandSyntaxException(null, null);
        }
        var type = ComponentType.fromString(typeString);
        if (type == null) {

        }
        return new ComponentSelector();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
