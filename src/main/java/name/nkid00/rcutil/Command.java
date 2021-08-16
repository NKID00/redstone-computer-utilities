package name.nkid00.rcutil;

import java.util.StringJoiner;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("rcu")
            .requires((s) -> s.hasPermissionLevel(Settings.requiredPermissionLevel))
            // give wand or stop running command
            .executes(Command::executeRcu)
            .then(
                literal("ram")
                // list rams
                .executes(Command::executeRcuRam)
                .then(
                    literal("new")
                    // read-only
                    .then(
                        literal("ro")
                        .then(
                            literal("pos")
                            .then(
                                argument("name", StringArgumentType.string())
                                .executes((c) -> executeRcuRamNewRo(c, EdgeTriggering.Positive))
                            )
                        )
                        .then(
                            literal("neg")
                            .then(
                                argument("name", StringArgumentType.string())
                                .executes((c) -> executeRcuRamNewRo(c, EdgeTriggering.Negative))
                            )
                        )
                    )
                    // write-only
                    .then(
                        literal("wo")
                        .then(
                            literal("pos")
                            .then(
                                argument("name", StringArgumentType.string())
                                .executes((c) -> executeRcuRamNewWo(c, EdgeTriggering.Positive))
                            )
                        )
                        .then(
                            literal("neg")
                            .then(
                                argument("name", StringArgumentType.string())
                                .executes((c) -> executeRcuRamNewWo(c, EdgeTriggering.Negative))
                            )
                        )
                    )
                )
            )
        );
    }

    private static int executeRcu(CommandContext<ServerCommandSource> context) {
        ServerCommandSource s = context.getSource();
        if (Settings.status == Status.Idle) {
            Entity entity = s.getEntity();
            if (entity != null && entity instanceof ServerPlayerEntity) {
                if (((ServerPlayerEntity)entity).inventory.insertStack(new ItemStack(Items.PINK_DYE))) {
                    s.sendFeedback(new TranslatableText("rcutil.commands.rcu.success.item"), true);
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
            Settings.status = Status.Idle;
            s.sendFeedback(new TranslatableText("rcutil.commands.rcu.success.stop"), true);
            return 1;
        }
    }

    private static int executeRcuRam(CommandContext<ServerCommandSource> context) {
        ServerCommandSource s = context.getSource();
        if (Settings.rams.isEmpty()) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.ram.failed"));
            return 0;
        } else {
            StringJoiner stringJoiner = new StringJoiner(", ");
            Settings.rams.forEach((k, v) -> { stringJoiner.add(k); });
            int count = Settings.rams.size();
            s.sendFeedback(new TranslatableText("rcutil.commands.rcu.ram.success", count, stringJoiner.toString()), false);
            return count;
        }
    }
    
    private static int executeRcuRamNewRo(CommandContext<ServerCommandSource> context, EdgeTriggering clockEdgeTriggering) {
        ServerCommandSource s = context.getSource();
        if (Settings.status == Status.Idle) {
            return 1;
        } else {
            s.sendError(new TranslatableText("rcutil.commands.rcu.ram.new.failed.running"));
            return 0;
        }
    }

    private static int executeRcuRamNewWo(CommandContext<ServerCommandSource> context, EdgeTriggering clockEdgeTriggering) {
        ServerCommandSource s = context.getSource();
        if (Settings.status == Status.Idle) {
            return 1;
        } else {
            s.sendError(new TranslatableText("rcutil.commands.rcu.ram.new.failed.running"));
            return 0;
        }
    }
}
