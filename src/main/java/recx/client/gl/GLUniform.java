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

import org.joml.Matrix4fc;
import org.joml.Vector4fc;
import org.overrun.glib.gl.GL;
import org.overrun.glib.gl.GLLoader;
import org.overrun.glib.joml.Matrixn;
import org.overrun.glib.joml.Vectorn;
import org.overrun.glib.util.MemoryUtil;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Locale;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class GLUniform implements AutoCloseable {
    public static final int TYPE_VEC4 = 7;
    public static final int TYPE_MAT4 = 10;
    private final int type;
    private final int location;
    private final MemorySegment buffer;
    private boolean dirty = true;

    public GLUniform(int type, int location) {
        this.type = type;
        this.location = location;
        this.buffer = MemoryUtil.calloc(1, layout(type));
    }

    public static int getTypeFromString(String typeName) {
        return switch (typeName.toLowerCase(Locale.ROOT)) {
            case "vec4" -> TYPE_VEC4;
            case "mat4" -> TYPE_MAT4;
            default -> throw new IllegalStateException("Unexpected value: " + typeName);
        };
    }

    private static MemoryLayout layout(int type) {
        return switch (type) {
            case TYPE_VEC4 -> Vectorn.VEC4F;
            case TYPE_MAT4 -> Matrixn.MAT4F;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    private void markDirty() {
        dirty = true;
    }

    public void set(float x, float y, float z, float w) {
        markDirty();
        buffer.set(ValueLayout.JAVA_FLOAT, 0, x);
        buffer.set(ValueLayout.JAVA_FLOAT, 4, y);
        buffer.set(ValueLayout.JAVA_FLOAT, 8, z);
        buffer.set(ValueLayout.JAVA_FLOAT, 12, w);
    }

    public void set(float... values) {
        markDirty();
        for (int i = 0, len = values.length; i < len; i++) {
            buffer.setAtIndex(ValueLayout.JAVA_FLOAT, i, values[i]);
        }
    }

    public void set(Vector4fc value) {
        markDirty();
        Vectorn.put(value, buffer);
    }

    public void set(Matrix4fc value) {
        markDirty();
        Matrixn.put(value, buffer);
    }

    public void upload(GLProgram program) {
        if (!dirty) return;
        dirty = false;

        final boolean arb = program != null && GLLoader.getExtCapabilities().GL_ARB_separate_shader_objects;
        switch (type) {
            case TYPE_VEC4 -> {
                if (arb) GL.programUniform4fv(program.id(), location, 1, buffer);
                else GL.uniform4fv(location, 1, buffer);
            }
            case TYPE_MAT4 -> {
                if (arb) GL.programUniformMatrix4fv(program.id(), location, 1, false, buffer);
                else GL.uniformMatrix4fv(location, 1, false, buffer);
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    @Override
    public void close() {
        MemoryUtil.free(buffer);
    }
}
