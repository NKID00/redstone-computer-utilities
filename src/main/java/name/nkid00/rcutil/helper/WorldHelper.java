package name.nkid00.rcutil.helper;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class WorldHelper {
    public static String toString(World world) {
        return world.getRegistryKey().getValue().toString();
    }

    public static ServerWorld fromString(MinecraftServer server, String s) {
        return server.getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(s)));
    }
}
