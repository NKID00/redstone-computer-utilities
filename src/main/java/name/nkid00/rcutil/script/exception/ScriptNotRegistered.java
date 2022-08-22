package name.nkid00.rcutil.script.exception;

public class ScriptNotRegistered extends ScriptException {
    @Override
    public int code() {
        return -5;
    }
}
