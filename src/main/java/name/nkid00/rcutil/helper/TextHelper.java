package name.nkid00.rcutil.helper;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class TextHelper {
    public static MutableText copy(Text text) {
        return text.copy();
    }

    public static MutableText empty() {
        return literal("");
    }

    public static MutableText literal(String string) {
        return new LiteralText(string);
    }

    public static MutableText translatable(String key, Object... args) {
        return new TranslatableText(key, args);
    }

    public static MutableText formatted(Text text, Formatting formatting) {
        return empty().append(text).formatted(formatting);
    }

    public static MutableText info(Text text) {
        return text.copy();
    }

    public static MutableText warn(Text text) {
        return formatted(text, Formatting.YELLOW);
    }

    public static MutableText error(Text text) {
        return formatted(text, Formatting.RED);
    }
}
