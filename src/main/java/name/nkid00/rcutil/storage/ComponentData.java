package name.nkid00.rcutil.storage;

import java.util.concurrent.ConcurrentHashMap;

import name.nkid00.rcutil.model.addrbus.Addrbus;
import name.nkid00.rcutil.model.bus.Bus;
import name.nkid00.rcutil.model.fileram.Fileram;
import name.nkid00.rcutil.model.ram.Ram;
import name.nkid00.rcutil.model.wires.Wires;

public class ComponentData {
    public ConcurrentHashMap<String, Wires> wires = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Bus> bus = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Addrbus> addrbus = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Ram> ram = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Fileram> fileram = new ConcurrentHashMap<>();
}
