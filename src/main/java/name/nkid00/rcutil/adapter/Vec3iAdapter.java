package name.nkid00.rcutil.adapter;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import net.minecraft.util.math.Vec3i;

public class Vec3iAdapter extends TypeAdapter<Vec3i> {
    @Override
    public void write(JsonWriter out, Vec3i value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.beginArray();
            out.value(value.getX());
            out.value(value.getY());
            out.value(value.getZ());
            out.endArray();
        }
    }

    @Override
    public Vec3i read(JsonReader in) throws IOException {
        if (in.peek().equals(JsonToken.NULL)) {
            in.nextNull();
            return null;
        } else {
            in.beginArray();
            var x = in.nextInt();
            var y = in.nextInt();
            var z = in.nextInt();
            in.endArray();
            return new Vec3i(x, y, z);
        }
    }
}
