package name.nkid00.rcutil.storage;

import java.util.concurrent.ConcurrentHashMap;

import name.nkid00.rcutil.model.Selection;
import name.nkid00.rcutil.model.wires.Wires;

public record PlayerData(ConcurrentHashMap<String, Wires> wires,
        ConcurrentHashMap<String, Object> bus,
        ConcurrentHashMap<String, Object> addrbus,
        ConcurrentHashMap<String, Object> ram,
        ConcurrentHashMap<String, Object> fileram,
        ConcurrentHashMap<String, Object> connection,
        Selection selection) {
    public PlayerData {
        wires = wires == null ? new ConcurrentHashMap<>() : wires;
        bus = bus == null ? new ConcurrentHashMap<>() : bus;
        addrbus = addrbus == null ? new ConcurrentHashMap<>() : addrbus;
        ram = ram == null ? new ConcurrentHashMap<>() : ram;
        fileram = fileram == null ? new ConcurrentHashMap<>() : fileram;
        connection = connection == null ? new ConcurrentHashMap<>() : connection;
        selection = selection == null ? new Selection() : selection;
    }

    public PlayerData() {
        this(null, null, null, null, null, null, null);
    }
}
