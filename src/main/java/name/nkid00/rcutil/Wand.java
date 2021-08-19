package name.nkid00.rcutil;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import name.nkid00.rcutil.enumeration.FileRamType;
import name.nkid00.rcutil.enumeration.Status;
import name.nkid00.rcutil.exception.BlockNotRedstoneWireException;
import name.nkid00.rcutil.exception.OversizedException;
import name.nkid00.rcutil.fileram.FileRam;

public class Wand {
    public static ActionResult register(PlayerEntity player, World rawWorld, Hand hand, BlockHitResult hitResult) {
        if (rawWorld.isClient || player.isSpectator() || player.getStackInHand(hand).getItem() != RCUtil.wandItem) {
            return ActionResult.PASS;
        }

        ServerCommandSource s = player.getCommandSource();

        if (RCUtil.status.isIdle()) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.failed.notfound"));
            return ActionResult.FAIL;
        }

        ServerWorld world = (ServerWorld) rawWorld;
        BlockPos pos = hitResult.getBlockPos();

        boolean check_redstone = true;
        switch (RCUtil.status) {
            case FileRamNewStepDataLsb:
            case FileRamNewStepData2Lsb:
                if (RCUtil.fileRamBuilder.fileRam.type == FileRamType.ReadOnly) {
                    check_redstone = false;
                }
            case FileRamNewStepAddrLsb:
            case FileRamNewStepAddr2Lsb:
            case FileRamNewStepClock:
                if (check_redstone && !world.getBlockState(pos).isOf(Blocks.REDSTONE_WIRE)) {
                    s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.block.target"));
                    break;
                }
            case FileRamNewStepAddrMsb:
            case FileRamNewStepDataMsb:
                if (world.getDimension() != RCUtil.fileRamBuilder.dimensionType) {
                    s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.block.dimension"));
                    break;
                }
                switch (RCUtil.status) {
                    case FileRamNewStepAddrLsb:
                        RCUtil.fileRamBuilder.addrLsb = pos;
                        RCUtil.status = Status.FileRamNewStepAddr2Lsb;
                        s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.new.step.addr2lsb", RCUtil.wandItemHoverableText), false);
                        return ActionResult.SUCCESS;
                    case FileRamNewStepAddr2Lsb:
                        RCUtil.fileRamBuilder.addr2Lsb = pos;
                        RCUtil.status = Status.FileRamNewStepAddrMsb;
                        s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.new.step.addrmsb", RCUtil.wandItemHoverableText), false);
                        return ActionResult.SUCCESS;
                    case FileRamNewStepAddrMsb:
                        RCUtil.fileRamBuilder.addrMsb = pos;
                        try {
                            switch (RCUtil.fileRamBuilder.buildAddress(world)) {
                                case WarningNotAligned:
                                    s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.warning.align"));
                                case Success:
                                    RCUtil.fileRamBuilder.fileRam.spawnAddrParticles(world);
                                    RCUtil.status = Status.FileRamNewStepDataLsb;
                                    s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.new.step.datalsb", RCUtil.wandItemHoverableText), false);
                                    return ActionResult.SUCCESS;
                                case FailedNotAligned:
                                    s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.block.align"));
                                    break;
                            }
                        } catch (BlockNotRedstoneWireException e) {
                            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.block.bit"));
                        } catch (OversizedException e) {
                            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.block.size"));
                        }
                        break;
                    case FileRamNewStepDataLsb:
                        RCUtil.fileRamBuilder.dataLsb = pos;
                        RCUtil.status = Status.FileRamNewStepData2Lsb;
                        s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.new.step.data2lsb", RCUtil.wandItemHoverableText), false);
                        return ActionResult.SUCCESS;
                    case FileRamNewStepData2Lsb:
                        RCUtil.fileRamBuilder.data2Lsb = pos;
                        RCUtil.status = Status.FileRamNewStepDataMsb;
                        s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.new.step.datamsb", RCUtil.wandItemHoverableText), false);
                        return ActionResult.SUCCESS;
                    case FileRamNewStepDataMsb:
                        RCUtil.fileRamBuilder.dataMsb = pos;
                        try {
                            switch (RCUtil.fileRamBuilder.buildData(world)) {
                                case WarningNotAligned:
                                    s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.warning.align"));
                                case Success:
                                    RCUtil.fileRamBuilder.fileRam.spawnDataParticles(world);
                                    RCUtil.status = Status.FileRamNewStepClock;
                                    s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.new.step.clock", RCUtil.wandItemHoverableText), false);
                                    return ActionResult.SUCCESS;
                                case FailedNotAligned:
                                    s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.block.align"));
                                    break;
                            }
                        } catch (BlockNotRedstoneWireException e) {
                            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.block.bit"));
                        } catch (OversizedException e) {
                            s.sendError(new TranslatableText("rcutil.commands.rcu.fileram.new.failed.block.size"));
                        }
                        break;
                    case FileRamNewStepClock:
                        RCUtil.fileRamBuilder.clock = pos;
                        FileRam ram = null;
                        try {
                            ram = RCUtil.fileRamBuilder.build(world);
                        } catch (BlockNotRedstoneWireException e) { }  // never happens
                        RCUtil.fileRams.put(ram.name, ram);
                        RCUtil.fileRamBuilder.fileRam.spawnClockParticles(world);
                        RCUtil.status = Status.Idle;
                        s.sendFeedback(new TranslatableText("rcutil.commands.rcu.fileram.new.success", ram.fancyName, ram.name), true);
                        return ActionResult.SUCCESS;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        RCUtil.status = Status.Idle;
        return ActionResult.FAIL;
    }
}
