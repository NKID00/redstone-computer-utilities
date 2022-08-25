package name.nkid00.rcutil.helper;

import com.google.gson.GsonBuilder;

import name.nkid00.rcutil.adapter.BlockPosAdapter;
import name.nkid00.rcutil.adapter.RegistryAdapterFactory;
import name.nkid00.rcutil.adapter.ServerWorldAdapter;
import name.nkid00.rcutil.adapter.Vec3iAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;

public class GsonHelper {
    public static GsonBuilder gsonBuilder(MinecraftServer server) {
        var registryManager = server.getRegistryManager();
        return new GsonBuilder()
                .registerTypeAdapter(BlockPos.class, new BlockPosAdapter())
                .registerTypeAdapter(Vec3i.class, new Vec3iAdapter())
                .registerTypeAdapter(ServerWorld.class, new ServerWorldAdapter(server))
                .registerTypeAdapterFactory(
                        new RegistryAdapterFactory<>(registryManager.get(Registry.DIMENSION_TYPE_KEY)) {
                        })
                .registerTypeAdapterFactory(
                        new RegistryAdapterFactory<>(registryManager.get(Registry.ITEM_KEY)) {
                        })
                .setLenient();
    }
}
