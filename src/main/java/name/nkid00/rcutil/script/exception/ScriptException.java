package name.nkid00.rcutil.script.exception;

import name.nkid00.rcutil.exception.RCUtilException;

public abstract class ScriptException extends RCUtilException {
    public abstract int code();
}
