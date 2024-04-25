package name.nkid00.rcutil.helper

import name.nkid00.rcutil.helper.TextHelper.formatted
import name.nkid00.rcutil.helper.TextHelper.translatable
import name.nkid00.rcutil.manager.LanguageManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Language
import java.util.*

object I18n {
    /**
     * Convenient method to construct a text with the language.
     */
    fun t(language: Language, key: String?, vararg args: Any?): MutableText {
        return translatable(language[key], *args)
    }

    /**
     * Convenient method to construct a text in the default language.
     */
    fun t(key: String?, vararg args: Any?): MutableText {
        return t(LanguageManager.defaultLanguage(), key, *args)
    }

    /**
     * Convenient method to construct a text for the player.
     *
     * @param uuid uuid of the player.
     */
    @JvmStatic
    fun t(uuid: UUID?, key: String?, vararg args: Any?): MutableText {
        return t(LanguageManager.languageOrDefault(uuid), key, *args)
    }

    /**
     * Convenient method to construct a plain String with the language.
     */
    fun s(language: Language, key: String?, vararg args: Any?): String {
        return translatable(language[key], *args).toString()
    }

    /**
     * Convenient method to construct a plain String in the default language.
     */
    fun s(key: String?, vararg args: Any?): String {
        return s(LanguageManager.defaultLanguage(), key, *args)
    }

    /**
     * Convenient method to construct a plain String for the player.
     *
     * @param uuid uuid of the player.
     */
    fun s(uuid: UUID?, key: String?, vararg args: Any?): String {
        return s(LanguageManager.languageOrDefault(uuid), key, *args)
    }

    fun overlay(player: ServerPlayerEntity, message: Text?) {
        player.sendMessage(message, true)
    }

    @JvmStatic
    fun overlay(player: ServerPlayerEntity, key: String?, vararg args: Any?) {
        overlay(player, t(player.uuid, key, *args))
    }

    fun overlayError(player: ServerPlayerEntity, message: Text?) {
        overlay(player, formatted(message, Formatting.RED))
    }

    @JvmStatic
    fun overlayError(player: ServerPlayerEntity, key: String?, vararg args: Any?) {
        overlayError(player, t(player.uuid, key, *args))
    }

    fun send(player: ServerPlayerEntity, message: Text?) {
        player.sendMessage(message)
    }

    fun send(player: ServerPlayerEntity, key: String?, vararg args: Any?) {
        send(player, t(player.uuid, key, *args))
    }

    fun sendError(player: ServerPlayerEntity, message: Text?) {
        send(player, formatted(message, Formatting.RED))
    }

    fun sendError(player: ServerPlayerEntity, key: String?, vararg args: Any?) {
        sendError(player, t(player.uuid, key, *args))
    }

    @JvmStatic
    fun sendFeedback(s: ServerCommandSource, broadcastToOps: Boolean, message: Text?) {
        s.sendFeedback(message, broadcastToOps)
    }

    @JvmStatic
    fun sendFeedback(s: ServerCommandSource, broadcastToOps: Boolean, key: String?, vararg args: Any?) {
        s.sendFeedback(t(CommandHelper.uuidOrNull(s), key, *args), broadcastToOps)
    }

    @JvmStatic
    fun sendError(s: ServerCommandSource, message: Text?) {
        s.sendError(message)
    }

    @JvmStatic
    fun sendError(s: ServerCommandSource, key: String?, vararg args: Any?) {
        s.sendError(t(CommandHelper.uuidOrNull(s), key, *args))
    }
}
