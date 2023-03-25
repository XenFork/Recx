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

package recx.client;

import org.intellij.lang.annotations.MagicConstant;
import org.overrun.glib.glfw.GLFW;

import java.lang.foreign.MemorySegment;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class Mouse {
    private final MemorySegment window;
    private double cursorX, cursorY;

    public Mouse(MemorySegment window) {
        this.window = window;
    }

    public boolean isButtonDown(@MagicConstant(valuesFromClass = GLFW.class) int button) {
        return GLFW.getMouseButton(window, button) == GLFW.PRESS;
    }

    public boolean isButtonUp(@MagicConstant(valuesFromClass = GLFW.class) int button) {
        return GLFW.getMouseButton(window, button) == GLFW.RELEASE;
    }

    public void updateCursor(double x, double y) {
        cursorX = x;
        cursorY = y;
    }

    public double cursorX() {
        return cursorX;
    }

    public double cursorY() {
        return cursorY;
    }
}
