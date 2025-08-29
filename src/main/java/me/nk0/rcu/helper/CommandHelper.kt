package me.nk0.rcu.helper

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.rcon.RconCommandOutput
import java.util.*

object CommandHelper {
    private val NOT_PLAYER_ENTITY_EXCEPTION = SimpleCommandExceptionType(
            I18n.t("rcutil.command.fail.not_player_entity"))

    fun isLetterDigitUnderline(c: Char): Boolean {
        return Character.isLetterOrDigit(c) || c == '_'
    }

    @JvmStatic
    fun isLetterDigitUnderline(s: String): Boolean {
        return s.chars().mapToObj { it.toChar() }.allMatch { isLetterDigitUnderline(it) }
    }

    fun isAllowedInUnquotedString(c: Char): Boolean {
        return !(Character.isWhitespace(c) || c == '\\' || c == '\"' || c == '\'' || Character.isISOControl(c))
    }

    fun getName(reader: StringReader): String {
        val begin = reader.cursor
        while (reader.canRead() && isLetterDigitUnderline(reader.peek())) {
            reader.skip()
        }
        return reader.string.substring(begin, reader.cursor)
    }

    fun isConsole(s: ServerCommandSource): Boolean {
        return s.output === s.server || s.output is RconCommandOutput
    }

    @JvmStatic
    fun uuidOrNull(s: ServerCommandSource): UUID? {
        val player = s.player ?: return null
        return player.uuid
    }

    @JvmStatic
    @Throws(CommandSyntaxException::class)
    fun playerOrNull(s: ServerCommandSource): ServerPlayerEntity? {
        return s.player
    }

    @JvmStatic
    @Throws(CommandSyntaxException::class)
    fun requirePlayer(s: ServerCommandSource): ServerPlayerEntity {
        val player = playerOrNull(s) ?: throw NOT_PLAYER_ENTITY_EXCEPTION.create()
        return player
    }

    fun isQuoted(s: String): Boolean {
        return (s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("\'") && s.endsWith("\'"))
    }

    fun quoted(s: String): String {
        return '"'.toString() + s.replace("\\", "\\\\").replace("\"", "\\\"") + '"'
    }

    fun unquoted(s: String): String {
        var s = s
        if (isQuoted(s)) {
            s = s.substring(1, s.length - 1)
        }
        return s.replace("\\\"", "\"").replace("\\\\", "\\")
    }
}
