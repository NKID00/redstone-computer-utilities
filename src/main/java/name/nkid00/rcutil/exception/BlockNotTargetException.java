package name.nkid00.rcutil.exception;

import net.minecraft.text.Text;

public class BlockNotTargetException extends RCUtilException {
    public BlockNotTargetException() {
    }

    public BlockNotTargetException(String message) {
        super(message);
    }

    public BlockNotTargetException(Text message) {
        super(message);
    }

    public BlockNotTargetException(Throwable cause) {
        super(cause);
    }

    public BlockNotTargetException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockNotTargetException(Text message, Throwable cause) {
        super(message, cause);
    }
}
