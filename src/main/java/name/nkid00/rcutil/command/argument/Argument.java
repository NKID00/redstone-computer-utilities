package name.nkid00.rcutil.command.argument;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;

public class Argument {
    public static void register() {
        // ArgumentTypeRegistry.registerArgumentType(new Identifier("rcutil", "name"), NameArgumentType.class,
        //         ConstantArgumentSerializer.of(NameArgumentType::name));
        // ArgumentTypeRegistry.registerArgumentType(new Identifier("rcutil", "interface"), InterfaceArgumentType.class,
        //         ConstantArgumentSerializer.of(InterfaceArgumentType::interfaze));
        // ArgumentTypeRegistry.registerArgumentType(new Identifier("rcutil", "script"), ScriptArgumentType.class,
        //         new NamedArgumentSerializer<>((name) -> new ScriptArgumentType(name)));
        // ArgumentTypeRegistry.registerArgumentType(new Identifier("rcutil", "argument"), ArgumentArgumentType.class,
        //         ConstantArgumentSerializer.of(ArgumentArgumentType::argument));
    }
}
