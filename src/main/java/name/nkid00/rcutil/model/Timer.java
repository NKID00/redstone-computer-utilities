package name.nkid00.rcutil.model;

import name.nkid00.rcutil.helper.GametimeHelper;

public class Timer {
    private TimedEvent event;
    private Script script;
    private long targetTime;

    protected Timer(TimedEvent event, Script script) {
        this.event = event;
        this.script = script;
        reset();
    }

    public static Timer create(TimedEvent event, Script script) {
        switch (event.name()) {
            case "onGametickStartDelay":
            case "onGametickEndDelay":
                return new Timer(event, script);
            case "onGametickStartClock":
            case "onGametickEndClock":
                return new Clock(event, script);
        }
        return null;
    }

    public void reset() {
        targetTime = GametimeHelper.gametime() + interval();
    }

    public TimedEvent event() {
        return event;
    }

    public Script script() {
        return script;
    }

    public long interval() {
        return event.interval();
    }

    public long targetTime() {
        return targetTime;
    }

    @Override
    public int hashCode() {
        // some random prime number
        int result = 31 + (event == null ? 0 : event.hashCode());
        result = result * 31 + (script == null ? 0 : script.hashCode());
        result = result * 31 + (int) targetTime;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Timer) {
            var other = (Timer) obj;
            if (event == null ? other.event != null : !event.equals(other.event)) {
                return false;
            }
            if (script == null ? other.script != null : !script.equals(other.script)) {
                return false;
            }
            if (targetTime != other.targetTime) {
                return false;
            }
            return true;
        }
        return false;
    }
}
