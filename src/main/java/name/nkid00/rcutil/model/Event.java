package name.nkid00.rcutil.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import name.nkid00.rcutil.exception.ResponseException;
import name.nkid00.rcutil.manager.InterfaceManager;

public abstract class Event {
    private final String name;
    private final Object param;

    public static final SimpleEvent ON_SCRIPT_RELOAD = new SimpleEvent("onScriptReload");
    public static final SimpleEvent ON_SCRIPT_RUN = new SimpleEvent("onScriptRun");
    public static final SimpleEvent ON_SCRIPT_INVOKE = new SimpleEvent("onScriptInvoke");
    public static final SimpleEvent ON_GAMETICK_START = new SimpleEvent("onGametickStart");
    public static final SimpleEvent ON_GAMETICK_END = new SimpleEvent("onGametickEnd");
    public static final SimpleEvent ON_INTERFACE_NEW = new SimpleEvent("onInterfaceNew");
    public static final TimedEvent ON_GAMETICK_START_DELAY = new TimedEvent("onGametickStartDelay");
    public static final TimedEvent ON_GAMETICK_END_DELAY = new TimedEvent("onGametickEndDelay");
    public static final TimedEvent ON_GAMETICK_START_CLOCK = new TimedEvent("onGametickStartClock");
    public static final TimedEvent ON_GAMETICK_END_CLOCK = new TimedEvent("onGametickEndClock");
    public static final InterfaceEvent ON_INTERFACE_UPDATE = new InterfaceEvent("onInterfaceUpdate");
    public static final InterfaceEvent ON_INTERFACE_UPDATE_IMMEDIATE = new InterfaceEvent("onInterfaceUpdateImmediate");
    public static final InterfaceEvent ON_INTERFACE_READ = new InterfaceEvent("onInterfaceRead");
    public static final InterfaceEvent ON_INTERFACE_WRITE = new InterfaceEvent("onInterfaceWrite");
    public static final InterfaceEvent ON_INTERFACE_REMOVE = new InterfaceEvent("onInterfaceRemove");

    public Event(String name, Object param) {
        this.name = name;
        this.param = param;
    }

    public static Event fromRequest(JsonObject event) throws ResponseException {
        var name = event.get("name").getAsString();
        var param = event.get("param");
        return Event.fromRequest(name, param);
    }

    public static Event fromRequest(String name, JsonElement param) throws ResponseException {
        switch (name) {
            case "onScriptReload":
                return ON_SCRIPT_RELOAD;
            case "onScriptRun":
                return ON_SCRIPT_RUN;
            case "onScriptInvoke":
                return ON_SCRIPT_INVOKE;
            case "onGametickStart":
                return ON_GAMETICK_START;
            case "onGametickEnd":
                return ON_GAMETICK_END;
            case "onInterfaceNew":
                return ON_INTERFACE_NEW;
            case "onGametickStartDelay":
            case "onGametickEndDelay":
            case "onGametickStartClock":
            case "onGametickEndClock":
                long interval;
                try {
                    interval = param.getAsLong();
                } catch (ClassCastException | IllegalStateException e) {
                    throw ResponseException.EVENT_NOT_FOUND;
                }
                switch (name) {
                    case "onGametickStartDelay":
                        return ON_GAMETICK_START_DELAY.withInterval(interval);
                    case "onGametickEndDelay":
                        return ON_GAMETICK_END_DELAY.withInterval(interval);
                    case "onGametickStartClock":
                        return ON_GAMETICK_START_CLOCK.withInterval(interval);
                    case "onGametickEndClock":
                        return ON_GAMETICK_END_CLOCK.withInterval(interval);
                }
            case "onInterfaceUpdate":
            case "onInterfaceUpdateImmediate":
            case "onInterfaceRead":
            case "onInterfaceWrite":
            case "onInterfaceRemove":
                String interfaceName;
                try {
                    interfaceName = param.getAsString();
                } catch (ClassCastException | IllegalStateException e) {
                    throw ResponseException.EVENT_NOT_FOUND;
                }
                if (InterfaceManager.hasInterface(interfaceName)) {
                    var interfaze = InterfaceManager.interfaceByName(interfaceName);
                    switch (name) {
                        case "onInterfaceUpdate":
                            return ON_INTERFACE_UPDATE.withInterface(interfaze);
                        case "onInterfaceUpdateImmediate":
                            return ON_INTERFACE_UPDATE_IMMEDIATE.withInterface(interfaze);
                        case "onInterfaceRead":
                            return ON_INTERFACE_READ.withInterface(interfaze);
                        case "onInterfaceWrite":
                            return ON_INTERFACE_WRITE.withInterface(interfaze);
                        case "onInterfaceRemove":
                            return ON_INTERFACE_REMOVE.withInterface(interfaze);
                    }
                } else {
                    throw ResponseException.INTERFACE_NOT_FOUND;
                }
            default:
                throw ResponseException.EVENT_NOT_FOUND;
        }
    }

    public String name() {
        return name;
    }

    public Object param() {
        return param;
    }

    public JsonElement jsonParam() {
        return null;
    }

    @Override
    public int hashCode() {
        // some random prime number
        int result = 31 + (name == null ? 0 : name.hashCode());
        result = result * 31 + (param == null ? 0 : param.hashCode());
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
        if (obj instanceof Event) {
            var other = (Event) obj;
            if (name == null ? other.name != null : !name.equals(other.name)) {
                return false;
            }
            if (param == null ? other.param != null : !param.equals(other.param)) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        if (param == null) {
            return name;
        } else {
            return "%s(%s)".formatted(name, param);
        }
    }
}
