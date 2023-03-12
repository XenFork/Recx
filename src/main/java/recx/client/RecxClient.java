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

import org.overrun.glib.RuntimeHelper;
import org.overrun.glib.gl.GL;
import org.overrun.glib.gl.GLLoader;
import org.overrun.glib.glfw.Callbacks;
import org.overrun.glib.glfw.GLFW;
import org.overrun.glib.glfw.GLFWVidMode;
import org.overrun.glib.util.MemoryStack;
import org.overrun.glib.util.value.Value2;
import org.overrun.timer.Timer;
import recx.client.render.GameRenderer;
import recx.client.render.RenderSystem;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class RecxClient implements Runnable, AutoCloseable {
    private static final RecxClient INSTANCE = new RecxClient();
    private MemorySegment window;
    private Timer timer;
    private GameRenderer gameRenderer;

    private void init() {
        if (!GLFW.init()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }
        GLFW.windowHint(GLFW.VISIBLE, false);
        GLFW.windowHint(GLFW.CONTEXT_VERSION_MAJOR, 3);
        GLFW.windowHint(GLFW.CONTEXT_VERSION_MINOR, 2);
        GLFW.windowHint(GLFW.OPENGL_PROFILE, GLFW.OPENGL_CORE_PROFILE);
        GLFW.windowHint(GLFW.OPENGL_FORWARD_COMPAT, true);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            window = GLFW.createWindow(stack, 864, 480, "Recx", MemorySegment.NULL, MemorySegment.NULL);
        }
        if (window.address() == RuntimeHelper.NULL) {
            throw new IllegalStateException("Failed to create the window");
        }

        // callbacks
        GLFW.setFramebufferSizeCallback(window, (window1, width, height) -> resize(width, height));

        // align to center
        final GLFWVidMode.Value videoMode = GLFW.getVideoMode(SegmentScope.auto(), GLFW.getPrimaryMonitor());
        if (videoMode != null) {
            final Value2.OfInt windowSize = GLFW.getWindowSize(window);
            GLFW.setWindowPos(window,
                (videoMode.width() - windowSize.x()) / 2,
                (videoMode.height() - windowSize.y()) / 2);
        }

        GLFW.makeContextCurrent(window);
        GLLoader.loadConfined(true, GLFW::getProcAddress);

        // init GL
        GL.clearColor(.4f, .6f, .9f, 1f);
        gameRenderer = new GameRenderer();
        gameRenderer.init();
        vao = GL.genVertexArray();
        vbo = GL.genBuffer();
        GL.bindVertexArray(vao);
        GL.bindBuffer(GL.ARRAY_BUFFER, vbo);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL.bufferData(stack, GL.ARRAY_BUFFER, new int[]{
                Float.floatToRawIntBits(0), Float.floatToRawIntBits(100), Float.floatToRawIntBits(0), Integer.reverseBytes(0xff0000ff),
                Float.floatToRawIntBits(0), Float.floatToRawIntBits(0), Float.floatToRawIntBits(0), Integer.reverseBytes(0x00ff00ff),
                Float.floatToRawIntBits(100), Float.floatToRawIntBits(0), Float.floatToRawIntBits(0), Integer.reverseBytes(0x0000ffff),
            }, GL.STATIC_DRAW);
        }
        gameRenderer.positionColor().format().enableAttributes();
        GL.bindBuffer(GL.ARRAY_BUFFER, 0);
        GL.bindVertexArray(0);

        final Value2.OfInt framebufferSize = GLFW.getFramebufferSize(window);
        resize(framebufferSize.x(), framebufferSize.y());

        GLFW.showWindow(window);

        timer = Timer.ofGetter(20.0, GLFW::getTime);
    }

    private void tick() {
    }

    private void update() {
    }

    private void resize(int width, int height) {
        GL.viewport(0, 0, width, height);
        RenderSystem.setProjectionMatrix(RenderSystem.projectionMatrix().setOrtho2D(0, width, 0, height));
    }

    int vao = 0, vbo = 0;

    private void render(double partialTick) {
        GL.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);
        RenderSystem.setProgram(gameRenderer.positionColor());
        GL.bindVertexArray(vao);
        GL.drawArrays(GL.TRIANGLES, 0, 3);
        GL.bindVertexArray(0);
        RenderSystem.setProgram(null);

        GLFW.swapBuffers(window);
    }

    @Override
    public void run() {
        try {
            init();
            while (!GLFW.windowShouldClose(window)) {
                // tick -> update -> callbacks -> render
                timer.advanceTime();
                timer.performTicks(this::tick);
                update();
                GLFW.pollEvents();
                render(timer.partialTick());
                timer.calcFPS();
            }
        } finally {
            close();
        }
    }

    @Override
    public void close() {
        gameRenderer.close();

        Callbacks.free(window);
        GLFW.destroyWindow(window);
        GLFW.terminate();
    }

    public static RecxClient get() {
        return INSTANCE;
    }
}
