package name.nkid00.rcutil.enumeration;

import net.minecraft.text.TranslatableText;

public enum RamBusEdgeTriggering {
    Positive, Negative, Dual;

    public static RamBusEdgeTriggering fromString(String s) {
        if (s.equals("pos")) {
            return Positive;
        } else if (s.equals("neg")) {
            return Negative;
        } else if (s.equals("dual")) {
            return Dual;
        } else {
            return null;
        }
    }

    public TranslatableText toText() {
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
