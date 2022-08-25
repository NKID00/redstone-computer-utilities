package name.nkid00.rcutil.manager;

import name.nkid00.rcutil.Options;
import name.nkid00.rcutil.helper.BlockPosHelper;
import name.nkid00.rcutil.helper.I18n;
import net.minecraft.entity.player.PlayerEntity;
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
        SelectionManager.selectMsb(uuid, pos, (ServerWorld)world);
        player.sendMessage(I18n.t("rcutil.select.msb", BlockPosHelper.toString(pos), world.getRegistryKey().getValue().toString()));
        return ActionResult.FAIL;
    }

    // select lsb
    public static ActionResult onUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient || player.isSpectator() || !player.getStackInHand(hand).isOf(Options.wandItem())) {
            return ActionResult.PASS;
        }
        var pos = hitResult.getBlockPos();
        var uuid = player.getUuid();
        SelectionManager.selectLsb(uuid, pos, (ServerWorld)world);
        player.sendMessage(I18n.t("rcutil.select.lsb", BlockPosHelper.toString(pos), world.getRegistryKey().getValue().toString()));
        return ActionResult.FAIL;
    }
}
