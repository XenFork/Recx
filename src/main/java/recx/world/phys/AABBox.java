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

package recx.world.phys;

import recx.util.ValueObject;

/**
 * The axis-aligned bounding box.
 *
 * @author squid233
 * @since 0.1.0
 */
@ValueObject
public final class AABBox {
    private final double minX, minY, minZ;
    private final double maxX, maxY, maxZ;

    private AABBox(double minX, double minY, double minZ,
                   double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public static AABBox ofPos(double minX, double minY, double minZ,
                               double maxX, double maxY, double maxZ) {
        return new AABBox(Math.min(minX, maxX), Math.min(minY, maxY), Math.min(minZ, maxZ),
            Math.max(maxX, minX), Math.max(maxY, minY), Math.max(maxZ, minZ));
    }

    public static AABBox ofSize(double x, double y, double z,
                                double sizeX, double sizeY, double sizeZ) {
        return ofPos(x, y, z, x + sizeX, y + sizeY, z + sizeZ);
    }

    public static AABBox ofExtents(double x, double y, double z,
                                   double extentX, double extentY, double extentZ) {
        return ofPos(x - extentX, y - extentY, z - extentZ,
            x + extentX, y + extentY, z + extentZ);
    }

    public AABBox grow(double x, double y, double z) {
        return ofPos(minX - x, minY - y, minZ - z,
            maxX + x, maxY + y, maxZ + z);
    }

    public AABBox expand(double x, double y, double z) {
        double nMinX = minX;
        double nMinY = minY;
        double nMinZ = minZ;
        double nMaxX = maxX;
        double nMaxY = maxY;
        double nMaxZ = maxZ;
        if (x < 0.0) nMinX += x;
        else if (x > 0.0) nMaxX += x;
        if (y < 0.0) nMinY += y;
        else if (y > 0.0) nMaxY += y;
        if (z < 0.0) nMinZ += z;
        else if (z > 0.0) nMaxZ += z;
        return ofPos(nMinX, nMinY, nMinZ, nMaxX, nMaxY, nMaxZ);
    }

    public AABBox move(double x, double y, double z) {
        return ofPos(minX + x, minY + y, minZ + z,
            maxX + x, maxY + y, maxZ + z);
    }

    /**
     * Clips the movement x of <i>{@code other}</i> box.
     *
     * @param other    the box to be clipped.
     * @param movement the movement x of <i>{@code other}</i>.
     * @return the clipped movement x.
     */
    public double clipXCollide(AABBox other, double movement) {
        // check whether maybe intersects
        if (minY < other.maxY && maxY > other.minY && minZ < other.maxZ && maxZ > other.minZ) {
            if (movement > 0.0 && minX >= other.maxX) return Math.min(movement, minX - other.maxX);
            if (movement < 0.0 && maxX <= other.minX) return Math.max(movement, maxX - other.minX);
        }
        return movement;
    }

    /**
     * Clips the movement y of <i>{@code other}</i> box.
     *
     * @param other    the box to be clipped.
     * @param movement the movement y of <i>{@code other}</i>.
     * @return the clipped movement y.
     */
    public double clipYCollide(AABBox other, double movement) {
        // check whether maybe intersects
        if (minX < other.maxX && maxX > other.minX && minZ < other.maxZ && maxZ > other.minZ) {
            if (movement > 0.0 && minY >= other.maxY) return Math.min(movement, minY - other.maxY);
            if (movement < 0.0 && maxY <= other.minY) return Math.max(movement, maxY - other.minY);
        }
        return movement;
    }

    /**
     * Clips the movement z of <i>{@code other}</i> box.
     *
     * @param other    the box to be clipped.
     * @param movement the movement z of <i>{@code other}</i>.
     * @return the clipped movement z.
     */
    public double clipZCollide(AABBox other, double movement) {
        // check whether maybe intersects
        if (minX < other.maxX && maxX > other.minX && minY < other.maxY && maxY > other.minY) {
            if (movement > 0.0 && minZ >= other.maxZ) return Math.min(movement, minZ - other.maxZ);
            if (movement < 0.0 && maxZ <= other.minZ) return Math.max(movement, maxZ - other.minZ);
        }
        return movement;
    }

    public double minX() {
        return minX;
    }

    public double minY() {
        return minY;
    }

    public double minZ() {
        return minZ;
    }

    public double maxX() {
        return maxX;
    }

    public double maxY() {
        return maxY;
    }

    public double maxZ() {
        return maxZ;
    }
}
