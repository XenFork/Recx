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

package recx.client.font;

import recx.client.render.Batch;
import recx.client.render.RenderSystem;
import recx.client.texture.Texture2D;
import recx.util.Identifier;

/**
 * @author squid233
 * @since 0.1.0
 */
public abstract class Font {
    public static final Identifier UNIFONT = new Identifier("_overrun", "unifont");
    private final Identifier identifier;
    private final Texture2D texture;

    protected Font(Identifier identifier, Texture2D texture) {
        this.identifier = identifier;
        this.texture = texture;
        RenderSystem.putTexture2D(identifier, texture);
    }

    public static Font unifont() {
        return Unifont.getInstance();
    }

    public void drawText(Batch batch, float x, float y, String text) {
        float x0 = x;
        for (int i = 0, c = text.codePointCount(0, text.length()); i < c; i++) {
            final int cp = text.codePointAt(i);
            final int w = xAdvance(cp);
            final int h = yAdvance();
            final int xo = xOffset(cp);
            final int yo = yOffset(cp);
            final float u0 = uv(xo);
            final float v0 = uv(yo);
            final float u1 = uv(xo + w);
            final float v1 = uv(yo + h);
            final float x1 = x0 + w;
            final float y1 = y + h;
            batch.indices(0, 1, 2, 2, 3, 0);
            batch.texCoords(u0, v0).vertex(x0, y1).emit();
            batch.texCoords(u0, v1).vertex(x0, y).emit();
            batch.texCoords(u1, v1).vertex(x1, y).emit();
            batch.texCoords(u1, v0).vertex(x1, y1).emit();
            x0 += w;
        }
    }

    public abstract int xAdvance(int codePoint);

    public abstract int yAdvance();

    public abstract int xOffset(int codePoint);

    public abstract int yOffset(int codePoint);

    public abstract float uv(int offset);

    public Identifier identifier() {
        return identifier;
    }

    public Texture2D texture() {
        return texture;
    }
}
