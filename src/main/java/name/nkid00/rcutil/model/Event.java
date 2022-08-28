package name.nkid00.rcutil.model;

import com.google.gson.JsonElement;

import name.nkid00.rcutil.manager.InterfaceManager;

public abstract class Event {
    private final String name;
    private final Object param;

    public static final SimpleEvent ON_SCRIPT_LOAD = new SimpleEvent("onScriptLoad");
    public static final SimpleEvent ON_SCRIPT_UNLOAD = new SimpleEvent("onScriptUnload");
    public static final SimpleEvent ON_SCRIPT_RUN = new SimpleEvent("onScriptRun");
    public static final SimpleEvent ON_SCRIPT_INVOKE = new SimpleEvent("onScriptInvoke");
    public static final SimpleEvent ON_GAMETICK_START = new SimpleEvent("onGametickStart");
    public static final SimpleEvent ON_GAMETICK_END = new SimpleEvent("onGametickEnd");
    public static final InterfaceEvent ON_INTERFACE_REDSTONE_UPDATE = new InterfaceEvent("onInterfaceRedstoneUpdate");
    public static final InterfaceEvent ON_INTERFACE_READ = new InterfaceEvent("onInterfaceRead");
    public static final InterfaceEvent ON_INTERFACE_WRITE = new InterfaceEvent("onInterfaceWrite");
    public static final InterfaceEvent ON_INTERFACE_NEW = new InterfaceEvent("onInterfaceNew");
    public static final InterfaceEvent ON_INTERFACE_REMOVE = new InterfaceEvent("onInterfaceRemove");

    public Event(String name, Object param) {
        this.name = name;
        this.param = param;
    }

    public static Event fromRequest(String name, JsonElement param) {
        switch (name) {
            case "onScriptLoad":
                return ON_SCRIPT_LOAD;
            case "onScriptUnload":
                return ON_SCRIPT_UNLOAD;
            case "onScriptRun":
                return ON_SCRIPT_RUN;
            case "onScriptInvoke":
                return ON_SCRIPT_INVOKE;
            case "onGametickStart":
                return ON_GAMETICK_START;
            case "onGametickEnd":
                return ON_GAMETICK_END;
            case "onInterfaceRedstoneUpdate":
            case "onInterfaceRead":
            case "onInterfaceWrite":
            case "onInterfaceNew":
            case "onInterfaceRemove":
                String interfaceName;
                try {
                    interfaceName = param.getAsString();
                } catch (ClassCastException | IllegalStateException e) {
                    return null;
                }
                if (InterfaceManager.hasInterface(interfaceName)) {
                    var interfaze = InterfaceManager.interfaze(interfaceName);
                    switch (name) {
                        case "onInterfaceRedstoneUpdate":
                            return ON_INTERFACE_REDSTONE_UPDATE.withInterface(interfaze);
                        case "onInterfaceRead":
                            return ON_INTERFACE_READ.withInterface(interfaze);
                        case "onInterfaceWrite":
                            return ON_INTERFACE_WRITE.withInterface(interfaze);
                        case "onInterfaceNew":
                            return ON_INTERFACE_NEW.withInterface(interfaze);
                        case "onInterfaceRemove":
                            return ON_INTERFACE_REMOVE.withInterface(interfaze);
                    }
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

    public String event() {
        return name;
    }

    public Object param() {
        return param;
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
}
