package name.nkid00.rcutil.enumeration;

import name.nkid00.rcutil.helper.I18n;
import net.minecraft.text.Text;

public enum TriggerType {
    Positive, Negative, Dual;

    public static TriggerType fromString(String s) {
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

    public Text toText() {
        switch (this) {
            case Positive:
            default:
                return I18n.t("rcutil.fileram.fancyname.pos");
            case Negative:
                return I18n.t("rcutil.fileram.fancyname.neg");
            case Dual:
                return I18n.t("rcutil.fileram.fancyname.dual");
        }
    }
}
