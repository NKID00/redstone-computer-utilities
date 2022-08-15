package name.nkid00.rcutil.manager;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.model.Selection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class SelectionManager {
    private static ConcurrentHashMap<UUID, Selection> selections = new ConcurrentHashMap<>();

    public static Selection selection(UUID uuid) {
        return selections.computeIfAbsent(uuid, _uuid -> new Selection());
    }

    public static void selectMsb(UUID uuid, BlockPos pos, DimensionType dimension) {
        selection(uuid).selectMsb(pos, dimension);
    }

    public static void selectLsb(UUID uuid, BlockPos pos, DimensionType dimension) {
        selection(uuid).selectLsb(pos, dimension);
    }

    public static boolean selected(UUID uuid) {
        return selection(uuid).selected;
    }

    public static void load(JsonReader reader, Gson gson) {
        try {
            selections = gson.fromJson(reader,
                    new TypeToken<ConcurrentHashMap<UUID, Selection>>() {
                    }.getType());
        } catch (JsonParseException e) {
            Log.error("Error occurred when loading selections, generating empty record", e);
            selections = new ConcurrentHashMap<>();
        }
    }

    public static void save(JsonWriter writer, Gson gson) {
        try {
            gson.toJson(selections, new TypeToken<ConcurrentHashMap<UUID, Selection>>() {
            }.getType(), writer);
        } catch (JsonParseException e) {
            Log.error("Error occurred when saving selections", e);
        }
    }
}
