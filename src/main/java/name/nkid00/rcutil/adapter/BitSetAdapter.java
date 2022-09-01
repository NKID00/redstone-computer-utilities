package name.nkid00.rcutil.adapter;

import java.io.IOException;
import java.util.BitSet;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import name.nkid00.rcutil.helper.BitSetHelper;

public class BitSetAdapter extends TypeAdapter<BitSet> {
    @Override
    public void write(JsonWriter out, BitSet value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(BitSetHelper.toBase64(value));
        }
    }

    @Override
    public BitSet read(JsonReader in) throws IOException {
        if (in.peek().equals(JsonToken.NULL)) {
            in.nextNull();
            return null;
        } else {
            return BitSetHelper.fromBase64(in.nextString());
        }
    }
    
}
