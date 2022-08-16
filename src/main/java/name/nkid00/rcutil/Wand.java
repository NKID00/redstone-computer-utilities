package name.nkid00.rcutil;

import name.nkid00.rcutil.helper.BlockPosHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.helper.RegistryHelper;
import name.nkid00.rcutil.helper.TextHelper;
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
        var dimension = world.getDimension();
        SelectionManager.selectMsb(uuid, pos, dimension);
        player.sendMessage(I18n.t("rcutil.select.msb", BlockPosHelper.toString(pos), RegistryHelper.toString(dimension)));
        return ActionResult.FAIL;
    }

    // select lsb
    public static ActionResult onUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient || player.isSpectator() || !player.getStackInHand(hand).isOf(Options.wandItem())) {
            return ActionResult.PASS;
        }
        var pos = hitResult.getBlockPos();
        var uuid = player.getUuid();
        var dimension = world.getDimension();
        SelectionManager.selectLsb(uuid, pos, dimension);
        player.sendMessage(I18n.t("rcutil.select.lsb", BlockPosHelper.toString(pos), RegistryHelper.toString(dimension)));
        return ActionResult.FAIL;
    }
}
