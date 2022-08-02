package name.nkid00.rcutil.helper;

import name.nkid00.rcutil.compat.TextCompat;
import net.minecraft.text.MutableText;

public class I18n {
    public static MutableText t(String key, Object... args) {
        return TextCompat.translatable(key, args);
    }
}
