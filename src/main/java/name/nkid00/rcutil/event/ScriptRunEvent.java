package name.nkid00.rcutil.event;

import java.util.Collection;

import com.google.gson.JsonObject;

import name.nkid00.rcutil.exception.ApiException;
import name.nkid00.rcutil.helper.GsonHelper;
import name.nkid00.rcutil.model.Script;
import name.nkid00.rcutil.util.TypedArgument;

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
        if (obj instanceof ScriptRunEvent) {
            return true;
        }
        return false;
    }
}
