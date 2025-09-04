package me.nk0.rcu.exception;

import net.minecraft.text.Text;

public class RcuException extends Exception {
    private Text text = null;

    public RcuException() {
    }

    public RcuException(String message) {
        super(message);
    }

    public RcuException(Text message) {
        super(message.toString());
        text = message;
    }

    public RcuException(Throwable cause) {
        super(cause);
    }

    public RcuException(String message, Throwable cause) {
        super(message, cause);
    }

    public RcuException(Text message, Throwable cause) {
        super(message.toString(), cause);
        text = message;
    }

    public Text text() {
        return text;
    }
}
