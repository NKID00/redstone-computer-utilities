package name.nkid00.rcutil.adapter;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import name.nkid00.rcutil.model.Interface;

public class InterfaceAdapter extends TypeAdapter<Interface> {
    private static final BlockPosWithWorldAdapter BLOCK_POS_WITH_WORLD_ADAPTER = new BlockPosWithWorldAdapter();
    private static final Vec3iAdapter VEC3I_ADAPTER = new Vec3iAdapter();

    @Override
    public void write(JsonWriter out, Interface value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.beginObject();
            out.name("name");
            out.value(value.name());
            out.name("lsb");
            BLOCK_POS_WITH_WORLD_ADAPTER.write(out, value.lsb().toBlockPosWithWorld());
            out.name("increment");
            VEC3I_ADAPTER.write(out, value.increment());
            out.name("size");
            out.value(value.size());
            out.name("option");
            out.beginArray();
            for (var option : value.option()) {
                out.value(option);
            }
            out.endArray();
            out.endObject();
        }
    }

    @Override
    public Interface read(JsonReader in) throws IOException {
        if (in.peek().equals(JsonToken.NULL)) {
            in.nextNull();
            return null;
        } else {
            // TODO: deserialize Interface
            return null;
        }
    }
}
