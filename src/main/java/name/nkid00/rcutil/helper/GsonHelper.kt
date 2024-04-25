package name.nkid00.rcutil.helper

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import name.nkid00.rcutil.adapter.*
import name.nkid00.rcutil.model.Interface
import name.nkid00.rcutil.util.BlockPosWithWorld
import name.nkid00.rcutil.util.TypedArgument
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import java.util.*

object GsonHelper {
    private var gson: Gson? = null

    @JvmStatic
    fun gsonBuilder(server: MinecraftServer): GsonBuilder {
        val registryManager = server.registryManager
        return GsonBuilder()
                .registerTypeAdapter(BlockPos::class.java, BlockPosAdapter())
                .registerTypeAdapter(BlockPosWithWorld::class.java, BlockPosWithWorldAdapter())
                .registerTypeAdapter(Vec3i::class.java, Vec3iAdapter())
                .registerTypeAdapter(ServerWorld::class.java, ServerWorldAdapter(server))
                .registerTypeAdapter(BitSet::class.java, BitSetAdapter())
                .registerTypeAdapterFactory(
                        object : RegistryAdapterFactory<Item?>(registryManager.get(RegistryKeys.ITEM)) {
                        })
                .registerTypeAdapter(Interface::class.java, InterfaceAdapter())
                .registerTypeAdapter(TypedArgument::class.java, TypedArgumentAdapter())
                .setLenient()
                .disableHtmlEscaping()
    }

    @JvmStatic
    fun init(server: MinecraftServer) {
        gson = gsonBuilder(server).serializeNulls().create()
    }

    @JvmStatic
    fun gson(): Gson? {
        return gson
    }
}
