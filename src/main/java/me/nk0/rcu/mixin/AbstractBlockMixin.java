package me.nk0.rcu.mixin;

import me.nk0.rcu.manager.InterfaceManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(method = "neighborUpdate(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;Z)V", at = @At("HEAD"))
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos,
                               boolean notify, CallbackInfo ci) {
        if (state.isOf(Blocks.TARGET) && (!world.isClient)) {
            var indexedInterfaces = InterfaceManager.interfaceByBlockPos((ServerWorld) world, pos);
            if (!indexedInterfaces.isEmpty()) {
                for (var indexedInterface : indexedInterfaces) {
                    indexedInterface.object().targetBlockNeighborUpdate(pos, indexedInterface.index());
                }
            }
        }
    }
}
