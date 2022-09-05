package name.nkid00.rcutil.model;

public class Clock extends Timer {
    protected Clock(TimedEvent event, Script script) {
        super(event, script);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) && obj instanceof Clock) {
            return true;
        }
        return false;
    }
}
