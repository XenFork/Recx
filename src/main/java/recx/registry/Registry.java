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

import recx.util.Identifier;

/**
 * @author squid233
 * @since 0.1.0
 */
public interface Registry<T> extends Iterable<T> {
    static <T, R extends T> R register(Registry<T> registry, Identifier id, R entry) {
        return registry.add(id, entry);
    }

    static <T, R extends T> R register(Registry<T> registry, String id, R entry) {
        return register(registry, Identifier.of(id), entry);
    }

    T get(int rawId);

    T get(Identifier id);

    int getRawId(T entry);

    Identifier getId(T entry);

    <R extends T> R set(int rawId, Identifier id, R entry);

    <R extends T> R add(Identifier id, R entry);

    T remove(int rawId);
}
