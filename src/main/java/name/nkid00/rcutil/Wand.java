package name.nkid00.rcutil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class Wand {
    public static TypedActionResult<ItemStack> register(PlayerEntity player, World world, Hand hand) {
        if (world.isClient) {
            return TypedActionResult.pass(ItemStack.EMPTY);
        }
        return TypedActionResult.success(ItemStack.EMPTY, true);
    }
}
