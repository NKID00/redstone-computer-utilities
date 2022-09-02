package name.nkid00.rcutil.util;

public record IndexedObject<T> (int index, T object) {
    @Override
    public int hashCode() {
        // some random prime number
        int result = 31 + index;
        result = result * 31 + (object == null ? 0 : object.hashCode());
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
        if (obj instanceof IndexedObject) {
            var other = (IndexedObject<?>) obj;
            if (index != other.index) {
                return false;
            }
            if (object == null ? other.object != null : !object.equals(other.object)) {
                return false;
            }
            return true;
        }
        return false;
    }
}
