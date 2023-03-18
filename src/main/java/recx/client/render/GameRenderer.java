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

import recx.client.gl.GLProgram;
import recx.client.gl.VertexFormat;
import recx.util.Identifier;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class GameRenderer implements AutoCloseable {
    private GLProgram positionColorProgram;
    private GLProgram positionColorTexProgram;
    private final Camera camera = new Camera();

    public void init() {
        positionColorProgram = new GLProgram(Identifier.recx("core/position_color"), VertexFormat.POSITION_COLOR);
        positionColorTexProgram = new GLProgram(Identifier.recx("core/position_color_tex"), VertexFormat.POSITION_COLOR_TEX);
    }

    public GLProgram positionColor() {
        return positionColorProgram;
    }

    public GLProgram positionColorTex() {
        return positionColorTexProgram;
    }

    public Camera camera() {
        return camera;
    }

    @Override
    public void close() {
        positionColorProgram.close();
        positionColorTexProgram.close();
        Tessellator.free();
    }
}
