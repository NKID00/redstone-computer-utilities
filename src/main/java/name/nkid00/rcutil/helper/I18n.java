package name.nkid00.rcutil.helper;

import java.util.UUID;

import name.nkid00.rcutil.manager.LanguageManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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

    public static String s(Language language, String key, Object... args) {
        return TextHelper.translatable(language.get(key), args).toString();
    }

    public static String s(String key, Object... args) {
        return s(LanguageManager.defaultLanguage(), key, args);
    }

    public static String s(UUID uuid, String key, Object... args) {
        return s(LanguageManager.languageOrDefault(uuid), key, args);
    }

    public static void overlay(ServerPlayerEntity player, Text message) {
        player.sendMessage(message, true);
    }

    public static void overlay(ServerPlayerEntity player, String key, Object... args) {
        overlay(player, t(player.getUuid(), key, args));
    }

    public static void overlayError(ServerPlayerEntity player, Text message) {
        overlay(player, TextHelper.formatted(message, Formatting.RED));
    }

    public static void overlayError(ServerPlayerEntity player, String key, Object... args) {
        overlayError(player, t(player.getUuid(), key, args));
    }

    public static void send(ServerPlayerEntity player, Text message) {
        player.sendMessage(message, false);
    }

    public static void send(ServerPlayerEntity player, String key, Object... args) {
        send(player, t(player.getUuid(), key, args));
    }

    public static void sendError(ServerPlayerEntity player, Text message) {
        send(player, TextHelper.formatted(message, Formatting.RED));
    }

    public static void sendError(ServerPlayerEntity player, String key, Object... args) {
        sendError(player, t(player.getUuid(), key, args));
    }
}
