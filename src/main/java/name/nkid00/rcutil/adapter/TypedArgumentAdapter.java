package name.nkid00.rcutil.adapter;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import name.nkid00.rcutil.manager.InterfaceManager;
import name.nkid00.rcutil.util.TypedArgument;

public class TypedArgumentAdapter extends TypeAdapter<TypedArgument> {
    private static final InterfaceAdapter INTERFACE_ADAPTER = new InterfaceAdapter();

    @Override
    public void write(JsonWriter out, TypedArgument value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            switch (value.type()) {
                default:
                case Literal:
                    out.value(value.value());
                    break;
                case Interface:
                    INTERFACE_ADAPTER.write(out, InterfaceManager.interfaceByName(value.value()));
                    break;

            }
        }
    }

    @Override
    public TypedArgument read(JsonReader in) throws IOException {
        if (in.peek().equals(JsonToken.NULL)) {
            in.nextNull();
            return null;
        } else {
            // TODO: deserialize TypedArgument
            return null;
        }
    }
}
