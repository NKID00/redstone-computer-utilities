package name.nkid00.rcutil.event;

import com.google.gson.JsonObject;

import name.nkid00.rcutil.exception.ApiException;
import name.nkid00.rcutil.helper.GsonHelper;
import name.nkid00.rcutil.util.BlockPosWithWorld;

public class BlockUpdateEvent extends Event {
    private final BlockPosWithWorld pos;
    private final Type type;

    public BlockUpdateEvent(BlockPosWithWorld pos, Type type) {
        this.pos = pos;
        this.type = type;
    }

    public BlockUpdateEvent(JsonObject param) throws ApiException {
        // TODO: check param
        pos = GsonHelper.gson().fromJson(param.get("pos"), BlockPosWithWorld.class);
        type = Type.fromString(param.get("pos").getAsString());
    }

    @Override
    public String name() {
        return "blockUpdate";
    }

    @Override
    public JsonObject param() {
        var result = new JsonObject();
        result.add("pos", GsonHelper.gson().toJsonTree(pos));
        result.addProperty("type", type.toString());
        return result;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public void broadcast() {
        super.broadcast(new JsonObject());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof BlockUpdateEvent) {
            var other = (BlockUpdateEvent) obj;
            if (pos == null ? other.pos != null : !pos.equals(other.pos)) {
                return false;
            }
            if (type == null ? other.type != null : !type.equals(other.type)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public enum Type {
        NeighborUpdate, PostPlacement;

        @Override
        public String toString() {
            switch (this) {
                default:
                case NeighborUpdate:
                    return "neighborUpdate";
                case PostPlacement:
                    return "postPlacement";
            }
        }

        public static Type fromString(String s) {
            switch (s) {
                default:
                case "neighborUpdate":
                    return NeighborUpdate;
                case "postPlacement":
                    return PostPlacement;
            }
        }
    }
}
