package me.nk0.rcu.exception;

import net.minecraft.text.Text;

public class LanguageNotFoundException extends RcuException {
    public LanguageNotFoundException() {
    }

    public LanguageNotFoundException(String message) {
        super(message);
    }

    public LanguageNotFoundException(Text message) {
        super(message);
    }

    public LanguageNotFoundException(Throwable cause) {
        super(cause);
    }

    public LanguageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LanguageNotFoundException(Text message, Throwable cause) {
        super(message, cause);
    }
}
