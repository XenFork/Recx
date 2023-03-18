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

import org.joml.Vector3d;
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
import recx.client.render.Tessellator;
import recx.util.Identifier;
import recx.world.entity.PlayerEntity;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class RecxClient implements Runnable, AutoCloseable {
    private static final RecxClient INSTANCE = new RecxClient();
    private MemorySegment window;
    private Keyboard keyboard;
    private int width;
    private int height;
    private Timer timer;
    private GameRenderer gameRenderer;
    private PlayerEntity player;

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

        keyboard = new Keyboard(window);

        // init GL
        GL.clearColor(.4f, .6f, .9f, 1f);
        gameRenderer = new GameRenderer();
        gameRenderer.init();
        player = new PlayerEntity();
        player.keyboard = keyboard;

        final Value2.OfInt framebufferSize = GLFW.getFramebufferSize(window);
        resize(framebufferSize.x(), framebufferSize.y());

        GLFW.showWindow(window);

        timer = Timer.ofGetter(20.0, GLFW::getTime);
    }

    private void tick() {
        player.tick();
    }

    private void update() {
    }

    private void resize(int width, int height) {
        GL.viewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    private void renderWorld(double partialTick) {
        gameRenderer.camera().moveToPlayer(player, partialTick);
        final Vector3d position = gameRenderer.camera().position;
        // update projection view matrix
        RenderSystem.setProjectionMatrix(RenderSystem.projectionMatrix().setOrthoSymmetric(width, height, -10f, 10f));
        RenderSystem.setViewMatrix(RenderSystem.viewMatrix()
            .scaling(16f * 2f)
            .translate(
                (float) -position.x(),
                (float) -position.y(),
                (float) -position.z()
            ));

        GL.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);

        RenderSystem.bindTexture2D(Identifier.recx("block/dirt"));
        RenderSystem.setProgram(gameRenderer.positionColorTex());
        final Tessellator t = Tessellator.getInstance();
        t.begin();
        t.indices(0, 1, 2, 2, 3, 0);
        t.vertex(0, 4, 0).color(0xff0000ff).texCoords(0f, 0f).emit();
        t.vertex(0, 0, 0).color(0x00ff00ff).texCoords(0f, 1f).emit();
        t.vertex(4, 0, 0).color(0x0000ffff).texCoords(1f, 1f).emit();
        t.vertex(4, 4, 0).color(0xffffffff).texCoords(1f, 0f).emit();
        t.end();
        RenderSystem.setProgram(null);
    }

    private void render(double partialTick) {
        renderWorld(partialTick);

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

    public MemorySegment window() {
        return window;
    }

    public Keyboard keyboard() {
        return keyboard;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public Timer timer() {
        return timer;
    }

    public GameRenderer gameRenderer() {
        return gameRenderer;
    }

    public static RecxClient get() {
        return INSTANCE;
    }
}
