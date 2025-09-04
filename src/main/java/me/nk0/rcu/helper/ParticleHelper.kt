package me.nk0.rcu.helper

import me.nk0.rcu.util.Vec3f
import net.minecraft.particle.DustParticleEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

object ParticleHelper {
    fun <T : ParticleEffect?> highlight(
        world: ServerWorld, viewer: ServerPlayerEntity?, particle: T, pos: BlockPos,
    ) {
        val centerPos = PosHelper.center(pos)
        for (direction in Direction.entries) {
            val deltaVector = PosHelper.scale(PosHelper.getPerpendicularVector(direction), 0.15f)
            val particlePos = PosHelper.applyOffset(
                centerPos,
                PosHelper.scale(PosHelper.toVec3f(direction.unitVector), 0.65f),
            )
            world.spawnParticles(
                viewer, particle, true,
                particlePos!!.getX().toDouble(), particlePos.getY().toDouble(), particlePos.getZ().toDouble(), 10,
                deltaVector!!.getX().toDouble(), deltaVector.getY().toDouble(), deltaVector.getZ().toDouble(), 0.0,
            )
        }
    }

    fun highlight(
        world: ServerWorld, viewer: ServerPlayerEntity?, color: Vec3f?, scale: Float, pos: BlockPos,
    ) {
        highlight(world, viewer, DustParticleEffect(color, scale), pos)
    }

    @JvmStatic
    fun highlight(
        world: ServerWorld, viewer: ServerPlayerEntity?, color: Vec3f?, pos: BlockPos,
    ) {
        highlight(world, viewer, color, 1.0f, pos)
    }

    fun highlight(
        world: ServerWorld, viewer: ServerPlayerEntity?, r: Float, g: Float, b: Float, pos: BlockPos,
    ) {
        highlight(world, viewer, Vec3f(r, g, b), pos)
    }
}
