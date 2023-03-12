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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.overrun.glib.gl.GL;
import recx.util.FileUtil;
import recx.util.Identifier;

import java.lang.foreign.Arena;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static recx.util.Identifier.*;
import static recx.util.JsonHelper.getFloat;
import static recx.util.JsonHelper.getString;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class GLProgram implements AutoCloseable {
    public static final GLProgram ZERO = new GLProgram(0, null);
    public static final String PROJECTION_VIEW_MATRIX = "ProjectionViewMatrix";
    public static final String MODEL_MATRIX = "ModelMatrix";
    public static final String COLOR_MODULATOR = "ColorModulator";
    private final int id;
    private final VertexFormat format;
    private final Map<String, GLUniform> uniforms = new HashMap<>();

    private GLProgram(int id, VertexFormat format) {
        this.id = id;
        this.format = format;
    }

    public GLProgram(Identifier identifier, VertexFormat format) {
        this(GL.createProgram(), format);
        try (Arena arena = Arena.openConfined()) {
            final JsonObject object = JsonParser.parseString(
                FileUtil.readString(identifier.toPath(ASSETS, SHADERS, JSON))
            ).getAsJsonObject();
            final Identifier vertex = of(getString(object, "vertex"));
            final Identifier fragment = of(getString(object, "fragment"));

            // compile shaders
            final int vsh = compileShader(arena,
                GL.VERTEX_SHADER, "vertex",
                FileUtil.readString(vertex.toPath(ASSETS, SHADERS, VERT_SHADER)));
            final int fsh = compileShader(arena,
                GL.FRAGMENT_SHADER, "fragment",
                FileUtil.readString(fragment.toPath(ASSETS, SHADERS, FRAG_SHADER)));

            GL.attachShader(id, vsh);
            GL.attachShader(id, fsh);
            GL.linkProgram(id);
            if (GL.getProgrami(id, GL.LINK_STATUS) == GL.FALSE) {
                throw new IllegalStateException("Failed to link the program " + identifier + ": " + GL.getProgramInfoLog(arena, id));
            }
            GL.detachShader(id, vsh);
            GL.detachShader(id, fsh);
            GL.deleteShader(vsh);
            GL.deleteShader(fsh);

            // load uniforms
            for (var e : object.getAsJsonObject("uniforms").entrySet()) {
                final String name = e.getKey();
                final JsonObject jsonUniform = e.getValue().getAsJsonObject();
                final int type = GLUniform.getTypeFromString(getString(jsonUniform, "type"));
                final JsonArray values = jsonUniform.getAsJsonArray("values");

                final GLUniform uniform = new GLUniform(type, GL.getUniformLocation(arena, id, name));
                switch (type) {
                    case GLUniform.TYPE_VEC4 -> uniform.set(
                        getFloat(values, 0),
                        getFloat(values, 1),
                        getFloat(values, 2),
                        getFloat(values, 3)
                    );
                    case GLUniform.TYPE_MAT4 -> {
                        float[] mat = new float[16];
                        for (int i = 0; i < mat.length; i++) {
                            mat[i] = getFloat(values, i);
                        }
                        uniform.set(mat);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                }
                uniforms.put(name, uniform);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load program " + identifier, e);
        }
    }

    private static int compileShader(Arena arena, int type, String typeName, String src) {
        final int shader = GL.createShader(type);
        GL.shaderSource(arena, shader, src);
        GL.compileShader(shader);
        if (GL.getShaderi(shader, GL.COMPILE_STATUS) == GL.FALSE) {
            throw new IllegalStateException("Failed to compile the " + typeName + " shader: " + GL.getShaderInfoLog(arena, shader));
        }
        return shader;
    }

    public void use() {
        GLStateManager.useProgram(id);
    }

    public Optional<GLUniform> getUniform(String name) {
        return Optional.ofNullable(uniforms.get(name));
    }

    public void uploadUniforms() {
        for (GLUniform uniform : uniforms.values()) {
            uniform.upload(this);
        }
    }

    public int id() {
        return id;
    }

    public VertexFormat format() {
        return format;
    }

    @Override
    public void close() {
        GL.deleteProgram(id);
        uniforms.values().forEach(GLUniform::close);
    }
}
