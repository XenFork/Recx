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
public final class GLStateManager {
    private static int currentProgram = 0;
    private static int textureBinding2D = 0;
    private static int vertexArrayBinding = 0;
    private static int arrayBufferBinding = 0;

    public static void useProgram(int program) {
        if (currentProgram != program) {
            currentProgram = program;
            GL.useProgram(program);
        }
    }

    public static int currentProgram() {
        return currentProgram;
    }

    public static void bindTexture2D(int texture) {
        if (textureBinding2D != texture) {
            textureBinding2D = texture;
            GL.bindTexture(GL.TEXTURE_2D, texture);
        }
    }

    public static int textureBinding2D() {
        return textureBinding2D;
    }

    public static void bindVertexArray(int array) {
        if (vertexArrayBinding != array) {
            vertexArrayBinding = array;
            GL.bindVertexArray(array);
        }
    }

    public static int vertexArrayBinding() {
        return vertexArrayBinding;
    }

    public static void bindArrayBuffer(int buffer) {
        if (arrayBufferBinding != buffer) {
            arrayBufferBinding = buffer;
            GL.bindBuffer(GL.ARRAY_BUFFER, buffer);
        }
    }

    public static int arrayBufferBinding() {
        return arrayBufferBinding;
    }
}
