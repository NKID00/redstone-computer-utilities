package name.nkid00.rcutil.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.nkid00.rcutil.Options;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class Log {
    public static final Logger LOGGER = LoggerFactory.getLogger("rcutil");
    private static final String BRAND = "[rcutil] ";
    private static final Text BRAND_TEXT = TextHelper.literal(BRAND);

    public static void info(String arg0) {
        LOGGER.info(BRAND + arg0);
    }

    public static void info(String arg0, Object... arg1) {
        LOGGER.info(BRAND + arg0, arg1);
    }

    public static void warn(String arg0) {
        LOGGER.warn(BRAND + arg0);
    }

    public static void warn(String arg0, Throwable arg1) {
        LOGGER.warn(BRAND + arg0, arg1);
    }

    public static void warn(String arg0, Object... arg1) {
        LOGGER.warn(BRAND + arg0, arg1);
    }

    public static void error(String arg0) {
        LOGGER.error(BRAND + arg0);
    }

    public static void error(String arg0, Throwable arg1) {
        LOGGER.error(BRAND + arg0, arg1);
    }

    public static void error(String arg0, Object... arg1) {
        LOGGER.error(BRAND + arg0, arg1);
    }

    public static void broadcastToPlayers(MinecraftServer server, String message) {
        var makeCompilerHappy = BRAND_TEXT.copy().append(message);
        server.getPlayerManager().getPlayerList().forEach(player -> {
            player.sendMessage(makeCompilerHappy, false);
        });
    }

    public static void broadcastToPlayers(MinecraftServer server, Text message) {
        var makeCompilerHappy = BRAND_TEXT.copy().append(message);
        server.getPlayerManager().getPlayerList().forEach(player -> {
            player.sendMessage(makeCompilerHappy, false);
        });
    }

    public static void broadcastToOps(MinecraftServer server, String message) {
        broadcastToOps(server, message, Options.requiredPermissionLevel());
    }

    public static void broadcastToOps(MinecraftServer server, Text message) {
        broadcastToOps(server, message, Options.requiredPermissionLevel());
    }

    public static void broadcastToOps(MinecraftServer server, String message, int permissionLevel) {
        var makeCompilerHappy = BRAND_TEXT.copy().append(message);
        server.getPlayerManager().getPlayerList().forEach(player -> {
            if (player.hasPermissionLevel(permissionLevel)) {
                player.sendMessage(makeCompilerHappy, false);
            }
        });
    }

    public static void broadcastToOps(MinecraftServer server, Text message, int permissionLevel) {
        var makeCompilerHappy = BRAND_TEXT.copy().append(message);
        server.getPlayerManager().getPlayerList().forEach(player -> {
            if (player.hasPermissionLevel(permissionLevel)) {
                player.sendMessage(makeCompilerHappy, false);
            }
        });
    }

    public static void send(ServerPlayerEntity player, Text message) {
        player.sendMessage(message, false);
    }

    public static void send(ServerPlayerEntity player, String message) {
        send(player, TextHelper.literal(message));
    }
}
