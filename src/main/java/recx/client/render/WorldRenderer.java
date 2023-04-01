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

import org.joml.Vector3f;
import recx.client.Mouse;
import recx.client.RecxClient;
import recx.client.gl.GLDrawMode;
import recx.client.gl.GLStateManager;
import recx.client.texture.TextureAtlas;
import recx.world.HitResult;
import recx.world.World;
import recx.world.block.Block;
import recx.world.block.Blocks;
import recx.world.phys.AABBox;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class WorldRenderer {
    private final RecxClient client;
    private final World world;
    private final Vector3f unproject = new Vector3f();
    private final HitResult hitResult = new HitResult();

    public WorldRenderer(RecxClient client, World world) {
        this.client = client;
        this.world = world;
    }

    public void render(double partialTick) {
        // render world
        RenderSystem.bindTexture2D(TextureAtlas.BLOCK);
        RenderSystem.setProgram(client.gameRenderer().positionColorTex());
        final Tessellator t = Tessellator.getInstance();
        t.begin();
        for (int x = 0; x < world.width(); x++) {
            for (int y = 0; y < world.height(); y++) {
                for (int z = 0; z < world.depth(); z++) {
                    final Block block = world.getBlock(x, y, z);
                    if (!block.isAir()) {
                        final boolean visible;
                        if (z != 0) {
                            visible = true;
                        } else {
                            final Block z1 = world.getBlock(x, y, 1);
                            visible = z1.isAir() || z1.isTextureTranslucent();
                        }
                        if (visible) {
                            BlockRenderer.render(block, t, partialTick, x, y, z);
                        }
                    }
                }
            }
        }
        t.end();
        RenderSystem.bindTexture2D(0);
        // render outline
        if (!hitResult.miss) {
            final AABBox outline = hitResult.block.getOutlineShape();
            if (outline != Block.EMPTY) {
                RenderSystem.setProgram(client.gameRenderer().positionColor());
                final int x = hitResult.x;
                final int y = hitResult.y;
                final float x0 = (float) (x - outline.minX());
                final float y0 = (float) (y - outline.minY());
                final float x1 = (float) (x + outline.maxX());
                final float y1 = (float) (y + outline.maxY());
                t.begin(GLDrawMode.LINE_LOOP);
                t.indices(0, 1, 2, 3).color(0x000000ff);
                t.vertex(x0, y1).emit();
                t.vertex(x0, y0).emit();
                t.vertex(x1, y0).emit();
                t.vertex(x1, y1).emit();
                t.end();
            }
        }
        RenderSystem.setProgram(null);
    }

    public void pick(Camera camera) {
        final Mouse mouse = client.mouse();
        camera.inverse().unprojectInv((float) mouse.cursorX(),
            client.height() - (float) mouse.cursorY(),
            0f,
            GLStateManager.viewport(),
            unproject);
        final int x = (int) Math.floor(unproject.x());
        final int y = (int) Math.floor(unproject.y());
        final int z = 1;
        final boolean miss = !world.isInsideWorld(x, y, z);
        hitResult.x = x;
        hitResult.y = y;
        hitResult.z = z;
        hitResult.miss = miss;
        hitResult.block = miss ? Blocks.AIR : world.getBlock(x, y, z);
    }

    public HitResult hitResult() {
        return hitResult;
    }
}
