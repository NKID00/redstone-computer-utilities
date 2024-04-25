package name.nkid00.rcutil.helper

import name.nkid00.rcutil.Options
import name.nkid00.rcutil.helper.TextHelper.literal
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

object Log {
    val LOGGER: Logger = LoggerFactory.getLogger("rcutil")
    private const val BRAND = "[rcutil] "
    private val BRAND_TEXT: Text = literal(BRAND)

    @JvmStatic
    fun info(arg0: String) {
        LOGGER.info(BRAND + arg0)
    }

    @JvmStatic
    fun info(arg0: String, vararg arg1: Any?) {
        LOGGER.info(BRAND + arg0, *arg1)
    }

    fun warn(arg0: String) {
        LOGGER.warn(BRAND + arg0)
    }

    fun warn(arg0: String, arg1: Throwable?) {
        LOGGER.warn(BRAND + arg0, arg1)
    }

    @JvmStatic
    fun warn(arg0: String, vararg arg1: Any?) {
        LOGGER.warn(BRAND + arg0, *arg1)
    }

    @JvmStatic
    fun error(arg0: String) {
        LOGGER.error(BRAND + arg0)
    }

    @JvmStatic
    fun error(arg0: String, arg1: Throwable?) {
        LOGGER.error(BRAND + arg0, arg1)
    }

    @JvmStatic
    fun error(arg0: String, vararg arg1: Any?) {
        LOGGER.error(BRAND + arg0, *arg1)
    }

    fun broadcastToPlayers(server: MinecraftServer, message: String?) {
        val makeCompilerHappy = BRAND_TEXT.copy().append(message)
        server.playerManager.playerList.forEach(Consumer { player: ServerPlayerEntity ->
            player.sendMessage(makeCompilerHappy)
        })
    }

    fun broadcastToPlayers(server: MinecraftServer, message: Text?) {
        val makeCompilerHappy = BRAND_TEXT.copy().append(message)
        server.playerManager.playerList.forEach(Consumer { player: ServerPlayerEntity ->
            player.sendMessage(makeCompilerHappy)
        })
    }

    @JvmStatic
    @JvmOverloads
    fun broadcastToOps(server: MinecraftServer, message: String?, permissionLevel: Int = Options.requiredPermissionLevel()) {
        val makeCompilerHappy = BRAND_TEXT.copy().append(message)
        server.playerManager.playerList.forEach(Consumer { player: ServerPlayerEntity ->
            if (player.hasPermissionLevel(permissionLevel)) {
                player.sendMessage(makeCompilerHappy)
            }
        })
    }

    @JvmStatic
    @JvmOverloads
    fun broadcastToOps(server: MinecraftServer, message: Text?, permissionLevel: Int = Options.requiredPermissionLevel()) {
        val makeCompilerHappy = BRAND_TEXT.copy().append(message)
        server.playerManager.playerList.forEach(Consumer { player: ServerPlayerEntity ->
            if (player.hasPermissionLevel(permissionLevel)) {
                player.sendMessage(makeCompilerHappy)
            }
        })
    }

    fun send(player: ServerPlayerEntity, message: Text?) {
        player.sendMessage(message, false)
    }

    fun send(player: ServerPlayerEntity, message: String?) {
        send(player, literal(message))
    }
}
