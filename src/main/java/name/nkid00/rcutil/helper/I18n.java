package name.nkid00.rcutil.helper;

import java.util.UUID;

import name.nkid00.rcutil.manager.LanguageManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;

public class I18n {
    /**
     * Convenient method to construct a text with the language.
     */
    public static MutableText t(Language language, String key, Object... args) {
        return TextHelper.translatable(language.get(key), args);
    }

    /**
     * Convenient method to construct a text in the default language.
     */
    public static MutableText t(String key, Object... args) {
        return t(LanguageManager.defaultLanguage(), key, args);
    }

    /**
     * Convenient method to construct a text for the player.
     * 
     * @param uuid uuid of the player.
     */
    public static MutableText t(UUID uuid, String key, Object... args) {
        return t(LanguageManager.languageOrDefault(uuid), key, args);
    }

    /**
     * Convenient method to construct a plain String with the language.
     */
    public static String s(Language language, String key, Object... args) {
        return TextHelper.translatable(language.get(key), args).toString();
    }

    /**
     * Convenient method to construct a plain String in the default language.
     */
    public static String s(String key, Object... args) {
        return s(LanguageManager.defaultLanguage(), key, args);
    }

    /**
     * Convenient method to construct a plain String for the player.
     * 
     * @param uuid uuid of the player.
     */
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
        player.sendMessage(message);
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

    public static void sendFeedback(ServerCommandSource s, boolean broadcastToOps, Text message) {
        s.sendFeedback(() -> message, broadcastToOps);
    }

    public static void sendFeedback(ServerCommandSource s, boolean broadcastToOps, String key, Object... args) {
        s.sendFeedback(() -> I18n.t(CommandHelper.uuidOrNull(s), key, args), broadcastToOps);
    }

    public static void sendError(ServerCommandSource s, Text message) {
        s.sendError(message);
    }

    public static void sendError(ServerCommandSource s, String key, Object... args) {
        s.sendError(I18n.t(CommandHelper.uuidOrNull(s), key, args));
    }
}
