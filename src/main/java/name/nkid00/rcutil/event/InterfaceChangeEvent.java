package name.nkid00.rcutil.event;

import java.util.BitSet;

import com.google.gson.JsonObject;

import name.nkid00.rcutil.exception.ApiException;
import name.nkid00.rcutil.helper.BitSetHelper;

public class InterfaceChangeEvent extends Event {
    private final String name;

    public InterfaceChangeEvent(String name) {
        this.name = name;
    }

    public InterfaceChangeEvent(JsonObject param) throws ApiException {
        // TODO: check param
        name = param.get("name").getAsString();
    }

    @Override
    public String name() {
        return "interfaceChange";
    }

    @Override
    public JsonObject param() {
        var result = new JsonObject();
        result.addProperty("name", name);
        return result;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public void broadcast(BitSet previous, BitSet current) {
        var content = new JsonObject();
        content.addProperty("previous", BitSetHelper.toBase64(previous));
        content.addProperty("current", BitSetHelper.toBase64(current));
        super.broadcast(content);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof InterfaceChangeEvent) {
            var other = (InterfaceChangeEvent) obj;
            if (name == null ? other.name != null : !name.equals(other.name)) {
                return false;
            }
            return true;
        }
        return false;
    }
}
