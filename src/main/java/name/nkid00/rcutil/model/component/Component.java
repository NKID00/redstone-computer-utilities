package name.nkid00.rcutil.model.component;

import org.jetbrains.annotations.NotNull;

import name.nkid00.rcutil.enumeration.Status;

public abstract class Component {

    @NotNull
    public String name = "";
    @NotNull
    public Status status = Status.Stopped;
    @NotNull
    public String cause = "";

    public void setStatus(Status status) {
        this.status = status;
        this.cause = "";
    }

    public void setStatus(Status status, String cause) {
        this.status = status;
        this.cause = cause;
    }

    public void start() {
        setStatus(Status.Running);
    }

    public void stop() {
        setStatus(Status.Stopped);
    }

    public void fail(@NotNull String cause) {
        setStatus(Status.Failed, cause);
    }

    public boolean isRunning() {
        return this.status.isRunning();
    }
}
