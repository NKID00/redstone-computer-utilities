package me.nk0.rcu.client

import me.nk0.rcu.client.render.InterfaceRenderer
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

fun init() {
    WorldRenderEvents.AFTER_ENTITIES.register(InterfaceRenderer::render)
}
