package name.nkid00.rcutil.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;

public interface NamedArgumentType<T> extends ArgumentType<T> {
    public String argumentName();
}
