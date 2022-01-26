package name.nkid00.rcutil.component;

import org.jetbrains.annotations.NotNull;

public abstract class Component {
    public static enum ComponentStatus {
        Running, Stopped, Failed;
    }

    @NotNull
    public String name = "";
    @NotNull
    public ComponentStatus status = ComponentStatus.Stopped;
    @NotNull
    public String cause = "";

    public void setStatus(ComponentStatus status, String cause) {
        this.status = status;
        this.cause = cause;
    }

    public void start() {
        setStatus(ComponentStatus.Running, "");
    }

    public void stop() {
        setStatus(ComponentStatus.Stopped, "");
    }

    public void fail(@NotNull String cause) {
        setStatus(ComponentStatus.Failed, cause);
    }

    public boolean isRunning() {
        return this.status.equals(ComponentStatus.Running);
    }
}
