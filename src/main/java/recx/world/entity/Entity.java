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

import org.joml.Vector3d;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Entity {
    public final Vector3d prevPosition = new Vector3d();
    public final Vector3d position = new Vector3d();
    public boolean onGround;

    public double getSpeedFactor() {
        return 0.2;
    }

    public void tick() {
        prevPosition.set(position);
    }
}
