package me.nk0.rcu.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import net.minecraft.registry.Registry;

import java.lang.reflect.ParameterizedType;

public class RegistryAdapterFactory<T> implements TypeAdapterFactory {
    private final Class<T> clazz;
    private final RegistryAdapter<T> registryAdapter;

    @SuppressWarnings("unchecked")
    protected RegistryAdapterFactory(Registry<T> registry) {
        clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        registryAdapter = new RegistryAdapter<>(registry);
    }

    @SuppressWarnings("unchecked")
    public <T2> TypeAdapter<T2> create(Gson gson, TypeToken<T2> type) {
        if (clazz.isAssignableFrom(type.getRawType())) {
            return (TypeAdapter<T2>) registryAdapter;
        }
        return null;
    }
}
