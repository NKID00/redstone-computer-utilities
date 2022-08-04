package name.nkid00.rcutil;

import name.nkid00.rcutil.storage.Options;
import name.nkid00.rcutil.storage.Storage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Wand {
    // select msb
    public static ActionResult onBlockAttack(PlayerEntity player, World world, Hand hand, BlockPos pos,
            Direction direction) {
        if (world.isClient || player.isSpectator() || !player.getStackInHand(hand).isOf(Options.wandItem)) {
            return ActionResult.PASS;
        }
        Storage.getPlayerData(player.getUuid()).selection.selectMsb(pos, world.getDimension());
        return ActionResult.FAIL;
    }

    // select lsb
    public static ActionResult onBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient || player.isSpectator() || !player.getStackInHand(hand).isOf(Options.wandItem)) {
            return ActionResult.PASS;
        }
        var pos = hitResult.getBlockPos();
        Storage.getPlayerData(player.getUuid()).selection.selectLsb(pos, world.getDimension());
        return ActionResult.FAIL;
    }

    // previous selecetion
    public static ActionResult onItemAttack(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (world.isClient || player.isSpectator() || !player.getStackInHand(hand).isOf(Options.wandItem) || entity != null || hitResult != null) {
            return ActionResult.PASS;
        }
        Storage.getPlayerData(player.getUuid()).selection.previousSelection();
        return ActionResult.FAIL;
    }

    // next selecetion
    public static TypedActionResult<ItemStack> onItemUse(PlayerEntity player, World world, Hand hand) {
        var itemStack = player.getStackInHand(hand);
        if (world.isClient || player.isSpectator() || !itemStack.isOf(Options.wandItem)) {
            return TypedActionResult.pass(itemStack);
        }
        Storage.getPlayerData(player.getUuid()).selection.nextSelection();
        return TypedActionResult.fail(itemStack);
    }
}
