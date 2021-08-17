package name.nkid00.rcutil.enumeration;

import net.minecraft.text.TranslatableText;

public enum FileRamType {
    ReadOnly, WriteOnly;

    public static FileRamType fromString(String s) {
        if (s.equals("ro")) {
            return ReadOnly;
        } else if (s.equals("wo")) {
            return WriteOnly;
        } else {
            return null;
        }
    }

    public TranslatableText toText() {
        switch (this) {
            case ReadOnly:
            default:
                return new TranslatableText("rcutil.fileram.fancyname.ro");
            case WriteOnly:
                return new TranslatableText("rcutil.fileram.fancyname.wo");
        }
    }
}
