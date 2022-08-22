package name.nkid00.rcutil.script.exception;

public class NameExists extends ScriptException {
    @Override
    public int code() {
        return -3;
    }
}
