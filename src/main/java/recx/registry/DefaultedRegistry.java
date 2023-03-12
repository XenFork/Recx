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

package recx.registry;

import org.jetbrains.annotations.NotNull;
import recx.util.Identifier;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class DefaultedRegistry<T> implements Registry<T> {
    private final Map<Integer, T> rawIdToEntry = new LinkedHashMap<>();
    private final Map<T, Integer> entryToRawId = new LinkedHashMap<>();
    private final Map<Identifier, T> idToEntry = new LinkedHashMap<>();
    private final Map<T, Identifier> entryToId = new LinkedHashMap<>();
    private final Supplier<T> lazyDefault;
    private T defaultEntry;
    private int nextId = -1;

    public DefaultedRegistry(Supplier<T> lazyDefault) {
        this.lazyDefault = lazyDefault;
    }

    public T defaultEntry() {
        if (defaultEntry == null) {
            defaultEntry = lazyDefault.get();
        }
        return defaultEntry;
    }

    @Override
    public T get(int rawId) {
        return rawIdToEntry.getOrDefault(rawId, defaultEntry());
    }

    @Override
    public T get(Identifier id) {
        return idToEntry.getOrDefault(id, defaultEntry());
    }

    @Override
    public int getRawId(T entry) {
        return entryToRawId.getOrDefault(entry, entryToRawId.getOrDefault(defaultEntry(), 0));
    }

    @Override
    public Identifier getId(T entry) {
        return entryToId.getOrDefault(entry, entryToId.get(defaultEntry()));
    }

    @Override
    public <R extends T> R set(int rawId, Identifier id, R entry) {
        rawIdToEntry.put(rawId, entry);
        entryToRawId.put(entry, rawId);
        idToEntry.put(id, entry);
        entryToId.put(entry, id);
        if (rawId > nextId) {
            nextId = rawId;
        }
        return entry;
    }

    @Override
    public <R extends T> R add(Identifier id, R entry) {
        nextId++;
        return set(nextId, id, entry);
    }

    @Override
    public T remove(int rawId) {
        final T entry = rawIdToEntry.remove(rawId);
        entryToRawId.remove(entry);
        idToEntry.remove(entryToId.remove(entry));
        return entry;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return rawIdToEntry.values().iterator();
    }
}
