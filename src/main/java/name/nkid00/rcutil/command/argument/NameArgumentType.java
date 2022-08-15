package name.nkid00.rcutil.command.argument;

import java.util.Arrays;
import java.util.Collection;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.helper.CommandHelper;

public class NameArgumentType implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("name", "42", "_0");

    public static NameArgumentType name() {
        return new NameArgumentType();
    }

    public static <S> String getName(CommandContext<S> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return CommandHelper.getName(reader);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
