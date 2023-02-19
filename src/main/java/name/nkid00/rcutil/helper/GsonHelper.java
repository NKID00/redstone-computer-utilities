package name.nkid00.rcutil.helper;

import java.util.BitSet;

import com.google.gson.GsonBuilder;

import name.nkid00.rcutil.adapter.BitSetAdapter;
import name.nkid00.rcutil.adapter.BlockPosAdapter;
import name.nkid00.rcutil.adapter.RegistryAdapterFactory;
import name.nkid00.rcutil.adapter.ServerWorldAdapter;
import name.nkid00.rcutil.adapter.Vec3iAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.registry.RegistryKeys;

public class GsonHelper {
    public static GsonBuilder gsonBuilder(MinecraftServer server) {
        var registryManager = server.getRegistryManager();
        return new GsonBuilder()
                .registerTypeAdapter(BlockPos.class, new BlockPosAdapter())
                .registerTypeAdapter(Vec3i.class, new Vec3iAdapter())
                .registerTypeAdapter(ServerWorld.class, new ServerWorldAdapter(server))
                .registerTypeAdapter(BitSet.class, new BitSetAdapter())
                .registerTypeAdapterFactory(
                        new RegistryAdapterFactory<>(registryManager.get(RegistryKeys.ITEM)) {
                        })
                .setLenient()
                .disableHtmlEscaping();
    }
}
