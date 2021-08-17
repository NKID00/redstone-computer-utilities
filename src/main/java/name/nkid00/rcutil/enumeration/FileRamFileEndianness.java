package name.nkid00.rcutil.enumeration;

import net.minecraft.text.TranslatableText;

public enum FileRamFileEndianness {
    LittleEndian, BigEndian;

    public static FileRamFileEndianness fromString(String s) {
        if (s.equals("le")) {
            return LittleEndian;
        } else if (s.equals("be")) {
            return BigEndian;
        } else {
            return null;
        }
    }

    public TranslatableText toText() {
        switch (this) {
            case LittleEndian:
            default:
                return new TranslatableText("rcutil.fileram.fancyname.le");
            case BigEndian:
                return new TranslatableText("rcutil.fileram.fancyname.be");
        }
    }
}
