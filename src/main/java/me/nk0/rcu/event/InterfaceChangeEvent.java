package me.nk0.rcu.event;

import com.google.gson.JsonObject;
import me.nk0.rcu.exception.ApiException;
import me.nk0.rcu.helper.BitSetHelper;

import java.util.BitSet;
import java.util.Objects;

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
        if (obj instanceof InterfaceChangeEvent other) {
            return Objects.equals(name, other.name);
        }
        return false;
    }
}
