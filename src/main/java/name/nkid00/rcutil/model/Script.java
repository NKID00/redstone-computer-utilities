package name.nkid00.rcutil.model;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.netty.channel.ChannelHandlerContext;
import name.nkid00.rcutil.event.Event;
import name.nkid00.rcutil.helper.I18n;
import net.minecraft.text.Text;

public class Script {
    public final String name;
    public final String description;
    public final String addr;
    public final AtomicBoolean alive = new AtomicBoolean(true);
    public final Set<Event> events = ConcurrentHashMap.newKeySet();
    public final ChannelHandlerContext ctx;

    public Script(String name, String description, String address, ChannelHandlerContext ctx) {
        this.name = name;
        this.description = description;
        this.addr = address;
        this.ctx = ctx;
    }

    public Text info(UUID uuid) {
        if (description.isEmpty()) {
            return I18n.t(uuid, "rcutil.info.script.detail", name,
                    I18n.t(uuid, "rcutil.info.script.no_description"),
                    String.join(", ", events.stream()
                            .map(event -> event.toString())
                            .toList().toArray(new String[0])));
        } else {
            return I18n.t(uuid, "rcutil.info.script.detail", name, description,
                    String.join(", ", events.stream()
                            .map(event -> event.toString())
                            .toList().toArray(new String[0])));
        }
    }

    public boolean eventExists(Event event) {
        return events.contains(event);
    }

    public void subscribe(Event event) {
        events.add(event);
    }

    public void unsubscribe(Event event) {
        events.remove(event);
    }

    public boolean isAddr(String addr) {
        return this.addr.equals(addr);
    }

    @Override
    public int hashCode() {
        // some random prime number
        int result = 31 + (name == null ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Script) {
            var other = (Script) obj;
            if (name == null ? other.name != null : !name.equals(other.name)) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
