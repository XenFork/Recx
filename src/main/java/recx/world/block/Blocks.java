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

import recx.registry.BuiltinRegistries;
import recx.util.Identifier;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class Blocks {
    public static final Block AIR = register(0, "air", new AirBlock());
    public static final Block STONE = register(1, "stone", new Block());
    public static final Block GRASS_BLOCK = register(2, "grass_block", new Block());
    public static final Block DIRT = register(3, "dirt", new Block());
    public static final Block COBBLESTONE = register(4, "cobblestone", new Block());
    public static final Block BEDROCK = register(5, "bedrock", new Block());

    private static Block register(int rawId, String name, Block block) {
        return BuiltinRegistries.BLOCK.set(rawId, Identifier.recx(name), block);
    }
}
