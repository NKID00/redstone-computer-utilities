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

import name.nkid00.rcutil.enumeration.FileRamEdgeTriggering;
import name.nkid00.rcutil.enumeration.FileRamType;
import name.nkid00.rcutil.enumeration.Status;
import name.nkid00.rcutil.fileram.FileRam;
import name.nkid00.rcutil.fileram.FileRamBuilder;

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
                                RCUtil.fileRams.forEach((k, v) -> {
                                    builder.suggest(k);
                                });
                                return builder.buildFuture();
                            }
                        })
                        .executes(Command::executeRcuFileRamRemove)
                    )
                )
                .then(
                    literal("run")
                    .then(
                        argument("name", StringArgumentType.string())
                        .suggests(new SuggestionProvider<ServerCommandSource>(){
                            public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> c, SuggestionsBuilder builder) {
                                RCUtil.fileRams.forEach((k, v) -> {
                                    if (!v.running) {
                                        builder.suggest(k);
                                    }
                                });
                                return builder.buildFuture();
                            }
                        })
                        .executes(Command::executeRcuFileRamRun)
                    )
                )
                .then(
                    literal("stop")
                    .then(
                        argument("name", StringArgumentType.string())
                        .suggests(new SuggestionProvider<ServerCommandSource>(){
                            public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> c, SuggestionsBuilder builder) {
                                RCUtil.fileRams.forEach((k, v) -> {
                                    if (v.running) {
                                        builder.suggest(k);
                                    }
                                });
                                return builder.buildFuture();
                            }
                        })
                        .executes(Command::executeRcuFileRamStop)
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
                s.sendError(new TranslatableText("rcutil.commands.rcu.failed.notfound"));
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
        int count = RCUtil.fileRams.size();
        if (count == 0) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.info.failed"));
            return 0;
        } else {
            MutableText text = new LiteralText("");
            Iterator<String> iter = RCUtil.fileRams.keySet().iterator();
            for (int i = 0; ; i++) {
                String k = iter.next();
                text.append(RCUtil.fileRams.get(k).fancyName);
                if (i >= count) {
                    break;
                }
                text.append(", ");
            }
            s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.info.success", count, text), false);
            return count;
        }
    }
    
    private static int executeRcuFileRamInfoSingle(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        String name = StringArgumentType.getString(c, "name");
        if (RCUtil.fileRams.keySet().contains(name)) {
            s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.info.single.success", RCUtil.fileRams.get(name).fancyName), false);
            return 0;
        } else {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.failed.notfound", name));
            return 0;
        }
    }

    private static int executeRcuFileRamNew(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();

        FileRamType type = FileRamType.fromString(StringArgumentType.getString(c, "type"));
        if (type == null) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.type"));
            return 0;
        }
        FileRamEdgeTriggering edge = FileRamEdgeTriggering.fromString(StringArgumentType.getString(c, "clock edge triggering"));
        if (edge == null) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.edge"));
            return 0;
        }

        String name = StringArgumentType.getString(c, "name");
        if (RCUtil.fileRams.keySet().contains(name)) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.failed.exists", name));
            return 0;
        }
        
        if (RCUtil.status != Status.Idle) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.failed.running"));
            return 0;
        }

        FileRamBuilder builder = new FileRamBuilder();
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

        RCUtil.fileRamBuilder = builder;
        RCUtil.status = Status.FileRamNewStepAddrLsb;
        s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.new.start", builder.fancyName), true);
        s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.new.step.addrlsb", RCUtil.wandItemHoverableText), false);
        return 1;
    }

    private static int executeRcuFileRamRemove(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        String name = StringArgumentType.getString(c, "name");
        if (RCUtil.fileRams.keySet().contains(name)) {
            RCUtil.fileRams.remove(name);
            s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.remove.success", name), true);
            return 1;
        }
        s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.failed.notfound", name)); 
        return 0;
    }

    private static int executeRcuFileRamRun(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        String name = StringArgumentType.getString(c, "name");
        if (RCUtil.fileRams.keySet().contains(name)) {
            FileRam ram = RCUtil.fileRams.get(name);
            if (!ram.running) {
                ram.running = true;
                s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.run.success", ram.fancyName), true);
                return 1;
            } else {
                s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.run.failed", name));
                return 0;
            }
        }
        s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.failed.notfound", name)); 
        return 0;
    }

    private static int executeRcuFileRamStop(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        String name = StringArgumentType.getString(c, "name");
        if (RCUtil.fileRams.keySet().contains(name)) {
            FileRam ram = RCUtil.fileRams.get(name);
            if (ram.running) {
                ram.running = false;
                s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.stop.success", ram.fancyName), true);
                return 1;
            } else {
                s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.stop.failed", name));
                return 0;
            }
        }
        s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.failed.notfound", name)); 
        return 0;
    }
}
