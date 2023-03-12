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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import recx.client.gl.GLProgram;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class RenderSystem {
    private static final Matrix4fStack projectionMatrix = new Matrix4fStack(4);
    private static final Matrix4fStack viewMatrix = new Matrix4fStack(32);
    private static final Matrix4fStack modelMatrix = new Matrix4fStack(32);
    private static final Matrix4f combinedMatrix = new Matrix4f();
    private static final Vector4f colorModulator = new Vector4f(1.0f);
    private static @NotNull GLProgram currentProgram = GLProgram.ZERO;

    public static void setProjectionMatrix(Matrix4fc mat) {
        projectionMatrix.set(mat);
        updateCombined();
    }

    public static void setViewMatrix(Matrix4fc mat) {
        viewMatrix.set(mat);
        updateCombined();
    }

    public static void setProjectionViewMatrix(Matrix4fc projectionMat, Matrix4fc viewMat) {
        projectionMatrix.set(projectionMat);
        viewMatrix.set(viewMat);
        updateCombined();
    }

    public static void setModelMatrix(Matrix4fc mat) {
        modelMatrix.set(mat);
    }

    private static void updateCombined() {
        combinedMatrix.set(projectionMatrix).mul(viewMatrix);
    }

    public static Matrix4fStack projectionMatrix() {
        return projectionMatrix;
    }

    public static Matrix4fStack viewMatrix() {
        return viewMatrix;
    }

    public static Matrix4f combinedMatrix() {
        return combinedMatrix;
    }

    public static Matrix4fStack modelMatrix() {
        return modelMatrix;
    }

    public static void setColorModulator(float r, float g, float b, float a) {
        colorModulator.set(r, g, b, a);
    }

    public static Vector4fc colorModulator() {
        return colorModulator;
    }

    public static void setProgram(@Nullable GLProgram program) {
        currentProgram = program != null ? program : GLProgram.ZERO;
        currentProgram.use();
        if (currentProgram.id() != 0) {
            currentProgram.getUniform(GLProgram.PROJECTION_VIEW_MATRIX).ifPresent(u -> u.set(combinedMatrix));
            currentProgram.getUniform(GLProgram.MODEL_MATRIX).ifPresent(u -> u.set(modelMatrix));
            currentProgram.getUniform(GLProgram.COLOR_MODULATOR).ifPresent(u -> u.set(colorModulator));
            currentProgram.uploadUniforms();
        }
    }

    public static @NotNull GLProgram currentProgram() {
        return currentProgram;
    }
}
