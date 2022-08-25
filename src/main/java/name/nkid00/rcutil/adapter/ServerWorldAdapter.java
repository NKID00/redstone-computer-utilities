package name.nkid00.rcutil.adapter;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class ServerWorldAdapter extends TypeAdapter<ServerWorld> {
    private final MinecraftServer server;

    public ServerWorldAdapter(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void write(JsonWriter out, ServerWorld value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.getRegistryKey().getValue().toString());
        }
    }

    @Override
    public ServerWorld read(JsonReader in) throws IOException {
        if (in.peek().equals(JsonToken.NULL)) {
            in.nextNull();
            return null;
        } else {
            return server.getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier(in.nextString())));
        }
    }
}
