package name.nkid00.rcutil.helper;

import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

public class RegistryHelper {
    private static DynamicRegistryManager registryManager;
    private static Registry<DimensionType> dimensionType;
    private static Registry<Item> item;

    public static void init(MinecraftServer server) {
        RegistryHelper.registryManager = server.getRegistryManager();
        dimensionType = registryManager.get(Registry.DIMENSION_TYPE_KEY);
        item = registryManager.get(Registry.ITEM_KEY);
    }

    public static <T> String toString(T value, RegistryKey<Registry<T>> key) {
        return registryManager.get(key).getId(value).toString();
    }

    public static String toString(DimensionType value) {
        return dimensionType.getId(value).toString();
    }

    public static String toString(Item value) {
        return item.getId(value).toString();
    }
}
