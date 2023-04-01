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

package recx.world.entity;

import org.joml.Math;
import org.joml.Vector3d;
import recx.world.World;
import recx.world.phys.AABBox;

import java.util.List;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Entity {
    public final Vector3d prevPosition = new Vector3d();
    public final Vector3d position = new Vector3d();
    public final Vector3d velocity = new Vector3d();
    public final World world;
    public boolean onGround = false;
    public boolean flying = false;
    protected double boxWidth = 0.6;
    protected double boxHeight = 1.8;
    protected double boxDepth = 0.6;
    public AABBox box;

    public Entity(World world) {
        this.world = world;
    }

    public double getSpeedFactor() {
        return flying ? 0.07 : (onGround ? 0.1 : 0.02);
    }

    public double getInvFrictionFactor() {
        return 0.7;
    }

    public void accelerate(double x, double z, double speed) {
        final double magSqr = x * x + z * z;
        if (magSqr >= 0.01) {
            // normalize
            final double n = speed / Math.sqrt(magSqr);
            velocity.x += x * n;
            velocity.z += z * n;
        }
    }

    protected double setPosition(double x, double y, double z) {
        final double finalZ = Math.clamp(0.0, 2.0, z);
        position.set(x, y, finalZ);
        return finalZ;
    }

    public void teleport(double x, double y, double z) {
        final double clampedZ = setPosition(x, y, z);
        final double halfWidth = boxWidth * 0.5;
        final double halfDepth = boxDepth * 0.5;
        box = AABBox.ofPos(x - halfWidth, y, clampedZ - halfDepth,
            x + halfWidth, y + boxHeight, clampedZ + halfDepth);
    }

    public void move(double x, double y, double z) {
        // copy movement
        double moveX = x;
        double moveY = y;
        double moveZ = z;

        // move and clip
        final List<AABBox> collisions = world.getCollisionsIn(box.expand(x, y, z));

        for (AABBox collision : collisions) {
            moveY = collision.clipYCollide(box, moveY);
        }
        box = box.move(0.0, moveY, 0.0);

        for (AABBox collision : collisions) {
            moveX = collision.clipXCollide(box, moveX);
        }
        box = box.move(moveX, 0.0, 0.0);

        for (AABBox collision : collisions) {
            moveZ = collision.clipZCollide(box, moveZ);
        }
        box = box.move(0.0, 0.0, moveZ);

        onGround = y != moveY && y < 0.0;
        // if movement is clipped
        if (x != moveX) {
            velocity.x = 0.0;
        }
        if (y != moveY) {
            velocity.y = 0.0;
        }
        if (z != moveZ) {
            velocity.z = 0.0;
        }

        setPosition(
            (box.minX() + box.maxX()) * 0.5,
            box.minY(),
            (box.minZ() + box.maxZ()) * 0.5
        );
    }

    public void tick() {
        prevPosition.set(position);
    }
}
