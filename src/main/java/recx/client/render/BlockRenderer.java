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

import recx.client.texture.TextureAtlas;
import recx.util.Identifier;
import recx.world.block.Block;
import recx.world.block.Blocks;

import java.util.Objects;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class BlockRenderer {
    @Deprecated(since = "0.1.0")
    public static void render(Block block, Batch batch, double partialTick, int x, int y, int z) {
        if (block.texture == null) {
            final Identifier id = block.getId();
            block.texture = new Identifier(id.namespace(), "block/" + (block == Blocks.GRASS_BLOCK ? id.path() + "_side" : id.path()));
        }
        final TextureAtlas atlas = Objects.requireNonNull(RenderSystem.getTexture2D(TextureAtlas.BLOCK));
        final float u0 = atlas.normalizeByWidth(atlas.getU0(block.texture));
        final float u1 = atlas.normalizeByWidth(atlas.getU1(block.texture));
        final float v0 = atlas.normalizeByHeight(atlas.getV0(block.texture));
        final float v1 = atlas.normalizeByHeight(atlas.getV1(block.texture));
        batch.indices(0, 1, 2, 2, 3, 0).color(z == 1 ? 0xffffffff : 0x808080ff);
        batch.texCoords(u0, v0).vertex(x, y + 1, z).emit();
        batch.texCoords(u0, v1).vertex(x, y, z).emit();
        batch.texCoords(u1, v1).vertex(x + 1, y, z).emit();
        batch.texCoords(u1, v0).vertex(x + 1, y + 1, z).emit();
    }
}
