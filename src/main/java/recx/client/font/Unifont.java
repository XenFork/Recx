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

import org.overrun.unifont.UnifontUtil;
import recx.client.texture.NativeImage;
import recx.client.texture.Texture2D;
import recx.util.Identifier;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class Unifont extends Font {
    private static Unifont instance;

    private Unifont(Identifier identifier, Texture2D texture) {
        super(identifier, texture);
    }

    public static Unifont getInstance() {
        if (instance == null) {
            final Texture2D texture = new Texture2D();
            try (NativeImage image = NativeImage.load(
                UNIFONT.toAssetPath("fonts", Identifier.PNG),
                NativeImage.Param.grey()
            )) {
                texture.load(image);
            }
            instance = new Unifont(UNIFONT, texture);
        }
        return instance;
    }

    @Override
    public int xAdvance(int codePoint) {
        return UnifontUtil.xAdvance(codePoint);
    }

    @Override
    public int yAdvance() {
        return UnifontUtil.yAdvance();
    }

    @Override
    public int xOffset(int codePoint) {
        return UnifontUtil.xOffset(codePoint);
    }

    @Override
    public int yOffset(int codePoint) {
        return UnifontUtil.yOffset(codePoint);
    }

    @Override
    public float uv(int offset) {
        return UnifontUtil.uv(offset);
    }
}
