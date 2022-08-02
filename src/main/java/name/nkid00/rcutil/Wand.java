package name.nkid00.rcutil;

import name.nkid00.rcutil.storage.Options;
import name.nkid00.rcutil.storage.Storage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Wand {
    // select lsb
    public static ActionResult onAttack(PlayerEntity player, World world, Hand hand, BlockPos pos,
            Direction direction) {
        if (world.isClient || player.isSpectator() || !player.getStackInHand(hand).isOf(Options.wandItem)) {
            return ActionResult.PASS;
        }
        Storage.getPlayerData(player.getUuid()).selection().selectLsb(pos, world.getDimension());
        return ActionResult.FAIL;
    }

    // select msb
    public static ActionResult onUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient || player.isSpectator() || !player.getStackInHand(hand).isOf(Options.wandItem)) {
            return ActionResult.PASS;
        }
        var pos = hitResult.getBlockPos();
        Storage.getPlayerData(player.getUuid()).selection().selectMsb(pos, world.getDimension());
        return ActionResult.FAIL;
    }
}
