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
import recx.world.phys.AABBox;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Block {
    public static final AABBox FULL_CUBE = AABBox.ofPos(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    public static final AABBox EMPTY = null;
    @Deprecated(since = "0.1.0")
    public Identifier texture;

    public final Identifier getId() {
        return BuiltinRegistries.BLOCK.getId(this);
    }

    public AABBox getOutlineShape() {
        return FULL_CUBE;
    }

    public AABBox getCollisionShape() {
        return FULL_CUBE;
    }

    public boolean isAir() {
        return false;
    }

    public boolean canBeReplaced() {
        return false;
    }

    @Deprecated(since = "0.1.0")
    public boolean isTextureTranslucent() {
        return isAir();
    }
}
