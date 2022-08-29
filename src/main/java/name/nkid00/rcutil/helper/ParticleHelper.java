package name.nkid00.rcutil.helper;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class ParticleHelper {
    public static <T extends ParticleEffect> void highlight(
            ServerWorld world, ServerPlayerEntity viewer, T particle, BlockPos pos) {
        var centerPos = PosHelper.center(pos);
        for (var direction : Direction.values()) {
            var deltaVector = PosHelper.scale(PosHelper.getPerpendicularVector(direction), 0.15f);
            var particlePos = PosHelper.applyOffset(centerPos, PosHelper.scale(direction.getUnitVector(), 0.65f));
            world.spawnParticles(viewer, particle, true,
                    particlePos.getX(), particlePos.getY(), particlePos.getZ(), 10,
                    deltaVector.getX(), deltaVector.getY(), deltaVector.getZ(), 0);
        }
    }

    public static void highlight(
            ServerWorld world, ServerPlayerEntity viewer, Vec3f color, float scale, BlockPos pos) {
        highlight(world, viewer, new DustParticleEffect(color, scale), pos);
    }

    public static void highlight(
            ServerWorld world, ServerPlayerEntity viewer, Vec3f color, BlockPos pos) {
        highlight(world, viewer, color, 1.0f, pos);
    }

    public static void highlight(
            ServerWorld world, ServerPlayerEntity viewer, float r, float g, float b, BlockPos pos) {
        highlight(world, viewer, new Vec3f(r, g, b), pos);
    }
}
