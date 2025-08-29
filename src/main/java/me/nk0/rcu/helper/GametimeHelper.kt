package me.nk0.rcu.helper

import net.minecraft.server.MinecraftServer

object GametimeHelper {
    private var gametime: Long = 0

    @JvmStatic
    @Synchronized
    fun isFrozen(server: MinecraftServer): Boolean {
        return gametime == server.overworld.time + 1
    }

    @JvmStatic
    @Synchronized
    fun updateGametime(server: MinecraftServer) {
        // START_SERVER_TICK is called before world time increases
        gametime = server.overworld.time + 1
    }

    @JvmStatic
    @Synchronized
    fun gametime(): Long {
        return gametime
    }
}
