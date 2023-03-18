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

package recx.client.texture;

import org.overrun.glib.RuntimeHelper;
import org.overrun.glib.gl.GL;
import recx.client.render.RenderSystem;
import recx.util.Identifier;

import java.io.IOException;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Texture2D implements Texture {
    protected final int textureId;
    protected int width, height;

    public Texture2D() {
        textureId = GL.genTexture();
    }

    public Texture2D(String filename) {
        this();
        load(filename);
    }

    public Texture2D(Identifier id) {
        this(id.toPath(Identifier.ASSETS, Identifier.TEXTURES, Identifier.PNG));
    }

    protected void setup() {
        final boolean isTexture = GL.isTexture(id());
        bind();
        if (!isTexture) {
            GL.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.NEAREST);
            GL.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.NEAREST);
            GL.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAX_LEVEL, 0);
        }
    }

    public void load(String filename) {
        setup();
        try (NativeImage image = NativeImage.load(filename)) {
            width = image.width();
            height = image.height();
            GL.texImage2D(GL.TEXTURE_2D,
                0,
                GL.RGBA,
                width,
                height,
                0,
                GL.RGBA,
                GL.UNSIGNED_BYTE,
                image.data());
        } catch (IOException e) {
            // todo: replace with log
            RuntimeHelper.apiLog("Failed to load texture " + filename);
            e.printStackTrace();
        }
        RenderSystem.bindTexture2D(0);
    }

    @Override
    public void bind() {
        RenderSystem.bindTexture2D(this);
    }

    @Override
    public int id() {
        return textureId;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    @Override
    public void close() {
        GL.deleteTexture(textureId);
    }
}
