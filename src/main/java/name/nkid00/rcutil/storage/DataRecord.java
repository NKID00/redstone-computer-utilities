package name.nkid00.rcutil.storage;

import java.util.concurrent.ConcurrentHashMap;

import name.nkid00.rcutil.model.connection.Connection;

public class DataRecord {
    public ComponentData component = new ComponentData();
    public ConcurrentHashMap<String, Connection> connection = new ConcurrentHashMap<>();
    public SelectionData selection = new SelectionData();
}
