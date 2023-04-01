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

import org.overrun.glib.glfw.GLFW;
import recx.client.Keyboard;
import recx.world.World;

/**
 * @author squid233
 * @since 0.1.0
 */
public class PlayerEntity extends Entity {
    public Keyboard keyboard;

    public PlayerEntity(World world) {
        super(world);
        boxWidth = 0.6;
        boxHeight = 1.8;
        boxDepth = 0.6;
        teleport(0.5, 5.0, 1.5);
    }

    @Override
    public void tick() {
        super.tick();

        final double speed = getSpeedFactor();
        double xo = 0.0;
        double zo = 0.0;
        if (keyboard.isKeyDown(GLFW.KEY_A)) {
            xo -= 1.0;
        }
        if (keyboard.isKeyDown(GLFW.KEY_D)) {
            xo += 1.0;
        }
        if (flying) {
            if (keyboard.isKeyDown(GLFW.KEY_LEFT_SHIFT)) {
                velocity.y = -0.2;
            }
            if (keyboard.isKeyDown(GLFW.KEY_SPACE)) {
                velocity.y = 0.2;
            }
        } else {
            if (onGround && keyboard.isKeyDown(GLFW.KEY_SPACE)) {
                velocity.y = 0.5;
            }
        }
        if (keyboard.isKeyDown(GLFW.KEY_W)) {
            zo -= 1.0;
        }
        if (keyboard.isKeyDown(GLFW.KEY_S)) {
            zo += 1.0;
        }
        accelerate(xo, zo, speed);
        if (!flying) {
            velocity.y -= 0.08;
        }
        move(velocity.x(), velocity.y(), velocity.z());
        velocity.mul(0.91, flying ? 0.8 : 0.98, 0.91);
        if (!flying && onGround) {
            final double factor = getInvFrictionFactor();
            velocity.x *= factor;
            velocity.z *= factor;
        }
    }
}
