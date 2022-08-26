package name.nkid00.rcutil.exception;

import net.minecraft.text.Text;

public class RCUtilException extends Exception {
    private Text text = null;

    public RCUtilException() {
    }

    public RCUtilException(String message) {
        super(message);
    }

    public RCUtilException(Text message) {
        super(message.toString());
        text = message;
    }

    public RCUtilException(Throwable cause) {
        super(cause);
    }

    public RCUtilException(String message, Throwable cause) {
        super(message, cause);
    }

    public RCUtilException(Text message, Throwable cause) {
        super(message.toString(), cause);
        text = message;
    }

    public Text text() {
        return text;
    }
}
