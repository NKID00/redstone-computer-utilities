package name.nkid00.rcutil.util;

public enum TypedArgumentType {
    Literal,
    Interface;

    public static TypedArgumentType fromString(String s) {
        switch (s) {
            case "literal":
            default:
                return Literal;
            case "interface":
                return Interface;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case Literal:
            default:
                return "literal";
            case Interface:
                return "interface";
        }
    }
}
