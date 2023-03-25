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

import recx.client.RecxClient;
import recx.client.texture.TextureAtlas;
import recx.world.World;
import recx.world.block.Block;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class WorldRenderer {
    private final World world;

    public WorldRenderer(World world) {
        this.world = world;
    }

    public void render(double partialTick) {
        RenderSystem.bindTexture2D(TextureAtlas.BLOCK);
        RenderSystem.setProgram(RecxClient.get().gameRenderer().positionColorTex());
        final Tessellator t = Tessellator.getInstance();
        t.begin();
        for (int x = 0; x < world.width(); x++) {
            for (int y = 0; y < world.height(); y++) {
                for (int z = 0; z < world.depth(); z++) {
                    final Block block = world.getBlock(x, y, z);
                    if (!block.isAir()) {
                        block.render(t, partialTick, x, y, z);
                    }
                }
            }
        }
        t.end();
        RenderSystem.setProgram(null);
    }
}
