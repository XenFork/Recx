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
public final class Keyboard {
    private final MemorySegment window;

    public Keyboard(MemorySegment window) {
        this.window = window;
    }

    public boolean isKeyDown(@MagicConstant(valuesFromClass = GLFW.class) int key) {
        return GLFW.getKey(window, key) == GLFW.PRESS;
    }

    public boolean isKeyUp(@MagicConstant(valuesFromClass = GLFW.class) int key) {
        return GLFW.getKey(window, key) == GLFW.RELEASE;
    }
}
