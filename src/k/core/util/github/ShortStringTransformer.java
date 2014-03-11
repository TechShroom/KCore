package k.core.util.github;

import java.util.*;

public final class ShortStringTransformer {
    public static <T extends ShortStringProvider> Collection<String> asShortStringCollection(
            Collection<T> list) {
        List<String> l = new ArrayList<String>(list.size());
        for (T t : list) {
            l.add(t.toShortString());
        }
        return l;
    }
}
