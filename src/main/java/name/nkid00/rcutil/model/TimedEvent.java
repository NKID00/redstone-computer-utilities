package name.nkid00.rcutil.model;

public class TimedEvent extends Event {
    public TimedEvent(String name) {
        this(name, 0);
    }

    public TimedEvent(String name, long interval) {
        super(name, interval < 0 ? 0 : interval);
    }

    public TimedEvent withInterval(long interval) {
        return new TimedEvent(name(), interval);
    }

    public long interval() {
        return param() == null ? 0 : (long) this.param();
    }
}
