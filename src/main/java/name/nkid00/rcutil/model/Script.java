package name.nkid00.rcutil.model;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import name.nkid00.rcutil.helper.I18n;
import net.minecraft.text.Text;

public class Script {
    public final String name;
    public final String description;
    public final int permissionLevel;
    public final String authKey;
    public final String clientAddress;
    public final ConcurrentHashMap<Event, String> callbacks = new ConcurrentHashMap<>();

    public Script(String name, String description, int permissionLevel, String authKey, String clientAddress) {
        this.name = name;
        this.description = description;
        this.permissionLevel = permissionLevel;
        this.authKey = authKey;
        this.clientAddress = clientAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Script) {
            var other = (Script) obj;
            if (name == null ? other.name != null : !name.equals(other.name)) {
                return false;
            }
            if (authKey == null ? other.authKey != null : !authKey.equals(other.authKey)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public Text info(UUID uuid) {
        if (description.isBlank()) {
            return I18n.t(uuid, "rcutil.info.script.detail", name,
                    I18n.t(uuid, "rcutil.info.script.no_description"), this.permissionLevel,
                    String.join(", ", callbacks.keySet().toArray(new String[0])));
        } else {
            return I18n.t(uuid, "rcutil.info.script.detail", name, description, permissionLevel,
                    String.join(", ", callbacks.keySet().toArray(new String[0])));
        }
    }

    public String callback(Event event) {
        return callbacks.get(event);
    }

    public boolean callbackExists(Event event) {
        return callbacks.containsKey(event);
    }

    public void registerCallback(Event event, String callback) {
        callbacks.put(event, callback);
    }

    public void deregisterCallback(Event event) {
        callbacks.remove(event);
    }
}
