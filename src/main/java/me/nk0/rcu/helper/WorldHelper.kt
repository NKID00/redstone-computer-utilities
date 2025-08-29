package me.nk0.rcu.helper

import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.world.World

object WorldHelper {
    private var server: MinecraftServer? = null

    @JvmStatic
    fun init(server: MinecraftServer?) {
        WorldHelper.server = server
    }

    @JvmStatic
    fun toString(world: World): String {
        return world.registryKey.value.toString()
    }

    @JvmStatic
    fun fromString(s: String?): ServerWorld? {
        return server!!.getWorld(RegistryKey.of(RegistryKeys.WORLD, Identifier(s)))
    }
}
