package name.nkid00.rcutil.command;

public class CommandUtil {
    public static boolean isLetterDigitUnderline(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
    }

    public static boolean isLetterDigitUnderline(String s) {
        return s.chars().mapToObj(i -> (char)i).allMatch(CommandUtil::isLetterDigitUnderline);
    }
}
