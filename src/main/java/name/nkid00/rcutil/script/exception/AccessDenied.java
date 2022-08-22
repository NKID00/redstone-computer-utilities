package name.nkid00.rcutil.script.exception;

public class AccessDenied extends ScriptException {
    @Override
    public int code() {
        return -11;
    }
}
