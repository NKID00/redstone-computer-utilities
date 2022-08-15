package name.nkid00.rcutil.helper;

import java.util.UUID;

import name.nkid00.rcutil.manager.LanguageManager;
import net.minecraft.text.MutableText;
import net.minecraft.util.Language;

public class I18n {
    public static MutableText t(Language language, String key, Object... args) {
        return TextHelper.translatable(language.get(key), args);
    }

    public static MutableText t(String key, Object... args) {
        return t(LanguageManager.defaultLanguage(), key, args);
    }

    public static MutableText t(UUID uuid, String key, Object... args) {
        return t(LanguageManager.languageOrDefault(uuid), key, args);
    }
}
