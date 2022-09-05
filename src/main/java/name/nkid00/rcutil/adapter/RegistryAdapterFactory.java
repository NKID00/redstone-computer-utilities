package name.nkid00.rcutil.adapter;

import java.lang.reflect.ParameterizedType;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import net.minecraft.util.registry.Registry;

public class RegistryAdapterFactory<T> implements TypeAdapterFactory {
    private Class<T> clazz;
    private RegistryAdapter<T> registryAdapter;

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
