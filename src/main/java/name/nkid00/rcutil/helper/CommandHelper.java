package name.nkid00.rcutil.helper;

import com.mojang.brigadier.StringReader;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.rcon.RconCommandOutput;

public class CommandHelper {
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
}
