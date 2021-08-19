package name.nkid00.rcutil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

import name.nkid00.rcutil.enumeration.FileRamEdgeTriggering;
import name.nkid00.rcutil.enumeration.FileRamFileByteOrder;
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
                        .suggests(new SuggestionProvider<ServerCommandSource>(){
                            public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> c, SuggestionsBuilder builder) {
                                RCUtil.fileRams.forEach((k, v) -> {
                                    builder.suggest(k);
                                });
                                return builder.buildFuture();
                            }
                        })
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
                            argument("clock triggering edge", StringArgumentType.word())
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
                                    .executes(Command::executeRcuFileRamNew)
                                    .then(
                                        argument("byte order", StringArgumentType.word())
                                        .suggests(new SuggestionProvider<ServerCommandSource>(){
                                            public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> c, SuggestionsBuilder builder) {
                                                builder.suggest("le");
                                                builder.suggest("be");
                                                return builder.buildFuture();
                                            }
                                        })
                                        .executes(Command::executeRcuFileRamNew)
                                    )
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
                    literal("start")
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
                        .executes(Command::executeRcuFileRamStart)
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
                .then(
                    literal("newfile")
                    .then(
                        argument("file", StringArgumentType.string())
                        .then(
                            argument("length in bytes", LongArgumentType.longArg(0))
                            .executes(Command::executeRcuFileRamNewFile)
                        )
                    )
                )
                .then(
                    literal("removefile")
                    .then(
                        argument("file", StringArgumentType.string())
                        .executes(Command::executeRcuFileRamRemoveFile)
                    )
                )
            )
        );
    }

    public static int executeRcu(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        if (RCUtil.status.isIdle()) {
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

    public static int executeRcuFileRamInfo(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        int count = RCUtil.fileRams.size();
        if (count == 0) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.info.failed"));
            return 0;
        } else {
            MutableText text = new LiteralText("");
            Iterator<String> iter = RCUtil.fileRams.keySet().iterator();
            ServerWorld world = s.getWorld();
            for (int i = 1; ; i++) {
                String k = iter.next();
                FileRam v = RCUtil.fileRams.get(k);
                text.append(v.fancyName);
                v.spawnAddrParticles(world);
                v.spawnDataParticles(world);
                v.spawnClockParticles(world);
                if (i >= count) {
                    break;
                }
                text.append(", ");
            }
            s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.info.success", count, text), false);
            return count;
        }
    }
    
    public static int executeRcuFileRamInfoSingle(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        String name = StringArgumentType.getString(c, "name");
        if (RCUtil.fileRams.keySet().contains(name)) {
            ServerWorld world = s.getWorld();
            FileRam ram = RCUtil.fileRams.get(name);
            ram.spawnAddrParticles(world);
            ram.spawnDataParticles(world);
            ram.spawnClockParticles(world);
            s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.info.single.success", ram.fancyName), false);
            return 0;
        } else {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.failed.notfound", name));
            return 0;
        }
    }

    public static int executeRcuFileRamNew(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();

        FileRamType type = FileRamType.fromString(StringArgumentType.getString(c, "type"));
        if (type == null) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.type"));
            return 0;
        }
        FileRamEdgeTriggering edge = FileRamEdgeTriggering.fromString(StringArgumentType.getString(c, "clock triggering edge"));
        if (edge == null) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.edge"));
            return 0;
        }
        FileRamFileByteOrder byteOrder = FileRamFileByteOrder.LittleEndian;
        try {
            byteOrder = FileRamFileByteOrder.fromString(StringArgumentType.getString(c, "byte order"));
        } catch (IllegalArgumentException e) { }

        String name = StringArgumentType.getString(c, "name");
        if (RCUtil.fileRams.keySet().contains(name)) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.failed.exists", name));
            return 0;
        }
        
        if (!RCUtil.status.isIdle()) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.failed.running"));
            return 0;
        }

        FileRamBuilder builder = new FileRamBuilder();
        builder.type = type;

        String file = StringArgumentType.getString(c, "file");
        try {
            if (!builder.setFile(file)) {
                s.sendError(new TranslatableText("rcutil.commands.rcu.failed.file.notfound", file));
                return 0;
            }
        } catch (IOException e) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.io"));
            return 0;
        }

        builder.name = name;
        builder.dimensionType = s.getWorld().getDimension();
        builder.clockEdgeTriggering = edge;
        builder.fileByteOrder = byteOrder;
        builder.buildFancyName();

        RCUtil.fileRamBuilder = builder;
        RCUtil.status = Status.FileRamNewStepAddrLsb;
        s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.new.start", builder.fancyName), true);
        s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.new.step.addrlsb", RCUtil.wandItemHoverableText), false);
        return 1;
    }

    public static int executeRcuFileRamRemove(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        String name = StringArgumentType.getString(c, "name");
        if (RCUtil.status.isRunningFileRamNew() && RCUtil.fileRamBuilder.name.equals(name)) {
            RCUtil.status = Status.Idle;
            s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.remove.success.builder", RCUtil.fileRamBuilder.fancyName), true);
            return 1;
        }
        if (RCUtil.fileRams.keySet().contains(name)) {
            RCUtil.fileRams.remove(name);
            s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.remove.success", name), true);
            return 1;
        }
        s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.failed.notfound", name)); 
        return 0;
    }

    public static int executeRcuFileRamStart(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        String name = StringArgumentType.getString(c, "name");
        if (RCUtil.fileRams.keySet().contains(name)) {
            FileRam ram = RCUtil.fileRams.get(name);
            if (!ram.running) {
                ram.setRunning(true);
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

    public static int executeRcuFileRamStop(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        String name = StringArgumentType.getString(c, "name");
        if (RCUtil.fileRams.keySet().contains(name)) {
            FileRam ram = RCUtil.fileRams.get(name);
            if (ram.running) {
                ram.setRunning(false);
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

    public static int executeRcuFileRamNewFile(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        String filename = StringArgumentType.getString(c, "file");
        long length = LongArgumentType.getLong(c, "length in bytes");
        File file = new File(RCUtil.fileRamBaseDirectory, filename);
        if (file.exists()) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.failed.file.exists"));
            return 0;
        }
        try {
            file.createNewFile();
            FileOutputStream stream = new FileOutputStream(file);
            for (int i = 0; i < length; i++) {
                stream.write(0);
            }
            stream.close();
        } catch (Exception e) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.io"));
            return 0;
        }
        s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.newfile.success", filename), true);
        return 1;
    }

    public static int executeRcuFileRamRemoveFile(CommandContext<ServerCommandSource> c) {
        ServerCommandSource s = c.getSource();
        String filename = StringArgumentType.getString(c, "file");
        File file = new File(RCUtil.fileRamBaseDirectory, filename);
        if (!file.exists()) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.failed.file.notfound"));
            return 0;
        }
        if (!file.delete()) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.io"));
            return 0;
        }
        s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.removefile.success", filename), true);
        return 1;
    }
}
