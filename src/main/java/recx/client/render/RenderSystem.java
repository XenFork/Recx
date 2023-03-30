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
import recx.client.gl.GLStateManager;
import recx.client.gl.GLUniform;
import recx.client.texture.Texture2D;
import recx.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

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
    private static final Map<Identifier, Texture2D> textures = new HashMap<>();

    private static void updateUniform(String name, Consumer<GLUniform> action) {
        if (currentProgram.id() != 0) {
            currentProgram.getUniform(name).ifPresent(action);
        }
    }

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
        updateUniform(GLProgram.MODEL_MATRIX, u -> u.set(modelMatrix));
    }

    private static void updateCombined() {
        projectionMatrix.mul(viewMatrix, combinedMatrix);
        updateUniform(GLProgram.PROJECTION_VIEW_MATRIX, u -> u.set(combinedMatrix));
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
        updateUniform(GLProgram.COLOR_MODULATOR, u -> u.set(colorModulator));
    }

    public static Vector4fc colorModulator() {
        return colorModulator;
    }

    public static void setProgram(@Nullable GLProgram program) {
        currentProgram = program != null ? program : GLProgram.ZERO;
        currentProgram.use();
        if (currentProgram.id() != 0) {
            updateUniform(GLProgram.PROJECTION_VIEW_MATRIX, u -> u.set(combinedMatrix));
            updateUniform(GLProgram.MODEL_MATRIX, u -> u.set(modelMatrix));
            updateUniform(GLProgram.COLOR_MODULATOR, u -> u.set(colorModulator));
            currentProgram.uploadUniforms();
        }
    }

    public static @NotNull GLProgram currentProgram() {
        return currentProgram;
    }

    public static Texture2D putTexture2D(Identifier id, Texture2D texture) {
        return textures.put(id, texture);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Texture2D> T getTexture2D(Identifier id) {
        final Texture2D texture = textures.get(id);
        return texture != null ? (T) texture : null;
    }

    public static void bindTexture2D(int texture) {
        GLStateManager.bindTexture2D(texture);
    }

    public static void bindTexture2D(Texture2D texture) {
        bindTexture2D(texture.id());
    }

    public static void bindTexture2D(Identifier id) {
        bindTexture2D(id, Texture2D::new);
    }

    public static void bindTexture2D(Identifier id, Function<Identifier, Texture2D> mappingFunction) {
        bindTexture2D(textures.computeIfAbsent(id, mappingFunction));
    }

    public static void deleteTextures() {
        textures.values().forEach(Texture2D::close);
        textures.clear();
    }
}
