package name.nkid00.rcutil.model;

import java.util.concurrent.ConcurrentHashMap;

public class Script {
    public String name;
    public String description;
    public int permissionLevel;
    public String authKey = null;
    public String clientAddress;
    public ConcurrentHashMap<String, String> callbacks = new ConcurrentHashMap<>();

    public Script(String name, String description, int permissionLevel) {
        this.name = name;
        this.description = description;
        this.permissionLevel = permissionLevel;
    }

    public boolean equals(Script script) {
        if (this == script) {
            return true;
        }
        if (!name.equals(script.name)) {
            return false;
        }
        if (authKey != null) {
            return authKey.equals(script.authKey);
        } else {
            return script.authKey == null;
        }
    }

    public String callback(String event) {
        return callbacks.get(event);
    }

    public boolean callbackExists(String event) {
        return callbacks.containsKey(event);
    }

    public void registerCallback(String event, String callback) {
        callbacks.put(event, callback);
    }

    public void deregisterCallback(String event) {
        callbacks.remove(event);
    }
}
