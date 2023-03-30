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

import org.overrun.glib.gl.GL;
import recx.client.gl.GLDrawMode;
import recx.client.gl.GLStateManager;
import recx.client.gl.VertexFormat;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static java.lang.foreign.ValueLayout.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class Tessellator implements Batch, AutoCloseable {
    private static final long VERTEX_COUNT = 15000L;
    private static final long INDEX_COUNT = 20000L;
    private static final long BUFFER_SIZE = VERTEX_COUNT * VertexFormat.POSITION_COLOR_TEX.stride();
    private static final long INDEX_BUFFER_SIZE = INDEX_COUNT * Integer.BYTES;
    private static Tessellator instance;
    private final Arena arena = Arena.openConfined();
    private final MemorySegment buffer = arena.allocate(BUFFER_SIZE);
    private final MemorySegment indexBuffer = arena.allocate(INDEX_BUFFER_SIZE);
    private GLDrawMode drawMode = GLDrawMode.QUADS;
    private float x, y, z;
    private byte r = -1, g = -1, b = -1, a = -1;
    private float u, v;
    private long offset;
    private int vertexCount, indexCount;
    private int vao, vbo, ebo;

    private Tessellator() {
    }

    public static Tessellator getInstance() {
        if (instance == null) {
            instance = new Tessellator();
        }
        return instance;
    }

    public static void free() {
        if (instance != null) {
            instance.close();
        }
    }

    private void clear() {
        offset = 0;
        vertexCount = 0;
        indexCount = 0;
    }

    public void begin(GLDrawMode drawMode) {
        clear();
        this.drawMode = drawMode;
    }

    @Override
    public void begin() {
        begin(GLDrawMode.QUADS);
    }

    @Override
    public void end() {
        flush();
    }

    @Override
    public void flush() {
        if (vertexCount <= 0) return;

        final boolean noVbo = vbo <= 0;
        final boolean noEbo = ebo <= 0;
        if (vao <= 0) vao = GL.genVertexArray();
        if (noVbo) vbo = GL.genBuffer();
        if (noEbo) ebo = GL.genBuffer();

        GLStateManager.bindVertexArray(vao);

        GLStateManager.bindArrayBuffer(vbo);
        if (noVbo) {
            GL.bufferData(GL.ARRAY_BUFFER, buffer, GL.STREAM_DRAW);
            VertexFormat.POSITION_COLOR_TEX.specificPointers();
        } else {
            GL.bufferSubData(GL.ARRAY_BUFFER, 0, offset, buffer);
        }
        GLStateManager.bindArrayBuffer(0);

        if (noEbo) {
            GL.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, ebo);
            GL.bufferData(GL.ELEMENT_ARRAY_BUFFER, indexBuffer, GL.STREAM_DRAW);
        } else {
            GL.bufferSubData(GL.ELEMENT_ARRAY_BUFFER, 0, Integer.toUnsignedLong(indexCount) << 2, indexBuffer);
        }

        GL.drawElements(drawMode.enumValue(), indexCount, GL.UNSIGNED_INT, MemorySegment.NULL);

        GLStateManager.bindVertexArray(0);

        clear();
    }

    @Override
    public Tessellator vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public Tessellator vertex(float x, float y) {
        Batch.super.vertex(x, y);
        return this;
    }

    @Override
    public Tessellator color(byte r, byte g, byte b, byte a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }

    @Override
    public Tessellator color(int r, int g, int b, int a) {
        Batch.super.color(r, g, b, a);
        return this;
    }

    @Override
    public Tessellator color(int rgba) {
        Batch.super.color(rgba);
        return this;
    }

    @Override
    public Tessellator texCoords(float u, float v) {
        this.u = u;
        this.v = v;
        return this;
    }

    @Override
    public Tessellator indices(int... indices) {
        for (int index : indices) {
            indexBuffer.setAtIndex(JAVA_INT, indexCount, index + vertexCount);
            indexCount++;
        }
        return this;
    }

    @Override
    public void emit() {
        // position
        buffer.set(JAVA_FLOAT_UNALIGNED, offset, x);
        buffer.set(JAVA_FLOAT_UNALIGNED, offset + 4, y);
        buffer.set(JAVA_FLOAT_UNALIGNED, offset + 8, z);
        // color
        buffer.set(JAVA_BYTE, offset + 12, r);
        buffer.set(JAVA_BYTE, offset + 13, g);
        buffer.set(JAVA_BYTE, offset + 14, b);
        buffer.set(JAVA_BYTE, offset + 15, a);
        // texture
        buffer.set(JAVA_FLOAT_UNALIGNED, offset + 16, u);
        buffer.set(JAVA_FLOAT_UNALIGNED, offset + 20, v);
        offset += 24;
        vertexCount++;

        final int count = drawMode.vertexCount();
        if (vertexCount % count == 0 && vertexCount >= VERTEX_COUNT - count) {
            flush();
        }
    }

    @Override
    public void close() {
        arena.close();
    }
}
