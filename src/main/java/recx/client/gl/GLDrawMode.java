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

/**
 * @author squid233
 * @since 0.1.0
 */
public enum GLDrawMode {
    TRIANGLES(GL.TRIANGLES, 3, 3),
    QUADS(GL.TRIANGLES, 4, 6),
    ;

    private final int enumValue;
    private final int vertexCount;
    private final int indexCount;

    GLDrawMode(int enumValue, int vertexCount, int indexCount) {
        this.enumValue = enumValue;
        this.vertexCount = vertexCount;
        this.indexCount = indexCount;
    }

    public int enumValue() {
        return enumValue;
    }

    public int vertexCount() {
        return vertexCount;
    }

    public int indexCount() {
        return indexCount;
    }
}
