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

import org.overrun.glib.gl.GL;

import java.lang.foreign.MemorySegment;

/**
 * @author squid233
 * @since 0.1.0
 */
public enum VertexElement {
    POSITION(0, 3, GLDataType.FLOAT, false),
    COLOR(1, 4, GLDataType.UNSIGNED_BYTE, true),
    UV0(2, 2, GLDataType.FLOAT, false);

    private final int index;
    private final int size;
    private final GLDataType type;
    private final boolean normalized;

    VertexElement(int index, int size, GLDataType type, boolean normalized) {
        this.index = index;
        this.size = size;
        this.type = type;
        this.normalized = normalized;
    }

    public void enableAttrib(int stride, MemorySegment pointer) {
        GL.enableVertexAttribArray(index);
        GL.vertexAttribPointer(index, size, type.enumValue(), normalized, stride, pointer);
    }

    public int index() {
        return index;
    }

    public int size() {
        return size;
    }

    public GLDataType type() {
        return type;
    }

    public boolean normalized() {
        return normalized;
    }

    public int byteSize() {
        return size * type.byteSize();
    }
}
