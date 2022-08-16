package name.nkid00.rcutil.manager;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import name.nkid00.rcutil.exception.LanguageNotFoundException;
import name.nkid00.rcutil.helper.Log;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Language;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

public class LanguageManager {
    private static Language DEFAULT_LANGUAGE = null;

    private static ConcurrentHashMap<String, Language> languages = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<UUID, String> playerLanguages = new ConcurrentHashMap<>();

    public static Language defaultLanguage() {
        if (DEFAULT_LANGUAGE == null) {
            try {
                DEFAULT_LANGUAGE = language(Language.DEFAULT_LANGUAGE);
            } catch (LanguageNotFoundException e) {
                throw new CrashException(new CrashReport("Failed to load default language", e));
            }
        }
        return DEFAULT_LANGUAGE;
    }

    public static Language language(String langCode) throws LanguageNotFoundException {
        if (languages.containsKey(langCode)) {
            return languages.get(langCode);
        }
        InputStream inputStream = Language.class.getResourceAsStream("/assets/rcutil/lang/%s.json".formatted(langCode));
        if (inputStream == null) {
            throw new LanguageNotFoundException();
        }
        var map = new ConcurrentHashMap<String, String>();
        Language.load(inputStream, map::put);
        var language = new Language() {
            @Override
            public String get(String key) {
                return map.getOrDefault(key, key);
            }

            @Override
            public boolean hasTranslation(String key) {
                return map.containsKey(key);
            }

            @Override
            public boolean isRightToLeft() {
                return false;
            }

            @Override
            public OrderedText reorder(StringVisitable text) {
                return null;
            }

        };
        languages.put(langCode, language);
        return language;
    }

    public static Language language(UUID uuid) throws LanguageNotFoundException {
        return language(playerLanguages.computeIfAbsent(uuid, _uuid -> Language.DEFAULT_LANGUAGE));
    }

    public static Language languageOrDefault(UUID uuid) {
        try {
            return language(uuid);
        } catch (LanguageNotFoundException e) {
            return defaultLanguage();
        }
    }

    public static void setLanguage(UUID uuid, String langCode) throws LanguageNotFoundException {
        language(langCode);
        playerLanguages.put(uuid, langCode);
    }

    public static void load(JsonReader reader, Gson gson) {
        try {
            playerLanguages = gson.fromJson(reader,
                    new TypeToken<ConcurrentHashMap<UUID, String>>() {
                    }.getType());
        } catch (JsonParseException e) {
            Log.error("Error occurred when loading languages, generating empty record", e);
            playerLanguages = new ConcurrentHashMap<>();
        }
    }

    public static void save(JsonWriter writer, Gson gson) {
        try {
            gson.toJson(playerLanguages, new TypeToken<ConcurrentHashMap<UUID, String>>() {
            }.getType(), writer);
        } catch (JsonParseException e) {
            Log.error("Error occurred when saving languages", e);
        }
    }
}