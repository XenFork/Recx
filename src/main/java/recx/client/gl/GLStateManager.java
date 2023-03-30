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
import org.overrun.glib.util.MemoryStack;

import java.util.Arrays;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class GLStateManager {
    private static int currentProgram = 0;
    private static int textureBinding2D = 0;
    private static int vertexArrayBinding = 0;
    private static int arrayBufferBinding = 0;
    private static final int[] viewport = {0, 0, -1, -1};
    private static boolean blend = false;
    private static int blendSrcRGB = GL.ONE;
    private static int blendSrcAlpha = GL.ONE;
    private static int blendDstRGB = GL.ZERO;
    private static int blendDstAlpha = GL.ZERO;

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

    public static void setViewport(int x, int y, int width, int height) {
        viewport();
        if (viewport[0] != x || viewport[1] != y || viewport[2] != width || viewport[3] != height) {
            viewport[0] = x;
            viewport[1] = y;
            viewport[2] = width;
            viewport[3] = height;
            GL.viewport(x, y, width, height);
        }
    }

    public static int[] viewport() {
        if (viewport[2] < 0 || viewport[3] < 0) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                GL.getIntegerv(stack, GL.VIEWPORT, viewport);
            }
        }
        return Arrays.copyOf(viewport, viewport.length);
    }

    public static void enableBlend() {
        if (!blend) {
            blend = true;
            GL.enable(GL.BLEND);
        }
    }

    public static void disableBlend() {
        if (blend) {
            blend = false;
            GL.disable(GL.BLEND);
        }
    }

    public static void blendFunc(int sfactor, int dfactor) {
        blendFuncSeparate(sfactor, dfactor, sfactor, dfactor);
    }

    public static void blendFuncSeparate(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha) {
        if (blendSrcRGB != sfactorRGB || blendSrcAlpha != sfactorAlpha || blendDstRGB != dfactorRGB || blendDstAlpha != dfactorAlpha) {
            blendSrcRGB = sfactorRGB;
            blendSrcAlpha = sfactorAlpha;
            blendDstRGB = dfactorRGB;
            blendDstAlpha = dfactorAlpha;
            GL.blendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
        }
    }

    public static boolean isBlendEnabled() {
        return blend;
    }

    public static int blendSrcRGB() {
        return blendSrcRGB;
    }

    public static int blendSrcAlpha() {
        return blendSrcAlpha;
    }

    public static int blendDstRGB() {
        return blendDstRGB;
    }

    public static int blendDstAlpha() {
        return blendDstAlpha;
    }
}
