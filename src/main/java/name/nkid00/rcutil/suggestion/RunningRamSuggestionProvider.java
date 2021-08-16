package name.nkid00.rcutil.suggestion;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.server.command.ServerCommandSource;

import name.nkid00.rcutil.RCUtil;

public class RunningRamSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        RCUtil.roRams.forEach((k, v) -> {
            if (v.running) {
                builder.suggest(k);
            }
        });
        RCUtil.woRams.forEach((k, v) -> {
            if (v.running) {
                builder.suggest(k);
            }
        });
        return builder.buildFuture();
    }
}
