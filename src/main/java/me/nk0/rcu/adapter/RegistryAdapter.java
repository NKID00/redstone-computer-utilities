package me.nk0.rcu.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.io.IOException;

public class RegistryAdapter<T> extends TypeAdapter<T> {
    private final Registry<T> registry;

    public RegistryAdapter(Registry<T> registry) {
        this.registry = registry;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(registry.getId(value).toString());
        }
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek().equals(JsonToken.NULL)) {
            in.nextNull();
            return null;
        } else {
            try {
                var string = in.nextString();
                var identifier = new Identifier(string);
                var optional = registry.getOrEmpty(identifier);
                if (optional.isEmpty()) {
                    throw new InvalidIdentifierException("Invalid item identifier: " + string);
                }
                return optional.get();
            } catch (InvalidIdentifierException e) {
                throw new IOException(e);
            }
        }
    }
}
