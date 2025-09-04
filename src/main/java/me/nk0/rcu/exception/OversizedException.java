package me.nk0.rcu.exception;

import net.minecraft.text.Text;

public class OversizedException extends RcuException {
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
