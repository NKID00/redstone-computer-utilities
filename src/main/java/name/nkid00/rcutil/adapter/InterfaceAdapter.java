package name.nkid00.rcutil.adapter;

import java.io.IOException;
import java.util.LinkedList;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import name.nkid00.rcutil.model.Interface;
import name.nkid00.rcutil.util.BlockPosWithWorld;
import net.minecraft.util.math.Vec3i;

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
            String name = "";
            BlockPosWithWorld lsb = new BlockPosWithWorld(null, null);
            Vec3i increment = new Vec3i(0, 0, 0);
            int size = 0;
            LinkedList<String> option = new LinkedList<>();
            in.beginObject();
            while (in.hasNext()) {
                switch (in.nextName()) {
                    case "name":
                        name = in.nextString();
                        break;
                    case "lsb":
                        lsb = BLOCK_POS_WITH_WORLD_ADAPTER.read(in);
                        break;
                    case "increment":
                        increment = VEC3I_ADAPTER.read(in);
                        break;
                    case "size":
                        size = in.nextInt();
                        break;
                    case "option":
                        in.beginArray();
                        while (in.hasNext()) {
                            option.add(in.nextString());
                        }
                        in.endArray();
                }
            }
            in.endObject();
            return new Interface(name, lsb.world(), lsb.pos(), increment, size, option);
        }
    }
}
