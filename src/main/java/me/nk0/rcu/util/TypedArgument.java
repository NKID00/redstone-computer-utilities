package me.nk0.rcu.util;

import java.util.Objects;

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
        if (obj instanceof TypedArgument other) {
            if (!Objects.equals(type, other.type)) {
                return false;
            }
            return Objects.equals(value, other.value);
        }
        return false;
    }


    @Override
    public String toString() {
        return type.toString() + ':' + value;
    }
}
