package name.nkid00.rcutil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Wand {
    // select msb
    public static ActionResult onAttack(PlayerEntity player, World world, Hand hand, BlockPos pos,
            Direction direction) {
        if (world.isClient || player.isSpectator() || !player.getStackInHand(hand).isOf(Options.wandItem())) {
            return ActionResult.PASS;
        }
        Storage.selection(player.getUuid()).selectMsb(pos, world.getDimension());
        return ActionResult.FAIL;
    }

    // select lsb
    public static ActionResult onUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient || player.isSpectator() || !player.getStackInHand(hand).isOf(Options.wandItem())) {
            return ActionResult.PASS;
        }
        var pos = hitResult.getBlockPos();
        Storage.selection(player.getUuid()).selectLsb(pos, world.getDimension());
        return ActionResult.FAIL;
    }
}
