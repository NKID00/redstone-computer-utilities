package me.nk0.rcu.client.render

import net.minecraft.client.render.*
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import org.joml.Matrix4f
import org.joml.Vector4f

object Color {
    val DEFAULT: Vector4f = Vector4f(0f, 0.823f, 1f, 0.3f)
    val POWERED: Vector4f = Vector4f(1f, 0.251f, 0f, 0.3f)
    val SELECTED: Vector4f = Vector4f(0f, 0.823f, 1f, 0.7f)
    val SELECTED_POWERED: Vector4f = Vector4f(1f, 0.251f, 0f, 0.7f)
    val SELECTED_OUTLINE: Vector4f = Vector4f(1f, 1f, 1f, 1f)
    val SELECTED_POWERED_OUTLINE: Vector4f = Vector4f(0.384f, 0.878f, 1f, 1f)

    fun getFill(selected: Boolean, powered: Boolean): Vector4f = when (Pair(selected, powered)) {
        Pair(true, true) -> {
            SELECTED_POWERED
        }

        Pair(true, false) -> {
            SELECTED
        }

        Pair(false, true) -> {
            POWERED
        }

        else -> {
            DEFAULT
        }
    }

    fun getOutline(powered: Boolean): Vector4f = if (powered) {
        SELECTED_POWERED_OUTLINE
    } else {
        SELECTED_OUTLINE
    }
}

object RcuRenderLayer {
    val DEFAULT = RenderLayer.of(
        "rcu_default",
        VertexFormats.POSITION_COLOR,
        DrawMode.TRIANGLE_STRIP,
        131072,
        RenderLayer.MultiPhaseParameters.builder().program(RenderPhase.COLOR_PROGRAM)
            .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .build(false),
    ) as RenderLayer
    val SELECTED = RenderLayer.of(
        "rcu_selected",
        VertexFormats.POSITION_COLOR,
        DrawMode.TRIANGLE_STRIP,
        131072,
        RenderLayer.MultiPhaseParameters.builder().program(RenderPhase.COLOR_PROGRAM)
            .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .depthTest(RenderPhase.ALWAYS_DEPTH_TEST).target(RenderPhase.OUTLINE_TARGET).build(true),
    ) as RenderLayer
    val OUTLINE = RenderLayer.of(
        "outline",
        VertexFormats.POSITION_COLOR_TEXTURE,
        DrawMode.QUADS,
        256,
        RenderLayer.MultiPhaseParameters.builder().program(RenderPhase.OUTLINE_PROGRAM)
            .texture(RenderPhase.Texture(Identifier("minecraft", "textures/entity/shulker/shulker.png"), false, false))
            .cull(RenderPhase.DISABLE_CULLING).depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
            .target(RenderPhase.OUTLINE_TARGET).build(RenderLayer.OutlineMode.IS_OUTLINE),
    )

    fun getFill(selected: Boolean): RenderLayer = if (selected) {
        SELECTED
    } else {
        DEFAULT
    }

    fun getOutline(selected: Boolean): RenderLayer = OUTLINE
}

fun MatrixStack.translate(vec: Vec3d) = this.translate(vec.x, vec.y, vec.z)

fun MatrixStack.translate(vec: Vec3i) = this.translate(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())

fun drawBoxFilledAtBlock(matrix: MatrixStack, buffer: VertexConsumer, pos: BlockPos, color: Vector4f) =
    WorldRenderer.method_49041(
        matrix,
        buffer,
        pos.x.toFloat(),
        pos.y.toFloat(),
        pos.z.toFloat(),
        (pos.x + 1).toFloat(),
        (pos.y + 1).toFloat(),
        (pos.z + 1).toFloat(),
        color.x,
        color.y,
        color.z,
        color.w,
    )

fun drawBoxOutline(
    matrix: MatrixStack, buffer: VertexConsumer,
    x1: Float,
    y1: Float,
    z1: Float,
    x2: Float,
    y2: Float,
    z2: Float,
) {
    val matrix: Matrix4f? = matrix.peek().getPositionMatrix()

    buffer.vertex(matrix, x1, y1, z1).next()
    buffer.vertex(matrix, x1, y1, z2).next()
    buffer.vertex(matrix, x1, y2, z2).next()
    buffer.vertex(matrix, x1, y2, z1).next()

    buffer.vertex(matrix, x2, y1, z1).next()
    buffer.vertex(matrix, x2, y2, z1).next()
    buffer.vertex(matrix, x2, y2, z2).next()
    buffer.vertex(matrix, x2, y1, z2).next()

    buffer.vertex(matrix, x1, y1, z1).next()
    buffer.vertex(matrix, x2, y1, z1).next()
    buffer.vertex(matrix, x2, y1, z2).next()
    buffer.vertex(matrix, x1, y1, z2).next()

    buffer.vertex(matrix, x1, y2, z1).next()
    buffer.vertex(matrix, x1, y2, z2).next()
    buffer.vertex(matrix, x2, y2, z2).next()
    buffer.vertex(matrix, x2, y2, z1).next()

    buffer.vertex(matrix, x1, y1, z1).next()
    buffer.vertex(matrix, x1, y2, z1).next()
    buffer.vertex(matrix, x2, y2, z1).next()
    buffer.vertex(matrix, x2, y1, z1).next()

    buffer.vertex(matrix, x1, y1, z2).next()
    buffer.vertex(matrix, x2, y1, z2).next()
    buffer.vertex(matrix, x2, y2, z2).next()
    buffer.vertex(matrix, x1, y2, z2).next()
}

fun drawBoxOutlineAtBlock(
    matrix: MatrixStack, buffer: VertexConsumer, pos: BlockPos,
) = drawBoxOutline(
    matrix, buffer,
    pos.x.toFloat(),
    pos.y.toFloat(),
    pos.z.toFloat(),
    (pos.x + 1).toFloat(),
    (pos.y + 1).toFloat(),
    (pos.z + 1).toFloat(),
)
