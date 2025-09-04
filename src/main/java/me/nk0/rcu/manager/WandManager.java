package me.nk0.rcu.manager;

import me.nk0.rcu.Options;
import me.nk0.rcu.helper.I18n;
import me.nk0.rcu.helper.TargetBlockHelper;
import me.nk0.rcu.util.TargetBlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WandManager {
    // select msb
    public static ActionResult onAttack(PlayerEntity player, World world, Hand hand, BlockPos pos,
                                        Direction direction) {
        if (world.isClient || player.isSpectator() || !player.getStackInHand(hand).isOf(Options.wandItem())) {
            return ActionResult.PASS;
        }
        var uuid = player.getUuid();
        var serverWorld = (ServerWorld) world;
        var ServerPlayerEntity = (ServerPlayerEntity) player;
        if (TargetBlockHelper.is(serverWorld, pos)) {
            SelectionManager.selectMsb(uuid, pos, serverWorld);
            I18n.overlay(ServerPlayerEntity, "rcutil.select.msb", new TargetBlockPos(serverWorld, pos));
        } else {
            I18n.overlayError(ServerPlayerEntity, "rcutil.select.not_target_block");
        }
        return ActionResult.FAIL;
    }

    // select lsb
    public static ActionResult onUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient || player.isSpectator() || !player.getStackInHand(hand).isOf(Options.wandItem())) {
            return ActionResult.PASS;
        }
        var pos = hitResult.getBlockPos();
        var uuid = player.getUuid();
        var serverWorld = (ServerWorld) world;
        var ServerPlayerEntity = (ServerPlayerEntity) player;
        if (TargetBlockHelper.is(serverWorld, pos)) {
            SelectionManager.selectLsb(uuid, pos, serverWorld);
            I18n.overlay(ServerPlayerEntity, "rcutil.select.lsb", new TargetBlockPos(serverWorld, pos));
        } else {
            I18n.overlayError(ServerPlayerEntity, "rcutil.select.not_target_block");
        }
        return ActionResult.FAIL;
    }
}
