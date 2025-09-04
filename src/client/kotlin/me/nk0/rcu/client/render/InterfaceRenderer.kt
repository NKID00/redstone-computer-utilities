package me.nk0.rcu.client.render

import me.nk0.rcu.manager.InterfaceManager
import me.nk0.rcu.model.Interface
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack

object InterfaceRenderer {
    fun render(ctx: WorldRenderContext) {
        val consumer = ctx.worldRenderer().bufferBuilders.outlineVertexConsumers
        consumer.setColor(255, 255, 255, 255)
        val matrix = ctx.matrixStack()
        matrix.push()
        matrix.translate(ctx.camera().pos.negate())
        InterfaceManager.iterable().forEach { interfaze -> renderOne(matrix, consumer, interfaze) }
        matrix.pop()
    }

    private fun renderOne(matrix: MatrixStack, consumer: VertexConsumerProvider, interfaze: Interface) {
        val selected = true
        interfaze.forEach { pos ->
            val powered = pos.readDigitalOrZero()
            val color = Color.getFill(selected, powered)
            val buffer = consumer.getBuffer(RcuRenderLayer.getFill(selected))
            drawBoxFilledAtBlock(matrix, buffer, pos, color)
            val outline = consumer.getBuffer(RcuRenderLayer.getOutline(selected))
            drawBoxOutlineAtBlock(matrix, outline, pos)
        }
    }
}