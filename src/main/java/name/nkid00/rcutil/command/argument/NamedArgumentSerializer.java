package name.nkid00.rcutil.command.argument;

import java.util.function.Function;

import com.google.gson.JsonObject;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class NamedArgumentSerializer<T extends NamedArgumentType<?>>
        implements ArgumentSerializer<T, NamedArgumentSerializer<T>.Properties> {
    private Function<String, T> factory;

    public NamedArgumentSerializer(Function<String, T> factory) {
        this.factory = factory;
    }
    
    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeString(properties.name);
    }

    @Override
    public Properties fromPacket(PacketByteBuf packetByteBuf) {
        return new Properties(packetByteBuf.readString());
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        jsonObject.addProperty("name", properties.name);
    }

    @Override
    public Properties getArgumentTypeProperties(T namedArgumentType) {
        return new Properties(namedArgumentType.argumentName());
    }

    public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<T> {
        final String name;

        Properties(String name) {
            this.name = name;
        }

        @Override
        public T createType(CommandRegistryAccess commandRegistryAccess) {
            return NamedArgumentSerializer.this.factory.apply(this.name);
        }

        @Override
        public ArgumentSerializer<T, ?> getSerializer() {
            return NamedArgumentSerializer.this;
        }
    }
}
