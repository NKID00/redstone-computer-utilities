package name.nkid00.rcutil.helper;

import java.util.UUID;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.rcon.RconCommandOutput;

public class CommandHelper {
    private static final SimpleCommandExceptionType NOT_PLAYER_ENTITY_EXCEPTION = new SimpleCommandExceptionType(
            I18n.t("rcutil.command.fail.not_player_entity"));

    public static boolean isLetterDigitUnderline(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    public static boolean isLetterDigitUnderline(String s) {
        return s.chars().mapToObj(i -> (char) i).allMatch(CommandHelper::isLetterDigitUnderline);
    }

    public static boolean isAllowedInUnquotedString(char c) {
        return !(Character.isWhitespace(c)
                || c == '\\' || c == '\"' || c == '\''
                || Character.isISOControl(c));
    }

    public static String getName(StringReader reader) {
        var begin = reader.getCursor();
        while (reader.canRead() && isLetterDigitUnderline(reader.peek())) {
            reader.skip();
        }
        return reader.getString().substring(begin, reader.getCursor());
    }

    public static boolean isConsole(ServerCommandSource s) {
        return s.output == s.server || s.output instanceof RconCommandOutput;
    }

    public static UUID uuidOrNull(ServerCommandSource s) {
        var player = s.getPlayer();
        if (player == null) {
            return null;
        }
        return player.getUuid();
    }

    public static ServerPlayerEntity requirePlayer(ServerCommandSource s) throws CommandSyntaxException {
        var player = s.getPlayer();
        if (player == null) {
            throw NOT_PLAYER_ENTITY_EXCEPTION.create();
        }
        return player;
    }

    public static boolean isQuoted(String s) {
        return (s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("\'") && s.endsWith("\'"));
    }

    public static String quoted(String s) {
        return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }

    public static String unquoted(String s) {
        if (isQuoted(s)) {
            s = s.substring(1, s.length() - 1);
        }
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
