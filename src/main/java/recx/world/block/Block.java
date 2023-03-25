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

package recx.world.block;

import recx.client.render.RenderSystem;
import recx.client.render.Tessellator;
import recx.client.texture.TextureAtlas;
import recx.registry.BuiltinRegistries;
import recx.util.Identifier;

import java.util.Objects;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Block {
    private Identifier texture;

    public final Identifier getId() {
        return BuiltinRegistries.BLOCK.getId(this);
    }

    public boolean isAir() {
        return false;
    }

    @Deprecated(since = "0.1.0")
    public void render(Tessellator t, double partialTick, int x, int y, int z) {
        if (texture == null) {
            final Identifier id = getId();
            texture = new Identifier(id.namespace(), "block/" + (this == Blocks.GRASS_BLOCK ? id.path() + "_side" : id.path()));
        }
        final TextureAtlas atlas = Objects.requireNonNull(RenderSystem.getTexture2D(TextureAtlas.BLOCK));
        final float u0 = atlas.normalizeByWidth(atlas.getU0(texture));
        final float u1 = atlas.normalizeByWidth(atlas.getU1(texture));
        final float v0 = atlas.normalizeByHeight(atlas.getV0(texture));
        final float v1 = atlas.normalizeByHeight(atlas.getV1(texture));
        t.indices(0, 1, 2, 2, 3, 0).color(0xffffffff);
        t.vertex(x, y + 1, z).texCoords(u0, v0).emit();
        t.vertex(x, y, z).texCoords(u0, v1).emit();
        t.vertex(x + 1, y, z).texCoords(u1, v1).emit();
        t.vertex(x + 1, y + 1, z).texCoords(u1, v0).emit();
    }
}
