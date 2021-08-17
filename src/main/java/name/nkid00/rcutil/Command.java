package name.nkid00.rcutil;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import name.nkid00.rcutil.enumeration.RamBusEdgeTriggering;
import name.nkid00.rcutil.enumeration.RamBusType;
import name.nkid00.rcutil.enumeration.Status;
import name.nkid00.rcutil.rambus.RamBus;
import name.nkid00.rcutil.rambus.RamBusBuilder;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(
            literal("rcu")
            .requires((s) -> s.hasPermissionLevel(RCUtil.requiredPermissionLevel))
            // give wand or stop running command
            .executes(Command::executeRcu)
            .then(
                literal("fileram")
                .executes(Command::executeRcuFileRamInfo)
                .then(
                    literal("info")
                    .executes(Command::executeRcuFileRamInfo)
                    .then(
                        argument("name", StringArgumentType.string())
                        .executes(Command::executeRcuFileRamInfoSingle)
                    )
                )
                .then(
                    literal("new")
                    .then(
                        argument("type", StringArgumentType.word())
                        .suggests(new SuggestionProvider<ServerCommandSource>(){
                            public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> c, SuggestionsBuilder builder) {
                                builder.suggest("ro");
                                builder.suggest("wo");
                                return builder.buildFuture();
                            }
                        })
                        .then(
                            argument("clock edge triggering", StringArgumentType.word())
                            .suggests(new SuggestionProvider<ServerCommandSource>(){
                                public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> c, SuggestionsBuilder builder) {
                                    builder.suggest("pos");
                                    builder.suggest("neg");
                                    builder.suggest("dual");
                                    return builder.buildFuture();
                                }
                            })
                            .then(
                                argument("name", StringArgumentType.string())
                                .then(
                                    argument("file", StringArgumentType.string())
                                    .executes((c) -> executeRcuFileRamNew(c))
                                )
                            )
                        )
                    )
                )
                .then(
                    literal("remove")
                    .then(
                        argument("name", StringArgumentType.string())
                        .suggests(new SuggestionProvider<ServerCommandSource>(){
                            public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> c, SuggestionsBuilder builder) {
                                RCUtil.rams.forEach((k, v) -> {
                                    builder.suggest(k);
                                });
                                return builder.buildFuture();
                            }
                        })
                        .executes(Command::executeRcuFileRamRemove)
                    )
                )
            )
        );
    }

    private static int executeRcu(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        if (RCUtil.status == Status.Idle) {
            Entity entity = s.getEntity();
            if (entity != null && entity instanceof ServerPlayerEntity) {
                if (((ServerPlayerEntity)entity).inventory.insertStack(new ItemStack(RCUtil.wandItem))) {
                    s.sendFeedback(new TranslatableText("rcutil.commands.rcu.success.item", RCUtil.wandItemHoverableText), true);
                    return 1;
                } else {
                    s.sendError(new TranslatableText("rcutil.commands.rcu.failed.item"));
                    return 0;
                }
            } else {
                s.sendError(new TranslatableText("rcutil.commands.rcu.failed.stop"));
                return 0;
            }
        } else {
            RCUtil.status = Status.Idle;
            s.sendFeedback(new TranslatableText("rcutil.commands.rcu.success.stop"), true);
            return 1;
        }
    }

    private static int executeRcuFileRamInfo(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        int count = RCUtil.rams.size();
        if (count == 0) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.failed"));
            return 0;
        } else {
            MutableText text = new LiteralText("");
            Iterator<String> iter = RCUtil.rams.keySet().iterator();
            for (int i = 0; ; i++) {
                String k = iter.next();
                RamBus v = RCUtil.rams.get(k);
                MutableText t = v.fancyName.copy();
                if (v.running) {
                    t.formatted(Formatting.BOLD, Formatting.UNDERLINE);
                }
                text.append(t);
                if (i >= count) {
                    break;
                }
                text.append(", ");
            }
            s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.success", count, text), false);
            return count;
        }
    }
    
    private static int executeRcuFileRamInfoSingle(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        String name = StringArgumentType.getString(c, "name");
        if (RCUtil.rams.keySet().contains(name)) {
            s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.info.single.success", RCUtil.rams.get(name).fancyName.copy()), false);
            return 0;
        } else {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.info.single.failed", name));
            return 0;
        }
    }

    private static int executeRcuFileRamNew(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();

        RamBusType type = RamBusType.fromString(StringArgumentType.getString(c, "type"));
        if (type == null) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.type"));
            return 0;
        }
        RamBusEdgeTriggering edge = RamBusEdgeTriggering.fromString(StringArgumentType.getString(c, "clock edge triggering"));
        if (edge == null) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.edge"));
            return 0;
        }

        String name = StringArgumentType.getString(c, "name");
        if (RCUtil.rams.keySet().contains(name)) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.name", name));
            return 0;
        }
        
        if (RCUtil.status != Status.Idle) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.failed.running"));
            return 0;
        }

        RamBusBuilder builder = new RamBusBuilder();
        builder.type = type;

        String file = StringArgumentType.getString(c, "file");
        try {
            if (!builder.setFile(file)) {
                s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.ro.failed.file", file));
                return 0;
            }
        } catch (IOException e) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.io"));
            return 0;
        }

        builder.name = name;
        builder.clockEdgeTriggering = edge;
        builder.buildFancyName();

        RCUtil.ramBusBuilder = builder;
        RCUtil.status = Status.RamNew1;
        s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.new.start", builder.fancyName), true);
        s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.new.step1", RCUtil.wandItemHoverableText), false);
        return 1;
    }

    private static int executeRcuFileRamRemove(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        String name = StringArgumentType.getString(c, "name");
        if (RCUtil.rams.keySet().contains(name)) {
            RCUtil.rams.remove(name);
            s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.remove.success", name), true);
            return 1;
        }
        s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.remove.failed", name)); 
        return 1;
    }
}
