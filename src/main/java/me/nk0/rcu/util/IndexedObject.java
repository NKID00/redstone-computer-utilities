package me.nk0.rcu.util;

import java.util.Objects;

public record IndexedObject<T>(int index, T object) {
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
        if (obj instanceof IndexedObject<?> other) {
            if (index != other.index) {
                return false;
            }
            return Objects.equals(object, other.object);
        }
        return false;
    }
}
