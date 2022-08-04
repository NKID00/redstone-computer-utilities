package name.nkid00.rcutil.helper;

import com.google.gson.GsonBuilder;

import name.nkid00.rcutil.adapter.BlockPosAdapter;
import name.nkid00.rcutil.adapter.RegistryAdapter;
import name.nkid00.rcutil.adapter.Vec3iAdapter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class GsonHelper {
    public static GsonBuilder gsonBuilder(MinecraftServer server) {
        return gsonBuilder(server.getRegistryManager());
    }

    public static GsonBuilder gsonBuilder(MinecraftClient client) {
        return gsonBuilder(client.getNetworkHandler().getRegistryManager());
    }

    public static GsonBuilder gsonBuilder(DynamicRegistryManager registryManager) {
        return new GsonBuilder()
                .registerTypeAdapter(BlockPos.class, new BlockPosAdapter())
                .registerTypeAdapter(DimensionType.class,
                        new RegistryAdapter<>(registryManager.get(Registry.DIMENSION_TYPE_KEY)))
                .registerTypeAdapter(Item.class,
                        new RegistryAdapter<>(registryManager.get(Registry.ITEM_KEY)))
                .registerTypeAdapter(Vec3i.class, new Vec3iAdapter())
                .setLenient();
    }
}
