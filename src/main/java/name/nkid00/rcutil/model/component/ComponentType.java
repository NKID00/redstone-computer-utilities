package name.nkid00.rcutil.model.component;

public enum ComponentType {
    Wires, Bus, AddrBus, Ram, Fileram;

    public static ComponentType fromString(String typeString) {
        switch (typeString) {
            case "wires":
                return Wires;
            case "bus":
                return Bus;
            case "addrbus":
                return AddrBus;
            case "ram":
                return Ram;
            case "fileram":
                return Fileram;
            default:
                return null;
        }
    }
}
