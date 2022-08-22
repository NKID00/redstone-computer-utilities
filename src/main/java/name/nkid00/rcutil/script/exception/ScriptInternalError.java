package name.nkid00.rcutil.script.exception;

public class ScriptInternalError extends ScriptException {
    @Override
    public int code() {
        return -7;
    }
}
