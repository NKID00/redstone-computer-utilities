package name.nkid00.rcutil.command.argument;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.model.Interface;

public class InterfaceArgumentType implements ArgumentType<Interface> {
    private static final Collection<String> EXAMPLES = Arrays.asList("name", "42", "_0");
    public static final DynamicCommandExceptionType INTERFACE_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
            name -> I18n.t("rcutil.argument.interface.notfound", name));

    public static InterfaceArgumentType interfaze() {
        return new InterfaceArgumentType();
    }

    public static <S> Interface getInterface(CommandContext<S> context, String name) {
        return context.getArgument(name, Interface.class);
    }

    @Override
    public Interface parse(StringReader reader) throws CommandSyntaxException {
        var name = CommandHelper.getName(reader);
        if (!InterfaceManager.hasInterface(name)) {
            throw INTERFACE_NOT_FOUND_EXCEPTION.create(name);
        }
        return InterfaceManager.interfaze(name);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        InterfaceManager.suggest(builder);
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
