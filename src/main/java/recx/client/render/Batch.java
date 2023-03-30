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

package recx.client.render;

/**
 * @author squid233
 * @since 0.1.0
 */
public interface Batch {
    void begin();

    void end();

    void flush();

    Batch indices(int... indices);

    Batch vertex(float x, float y, float z);

    default Batch vertex(float x, float y) {
        return vertex(x, y, 0f);
    }

    Batch color(byte r, byte g, byte b, byte a);

    default Batch color(int r, int g, int b, int a) {
        return color((byte) r, (byte) g, (byte) b, (byte) a);
    }

    default Batch color(int rgba) {
        return color(rgba >>> 24, rgba >>> 16, rgba >>> 8, rgba);
    }

    Batch texCoords(float u, float v);

    void emit();
}
