package name.nkid00.rcutil;

import java.util.HashMap;

public class Settings {
    public static final int requiredPermissionLevel = 2;
    public static Status status = Status.Idle;
    public static HashMap<String, WriteOnlyRamBus> rams = new HashMap<>();
}
