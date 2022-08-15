package name.nkid00.rcutil;

import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.manager.SelectionManager;
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
        var uuid = player.getUuid();
        SelectionManager.selectMsb(uuid, pos, world.getDimension());
        if (SelectionManager.selected(uuid)) {
            player.sendMessage(I18n.t("rcutil.select.selected"));
        } else {
            player.sendMessage(I18n.t("rcutil.select.msb.success"));
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
        SelectionManager.selectLsb(uuid, pos, world.getDimension());
        if (SelectionManager.selected(uuid)) {
            player.sendMessage(I18n.t("rcutil.select.selected"));
        } else {
            player.sendMessage(I18n.t("rcutil.select.lsb.success"));
        }
        return ActionResult.FAIL;
    }
}
