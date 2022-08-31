package name.nkid00.rcutil.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class InterfaceEvent extends Event {
    public InterfaceEvent(String name) {
        this(name, null);
    }

    public InterfaceEvent(String name, Interface interfaze) {
        super(name, interfaze);
    }

    public InterfaceEvent withInterface(Interface interfaze) {
        return new InterfaceEvent(name(), interfaze);
    }

    public Interface interfaze() {
        return (Interface) this.param();
    }

    @Override
    public JsonElement jsonParam() {
        return new JsonPrimitive(interfaze().name());
    }
}
