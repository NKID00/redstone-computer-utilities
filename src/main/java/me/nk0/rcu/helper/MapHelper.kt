package me.nk0.rcu.helper

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

object MapHelper {
    private const val PARALLELISM_THRESHOLD: Long = 4

    @JvmStatic
    fun <K, V> forEachKeySynchronized(map: ConcurrentHashMap<K, V>, action: Consumer<in K>?) {
        map.forEachKey(Long.MAX_VALUE, action)
    }

    fun <K, V> forEachKeyParallelized(map: ConcurrentHashMap<K, V>, action: Consumer<in K>?) {
        map.forEachKey(PARALLELISM_THRESHOLD, action)
    }

    fun <K, V> forEachValueSynchronized(map: ConcurrentHashMap<K, V>, action: Consumer<in V>?) {
        map.forEachValue(Long.MAX_VALUE, action)
    }

    fun <K, V> forEachValueParallelized(map: ConcurrentHashMap<K, V>, action: Consumer<in K>?) {
        map.forEachKey(PARALLELISM_THRESHOLD, action)
    }

    fun <K, V> forEachEntrySynchronized(
        map: ConcurrentHashMap<K, V>,
        action: Consumer<in MutableMap.MutableEntry<K, V>?>?,
    ) {
        map.forEachEntry(Long.MAX_VALUE, action)
    }

    fun <K, V> forEachEntryParallelized(
        map: ConcurrentHashMap<K, V>,
        action: Consumer<in MutableMap.MutableEntry<K, V>?>?,
    ) {
        map.forEachEntry(PARALLELISM_THRESHOLD, action)
    }
}
