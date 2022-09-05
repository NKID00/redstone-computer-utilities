package name.nkid00.rcutil.helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MapHelper {
    private static final long PARALLELISM_THRESHOLD = 4;

    public static <K, V> void forEachKeySynchronized(ConcurrentHashMap<K, V> map, Consumer<? super K> action) {
        map.forEachKey(Long.MAX_VALUE, action);
    }

    public static <K, V> void forEachKeyParallelized(ConcurrentHashMap<K, V> map, Consumer<? super K> action) {
        map.forEachKey(PARALLELISM_THRESHOLD, action);
    }

    public static <K, V> void forEachValueSynchronized(ConcurrentHashMap<K, V> map, Consumer<? super V> action) {
        map.forEachValue(Long.MAX_VALUE, action);
    }

    public static <K, V> void forEachValueParallelized(ConcurrentHashMap<K, V> map, Consumer<? super K> action) {
        map.forEachKey(PARALLELISM_THRESHOLD, action);
    }

    public static <K, V> void forEachEntrySynchronized(ConcurrentHashMap<K, V> map,
            Consumer<? super Map.Entry<K, V>> action) {
        map.forEachEntry(Long.MAX_VALUE, action);
    }

    public static <K, V> void forEachEntryParallelized(ConcurrentHashMap<K, V> map,
            Consumer<? super Map.Entry<K, V>> action) {
        map.forEachEntry(PARALLELISM_THRESHOLD, action);
    }
}
