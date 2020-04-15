package xyz.srclab.common.collection.map;

import com.google.common.collect.ImmutableMap;
import xyz.srclab.annotations.Immutable;
import xyz.srclab.annotations.WrittenReturn;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class MapHelper {

    @Immutable
    public static <NK, NV, OK, OV> Map<NK, NV> map(
            Map<OK, OV> sourceMap, Function<OK, NK> keyMapper, Function<OV, NV> valueMapper) {
        Map<NK, NV> newMap = new LinkedHashMap<>();
        sourceMap.forEach((k, v) -> newMap.put(keyMapper.apply(k), valueMapper.apply(v)));
        return immutable(newMap);
    }

    @SafeVarargs
    public static <K, V> void removeAll(@WrittenReturn Map<K, V> map, K... keys) {
        removeAll(map, Arrays.asList(keys));
    }

    public static <K, V> void removeAll(@WrittenReturn Map<K, V> map, Iterable<? extends K> keys) {
        for (K key : keys) {
            map.remove(key);
        }
    }

    @Immutable
    public static <K, V> Map<K, V> immutable(Map<? extends K, ? extends V> map) {
        return ImmutableMap.copyOf(map);
    }

    @Immutable
    public static <K, V> Map<K, V> filter(
            Map<? extends K, ? extends V> map, Predicate<Map.Entry<? extends K, ? extends V>> predicate) {
        Map<K, V> result = new LinkedHashMap<>();
        map.entrySet().forEach(e -> {
            if (predicate.test(e)) {
                result.put(e.getKey(), e.getValue());
            }
        });
        return immutable((result));
    }
}
