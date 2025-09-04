package me.nk0.rcu.event;

import com.google.gson.JsonObject;
import me.nk0.rcu.exception.ApiException;
import me.nk0.rcu.helper.GsonHelper;
import me.nk0.rcu.model.Script;
import me.nk0.rcu.util.TypedArgument;

import java.util.Collection;

public class ScriptRunEvent extends Event {
    public ScriptRunEvent() {
    }

    public ScriptRunEvent(JsonObject param) throws ApiException {
        // TODO: check param
    }

    @Override
    public String name() {
        return "scriptRun";
    }

    @Override
    public JsonObject param() {
        return new JsonObject();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public int publish(Collection<TypedArgument> argument, Script script) throws ApiException {
        if (!script.eventExists(this)) {
            throw ApiException.GENERAL_ERROR;
        }
        var content = new JsonObject();
        content.add("argument", GsonHelper.gson().toJsonTree(argument));
        var result = super.publish(content, script);
        return result.get("result").getAsInt();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        return obj instanceof ScriptRunEvent;
    }
}
