package name.nkid00.rcutil.mixin;

import java.io.InputStream;
import java.util.function.BiConsumer;

import net.minecraft.util.Language;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import name.nkid00.rcutil.RCUtil;

@Mixin(Language.class)
public abstract class LanguageMixin {
    private static boolean languageLoaded = false;

    @Redirect(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Language;load(Ljava/io/InputStream;Ljava/util/function/BiConsumer;)V"))
    private static void loadLanguageFileForDedicatedServer(InputStream inputStream,
            BiConsumer<String, String> entryConsumer) {
        Language.load(inputStream, entryConsumer);
        if (RCUtil.isDedicatedServer && !languageLoaded) {
            InputStream languageFileStream = Language.class.getResourceAsStream("/assets/rcutil/lang/en_us.json");
            Language.load(languageFileStream, entryConsumer);
            languageLoaded = true;
        }
    }
}
