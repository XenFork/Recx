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

package recx.world;

import recx.world.block.Block;
import recx.world.block.Blocks;
import recx.world.phys.AABBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class World {
    private final Block[] blocks;
    private final int width;
    private final int height;
    private final int depth;
    private final AABBox borderMinX;
    private final AABBox borderMaxX;
    private final AABBox borderMinZ;
    private final AABBox borderMaxZ;

    public World(int width, int height, int depth) {
        this.blocks = new Block[width * height * depth];
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.borderMinX = AABBox.ofPos(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
            0.0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        this.borderMaxX = AABBox.ofPos(width, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        this.borderMinZ = AABBox.ofPos(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0);
        this.borderMaxZ = AABBox.ofPos(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, depth,
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        Arrays.fill(blocks, Blocks.AIR);
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                setBlock(Blocks.BEDROCK, x, 0, z);
                setBlock(Blocks.STONE, x, 1, z);
                setBlock(Blocks.COBBLESTONE, x, 2, z);
                setBlock(Blocks.DIRT, x, 3, z);
                setBlock(Blocks.GRASS_BLOCK, x, 4, z);
            }
        }
    }

    public void setBlock(Block block, int x, int y, int z) {
        if (isInsideWorld(x, y, z)) {
            blocks[(y * depth + z) * width + x] = block;
        }
    }

    public Block getBlock(int x, int y, int z) {
        if (isInsideWorld(x, y, z)) {
            return blocks[(y * depth + z) * width + x];
        }
        return Blocks.AIR;
    }

    public boolean isInsideWorld(int x, int y, int z) {
        return x >= 0 && x < width &&
               y >= 0 && y < height &&
               z >= 0 && z < depth;
    }

    public List<AABBox> getCollisionsIn(AABBox box) {
        final int x0 = Math.max(0, (int) Math.floor(box.minX()));
        final int y0 = Math.max(0, (int) Math.floor(box.minY()));
        final int z0 = Math.max(0, (int) Math.floor(box.minZ()));
        final int x1 = Math.min(width, (int) Math.floor(box.maxX() + 1.0));
        final int y1 = Math.min(height, (int) Math.floor(box.maxY() + 1.0));
        final int z1 = Math.min(depth, (int) Math.floor(box.maxZ() + 1.0));
        List<AABBox> list = new ArrayList<>(16);
        list.add(borderMinX);
        list.add(borderMaxX);
        list.add(borderMinZ);
        list.add(borderMaxZ);
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    final AABBox collision = getBlock(x, y, z).getCollisionShape();
                    if (collision != Block.EMPTY) {
                        list.add(collision.move(x, y, z));
                    }
                }
            }
        }
        return list;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int depth() {
        return depth;
    }
}
