package name.nkid00.rcutil.command.argument;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.helper.MapHelper;
import name.nkid00.rcutil.manager.ScriptManager;
import name.nkid00.rcutil.model.Script;
import net.minecraft.server.command.ServerCommandSource;

public class ScriptArgumentType implements NamedArgumentType<Script> {
    private static final Collection<String> EXAMPLES = Arrays.asList("name", "42", "_0");
    public static final DynamicCommandExceptionType SCRIPT_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
            name -> I18n.t("rcutil.argument.script.notfound", name));

    private String argumentName;

    public ScriptArgumentType() {
        this.argumentName = "";
    }

    public ScriptArgumentType(String argumentName) {
        this.argumentName = argumentName;
    }

    public static <S> Script getScript(CommandContext<S> context, String name) {
        return context.getArgument(name, Script.class);
    }

    @Override
    public Script parse(StringReader reader) throws CommandSyntaxException {
        Log.info("ScriptArgumentType::parse");
        var name = CommandHelper.getName(reader);
        if (!ScriptManager.nameExists(name)) {
            Log.info("Script not found");
            throw SCRIPT_NOT_FOUND_EXCEPTION.create(name);
        }
        return ScriptManager.scriptByName(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        ScriptManager.suggest(builder);
        return builder.buildFuture();
        // try {
        //     if (!argumentName.isEmpty()) {
        //         var s = context.getSource();
        //         if (s instanceof ServerCommandSource) {
        //             var c = (CommandContext<ServerCommandSource>) context;
        //             var used = CommandHelper.getOneOrMoreArguments(c, argumentName, ScriptArgumentType::getScript);
        //             MapHelper.forEachKeySynchronized(ScriptManager.scripts, name -> {
        //                 Log.info("name = ", name);
        //                 if (!used.stream().anyMatch(script -> script.name.equals(name))) {
        //                     Log.info("suggested");
        //                     builder.suggest(name);
        //                 }
        //                 Log.info("ignored");
        //             });
        //             return builder.buildFuture();
        //         }
        //     }
        //     MapHelper.forEachKeySynchronized(ScriptManager.scripts, builder::suggest);
        //     return builder.buildFuture();
        // } catch (Exception e) {
        //     Log.error("@ ScriptArgumentType::listSuggestions", e);
        //     throw e;
        // }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public String argumentName() {
        return this.argumentName;
    }
}
