package name.nkid00.rcutil.exception;

import net.minecraft.text.Text;

public class OversizedException extends RCUtilException {
    public OversizedException() {
    }

    public OversizedException(String message) {
        super(message);
    }

    public OversizedException(Text message) {
        super(message);
    }

    public OversizedException(Throwable cause) {
        super(cause);
    }

    public OversizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public OversizedException(Text message, Throwable cause) {
        super(message, cause);
    }
}
