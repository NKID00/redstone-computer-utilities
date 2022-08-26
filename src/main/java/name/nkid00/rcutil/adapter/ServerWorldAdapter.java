package name.nkid00.rcutil.adapter;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import name.nkid00.rcutil.helper.WorldHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

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
            out.value(WorldHelper.toString(value));
        }
    }

    @Override
    public ServerWorld read(JsonReader in) throws IOException {
        if (in.peek().equals(JsonToken.NULL)) {
            in.nextNull();
            return null;
        } else {
            return WorldHelper.fromString(server, in.nextString());
        }
    }
}
