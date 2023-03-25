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

import java.util.Arrays;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class World {
    private final Block[] blocks;
    private final int width;
    private final int height;
    private final int depth;

    public World(int width, int height, int depth) {
        this.blocks = new Block[width * height * depth];
        this.width = width;
        this.height = height;
        this.depth = depth;
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
        blocks[(y * depth + z) * width + x] = block;
    }

    public Block getBlock(int x, int y, int z) {
        return blocks[(y * depth + z) * width + x];
    }

    public boolean isInsideWorld(int x, int y, int z) {
        return x >= 0 && x < width &&
               y >= 0 && y < height &&
               z >= 0 && z < depth;
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
