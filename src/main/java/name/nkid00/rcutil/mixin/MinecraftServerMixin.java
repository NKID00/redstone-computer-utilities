package name.nkid00.rcutil.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.nkid00.rcutil.storage.Storage;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "save(ZZZ)Z", at = @At("HEAD"))
    private void save(boolean suppressLogs, boolean flush, boolean force, CallbackInfo ci) {
        Storage.save();
    }
}
