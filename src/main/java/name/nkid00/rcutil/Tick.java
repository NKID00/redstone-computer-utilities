package name.nkid00.rcutil;

import java.io.EOFException;
import java.io.IOException;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.dimension.DimensionType;
import name.nkid00.rcutil.exception.BlockNotRedstoneWireException;

public class Tick {
    public static void register(ServerWorld world) {
        DimensionType dimensionType = world.getDimension();
        RCUtil.fileRams.forEach((k, v) -> {
            if (v.running && dimensionType == v.dimensionType) {
                try {
                    v.tick(world);
                } catch (BlockNotRedstoneWireException e) {
                    v.setRunning(false);
                    world.getServer().getCommandSource().sendFeedback(new TranslatableText("rcutil.fileram.failed.block", v.fancyName).formatted(Formatting.RED), true);
                } catch (EOFException e) {
                    v.setRunning(false);
                    world.getServer().getCommandSource().sendFeedback(new TranslatableText("rcutil.fileram.failed.eof", v.fancyName).formatted(Formatting.RED), true);
                } catch (IOException e) {
                    v.setRunning(false);
                    world.getServer().getCommandSource().sendFeedback(new TranslatableText("rcutil.fileram.failed.io", v.fancyName).formatted(Formatting.RED), true);
                }
            }
        });
    }
}
