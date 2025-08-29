package me.nk0.rcu.event;

import com.google.gson.JsonObject;

import me.nk0.rcu.exception.ApiException;
import me.nk0.rcu.model.Script;

public class ScriptInitializeEvent extends Event {
    public ScriptInitializeEvent() {
    }

    public ScriptInitializeEvent(JsonObject param) throws ApiException {
        // TODO: check param
    }

    @Override
    public String name() {
        return "scriptInitialize";
    }

    @Override
    public JsonObject param() {
        return new JsonObject();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public void publish(Script script) throws ApiException {
        super.publish(new JsonObject(), script);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ScriptInitializeEvent) {
            return true;
        }
        return false;
    }
}
