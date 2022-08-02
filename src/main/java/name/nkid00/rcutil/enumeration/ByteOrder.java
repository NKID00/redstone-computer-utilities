package name.nkid00.rcutil.enumeration;

import name.nkid00.rcutil.helper.I18n;
import net.minecraft.text.Text;

public enum ByteOrder {
    LittleEndian, BigEndian;

    public static ByteOrder fromString(String s) {
        if (s.equals("le")) {
            return LittleEndian;
        } else if (s.equals("be")) {
            return BigEndian;
        } else {
            return null;
        }
    }

    public Text toText() {
        switch (this) {
            case LittleEndian:
            default:
                return I18n.t("rcutil.fileram.fancyname.le");
            case BigEndian:
                return I18n.t("rcutil.fileram.fancyname.be");
        }
    }
}
