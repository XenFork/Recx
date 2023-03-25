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

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3d;
import recx.world.entity.PlayerEntity;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class Camera {
    public final Vector3d position = new Vector3d();
    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f view = new Matrix4f();
    private final Matrix4f combined = new Matrix4f();
    private final Matrix4f inverse = new Matrix4f();

    public void moveToPlayer(PlayerEntity player, double partialTick) {
        player.prevPosition.lerp(player.position, partialTick, position);
        update();
    }

    public void update() {
        view.scaling(16f * 2f)
            .translate(
                (float) -position.x(),
                (float) -position.y(),
                (float) -position.z()
            );
        projection.mul(view, combined);
        combined.invert(inverse);
    }

    public void update(float width, float height) {
        projection.setOrthoSymmetric(width, height, -100f, 100f);
        update();
    }

    public Matrix4fc projection() {
        return projection;
    }

    public Matrix4fc view() {
        return view;
    }

    public Matrix4fc combined() {
        return combined;
    }

    public Matrix4fc inverse() {
        return inverse;
    }
}
