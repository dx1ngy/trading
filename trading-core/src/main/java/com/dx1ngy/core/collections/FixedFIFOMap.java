package com.dx1ngy.core.collections;

import java.util.LinkedHashMap;
import java.util.Map;

public class FixedFIFOMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public FixedFIFOMap(int maxSize) {
        super(maxSize + 1, 1.0f, false);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // 当大小超过maxSize时，移除最旧的条目
        return size() > maxSize;
    }
}
