package name.nkid00.rcutil.model;

public class Script {
    public String name;
    public String description;
    public int permissionLevel;

    public Script(String name, String description, int permissionLevel) {
        this.name = name;
        this.description = description;
        this.permissionLevel = permissionLevel;
    }
}
