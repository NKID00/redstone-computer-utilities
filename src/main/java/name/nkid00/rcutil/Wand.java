package name.nkid00.rcutil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import name.nkid00.rcutil.command.CommandStatus;

public class Wand {
    public static ActionResult onUse(PlayerEntity player, World rawWorld, Hand hand, BlockHitResult hitResult) {
        if (rawWorld.isClient || player.isSpectator() || player.getStackInHand(hand).getItem() != RCUtil.wandItem) {
            return ActionResult.PASS;
        }

        var s = player.getCommandSource();
        var uuid = player.getUuid();
        var status = RCUtil.getCommandStatus(uuid);

        if (status.isIdle()) {
            s.sendError(new TranslatableText("rcutil.wand.failed.notfound"));
            return ActionResult.FAIL;
        }

        var world = (ServerWorld) rawWorld;
        var pos = hitResult.getBlockPos();
        if (!TargetBlockUtil.isTargetBlock(world, pos)) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.new.component.failed.target"));
            RCUtil.setCommandStatus(uuid, CommandStatus.Idle);
            return ActionResult.FAIL;
        }

        if (status.isRunningRcuNewWires()) {
            var builder = RCUtil.getWiresBuilder(uuid);
            if (status.equals(CommandStatus.RcuNewWiresLsb)) {
                builder.dimensionType = world.getDimension();
            } else if (!builder.dimensionType.equals(world.getDimension())) {
                s.sendError(new TranslatableText("rcutil.commands.rcu.new.component.failed.dimension"));
                RCUtil.setCommandStatus(uuid, CommandStatus.Idle);
                return ActionResult.FAIL;
            }
            switch (status) {
                case RcuNewWiresLsb:
                    builder.lsb = pos;
                    s.sendFeedback(new TranslatableText("rcutil.commands.rcu.new.wires.step.2lsb",
                            RCUtil.wandItemHoverableText), false);
                    RCUtil.setCommandStatus(uuid, CommandStatus.RcuNewWiresSecondLsb);
                    return ActionResult.SUCCESS;
                case RcuNewWiresSecondLsb:
                    builder.secondLsb = pos;
                    s.sendFeedback(new TranslatableText("rcutil.commands.rcu.new.wires.step.msb",
                            RCUtil.wandItemHoverableText), false);
                    RCUtil.setCommandStatus(uuid, CommandStatus.RcuNewWiresMsb);
                    return ActionResult.SUCCESS;
                case RcuNewWiresMsb:
                    builder.msb = pos;
                    var wires = builder.build();
                    if (wires == null) {
                        s.sendError(new TranslatableText("rcutil.commands.rcu.new.component.failed.notaligned"));
                        return ActionResult.FAIL;
                    }
                    RCUtil.getWires(uuid).put(wires.name, wires);
                    s.sendFeedback(new TranslatableText("rcutil.commands.rcu.new.component.success", wires.name), true);
                    RCUtil.setCommandStatus(uuid, CommandStatus.Idle);
                    return ActionResult.SUCCESS;
                default:
                    RCUtil.setCommandStatus(uuid, CommandStatus.Idle);
                    return ActionResult.FAIL;
            }
        } else if (status.isRunningRcuNewBus()) {
            var builder = RCUtil.getBusBuilder(uuid);
        } else if (status.isRunningRcuNewAddrbus()) {
            var builder = RCUtil.getAddrbusBuilder(uuid);
        }

        return ActionResult.FAIL;
    }
}
