package k.core.util.github;

import java.util.*;
import java.util.Map.Entry;

public final class ShortStringTransformer {
    public static <T extends ShortStringProvider> Collection<String> asShortStringCollection(
            Collection<T> collection) {
        List<String> l = new ArrayList<String>(collection.size());
        for (T t : collection) {
            l.add(t.toShortString());
        }
        return l;
    }

    public static <K, V extends ShortStringProvider> Map<K, String> asShortStringMap(
            Map<K, V> map) {
        Map<K, String> copy = new HashMap<K, String>(map.size());
        for (Entry<K, V> e : map.entrySet()) {
            copy.put(e.getKey(), e.getValue().toShortString());
        }
        return copy;
    }
}
