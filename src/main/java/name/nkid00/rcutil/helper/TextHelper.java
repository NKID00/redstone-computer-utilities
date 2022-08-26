package name.nkid00.rcutil.helper;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TextHelper {
    public static MutableText copy(Text text) {
        return text.copy();
    }

    public static MutableText empty() {
        return Text.empty();
    }

    public static MutableText literal(String string) {
        return Text.literal(string);
    }

    public static MutableText translatable(String key, Object... args) {
        return Text.translatable(key, args);
    }

    public static MutableText formatted(Text text, Formatting formatting) {
        return empty().append(text).formatted(formatting);
    }
}
