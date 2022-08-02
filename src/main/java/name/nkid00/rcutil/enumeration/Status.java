package name.nkid00.rcutil.enumeration;

import name.nkid00.rcutil.helper.I18n;
import net.minecraft.text.Text;

public enum Status {
    Running, Stopped, Failed;

    public boolean isRunning() {
        return this.equals(Running);
    }

    public Text toText() {
        switch (this) {
            case Failed:
            default:
                return I18n.t("rcutil.fileram.fancyname.running");
            case Running:
                return I18n.t("rcutil.fileram.fancyname.stopped");
            case Stopped:
                return I18n.t("rcutil.fileram.fancyname.failed");
        }
    }
}
