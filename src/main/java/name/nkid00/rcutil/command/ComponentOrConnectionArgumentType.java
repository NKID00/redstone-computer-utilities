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

import net.minecraft.text.TranslatableText;

import name.nkid00.rcutil.component.ComponentSelector;
import name.nkid00.rcutil.component.ComponentType;

public class ComponentOrConnectionArgumentType implements ArgumentType<ComponentSelector> {
    private static final Collection<String> EXAMPLES = Arrays.asList("addrbus:Input1", "fileram:new_executable", "wires:1", "ram:ram", "addrbus:Input_1->addrbus:Output_1", "wires:1->wires:2");
    public static final SimpleCommandExceptionType INVALID_SELECTOR_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText(""));
    public static final SimpleCommandExceptionType INVALID_TYPE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText(""));
    public static final SimpleCommandExceptionType INVALID_NAME_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText(""));
    public static final SimpleCommandExceptionType ALREADY_EXISTS_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText(""));

    @Override
    public ComponentSelector parse(StringReader reader) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw INVALID_SELECTOR_EXCEPTION.createWithContext(reader);
        } else if (!CommandUtil.isLetterDigitUnderline(reader.peek())) {
            reader.skip();
            throw INVALID_TYPE_EXCEPTION.createWithContext(reader);
        }
        var typeString = reader.readStringUntil(':');
        if (typeString.isEmpty()) {
            throw INVALID_SELECTOR_EXCEPTION.createWithContext(reader);
        }
        var type = ComponentType.fromString(typeString);
        if (type == null) {

        }
        var name = reader.readStringUntil('-');
        if (typeString.isEmpty()) {
            throw INVALID_SELECTOR_EXCEPTION.createWithContext(reader);
        }
        var selector = new ComponentSelector();
        selector.type = type;
        selector.name = name;
        return selector;
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
