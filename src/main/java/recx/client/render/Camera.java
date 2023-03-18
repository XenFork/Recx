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

import org.joml.Vector3d;
import recx.world.entity.PlayerEntity;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class Camera {
    public final Vector3d position = new Vector3d();

    public void moveToPlayer(PlayerEntity player, double partialTick) {
        position.set(player.prevPosition).lerp(player.position, partialTick);
    }
}