package name.nkid00.rcutil.util;

public record TypedArgument(TypedArgumentType type, String value) {
    @Override
    public int hashCode() {
        // some random prime number
        int result = 31 + (type == null ? 0 : type.hashCode());
        result = result * 31 + (value == null ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof TypedArgument) {
            var other = (TypedArgument) obj;
            if (type == null ? other.type != null : !type.equals(other.type)) {
                return false;
            }
            if (value == null ? other.value != null : !value.equals(other.value)) {
                return false;
            }
            return true;
        }
        return false;
    }


    @Override
    public String toString() {
        return type.toString() + ':' + value;
    }
}
