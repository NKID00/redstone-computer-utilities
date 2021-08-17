package name.nkid00.rcutil.enumeration;

import net.minecraft.text.TranslatableText;

public enum EdgeTriggering {
    Positive, Negative, Dual;

    public TranslatableText getText() {
        switch (this) {
            case Positive:
            default:
                return new TranslatableText("rcutil.fileram.fancyname.pos");
            case Negative:
                return new TranslatableText("rcutil.fileram.fancyname.neg");
            case Dual:
                return new TranslatableText("rcutil.fileram.fancyname.dual");
        }
    }
}