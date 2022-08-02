package name.nkid00.rcutil.enumeration;

import name.nkid00.rcutil.helper.I18n;
import net.minecraft.text.Text;

public enum RamType {
    ReadOnly, WriteOnly, ReadWrite;

    public static RamType fromString(String s) {
        if (s.equals("ro")) {
            return ReadOnly;
        } else if (s.equals("wo")) {
            return WriteOnly;
        } else {
            return null;
        }
    }

    public Text toText() {
        switch (this) {
            case ReadOnly:
            default:
                return I18n.t("rcutil.fileram.fancyname.ro");
            case WriteOnly:
                return I18n.t("rcutil.fileram.fancyname.wo");
        }
    }
}
