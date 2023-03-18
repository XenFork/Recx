/*
 * MIT License
 *
 * Copyright (c) 2023 XenFork Union
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package recx.client.gl;

import java.lang.foreign.MemorySegment;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class VertexFormat {
    public static final VertexFormat POSITION_COLOR = new VertexFormat(
        Map.entry("Position", VertexElement.POSITION),
        Map.entry("Color", VertexElement.COLOR)
    );
    public static final VertexFormat POSITION_COLOR_TEX = new VertexFormat(
        Map.entry("Position", VertexElement.POSITION),
        Map.entry("Color", VertexElement.COLOR),
        Map.entry("UV0", VertexElement.UV0)
    );

    private final Map<String, VertexElement> elementMap;
    private final Map<String, MemorySegment> offsetMap;
    private final int stride;

    @SafeVarargs
    private VertexFormat(Map.Entry<String, VertexElement>... entries) {
        this.elementMap = Map.ofEntries(entries);

        final var map = HashMap.<String, MemorySegment>newHashMap(entries.length);
        // compute offsets
        int offset = 0;
        for (var entry : entries) {
            // use MemorySegment to wrap the offset. the segment is zero-length
            map.put(entry.getKey(), MemorySegment.ofAddress(offset));
            offset += entry.getValue().byteSize();
        }
        this.offsetMap = Collections.unmodifiableMap(map);
        this.stride = offset;
    }

    public void specificPointers() {
        elementMap.forEach((name, element) -> element.specificPointer(stride, offsetMap.get(name)));
    }

    public void forEachElement(BiConsumer<? super String, ? super VertexElement> action) {
        elementMap.forEach(action);
    }

    public VertexElement getElement(String name) {
        return elementMap.get(name);
    }

    public MemorySegment getOffset(String name) {
        return offsetMap.get(name);
    }

    public int stride() {
        return stride;
    }
}
