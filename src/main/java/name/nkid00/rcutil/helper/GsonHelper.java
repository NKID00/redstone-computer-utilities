package name.nkid00.rcutil.helper;

import com.google.gson.GsonBuilder;

import name.nkid00.rcutil.adapter.BlockPosAdapter;
import name.nkid00.rcutil.adapter.RegistryAdapterFactory;
import name.nkid00.rcutil.adapter.Vec3iAdapter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;

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
                .registerTypeAdapter(Vec3i.class, new Vec3iAdapter())
                .registerTypeAdapterFactory(
                        new RegistryAdapterFactory<>(registryManager.get(Registry.DIMENSION_TYPE_KEY)) {
                        })
                .registerTypeAdapterFactory(
                        new RegistryAdapterFactory<>(registryManager.get(Registry.ITEM_KEY)) {
                        })
                .setLenient();
    }
}
