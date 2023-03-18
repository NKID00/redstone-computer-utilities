package name.nkid00.rcutil.event;

import com.google.gson.JsonObject;

import name.nkid00.rcutil.exception.ApiException;
import name.nkid00.rcutil.manager.ScriptManager;

public class AlarmEvent extends Event implements Comparable<AlarmEvent> {
    private final long gametime;
    private final At at;

    public AlarmEvent(long gametime, At at) {
        this.gametime = gametime;
        this.at = at;
    }

    public AlarmEvent(JsonObject param) throws ApiException {
        // TODO: check param
        gametime = param.get("gametime").getAsLong();
        at = At.fromString(param.get("at").getAsString());
    }

    @Override
    public String name() {
        return "alarm";
    }

    @Override
    public JsonObject param() {
        var result = new JsonObject();
        result.addProperty("gametime", gametime);
        result.addProperty("at", at.toString());
        return result;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public void broadcast() {
        super.broadcast(new JsonObject());
        for (var script : ScriptManager.iterable()) {
            script.events.removeIf(event -> event instanceof AlarmEvent
                    && ((AlarmEvent) event).compareTo(this) <= 0);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof AlarmEvent) {
            var other = (AlarmEvent) obj;
            if (gametime != other.gametime) {
                return false;
            }
            if (at == null ? other.at != null : !at.equals(other.at)) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(AlarmEvent event) {
        if (event == null) {
            throw new NullPointerException();
        }
        if (gametime - event.gametime != 0) {
            return (int) (gametime - event.gametime);
        } else if (at.ordinal() - event.at.ordinal() != 0) {
            return (int) (at.ordinal() - event.at.ordinal());
        } else {
            return 0;
        }
    }

    public enum At {
        Start, End;

        @Override
        public String toString() {
            switch (this) {
                default:
                case Start:
                    return "start";
                case End:
                    return "end";
            }
        }

        public static At fromString(String s) {
            switch (s) {
                default:
                case "start":
                    return Start;
                case "end":
                    return End;
            }
        }
    }
}
