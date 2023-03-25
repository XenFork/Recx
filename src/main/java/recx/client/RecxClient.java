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
import recx.client.gl.GLStateManager;
import recx.client.render.Camera;
import recx.client.render.GameRenderer;
import recx.client.render.RenderSystem;
import recx.client.render.WorldRenderer;
import recx.client.texture.NativeImage;
import recx.client.texture.TextureAtlas;
import recx.util.Identifier;
import recx.world.HitResult;
import recx.world.World;
import recx.world.block.Blocks;
import recx.world.entity.PlayerEntity;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.HashMap;
import java.util.Map;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class RecxClient implements Runnable, AutoCloseable {
    private static final RecxClient INSTANCE = new RecxClient();
    private MemorySegment window;
    private Keyboard keyboard;
    private Mouse mouse;
    private int width;
    private int height;
    private Timer timer;
    private GameRenderer gameRenderer;
    private World world;
    private WorldRenderer worldRenderer;
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
        GLFW.setCursorPosCallback(window, (window1, xpos, ypos) -> onCursorPos(xpos, ypos));

        // align to center
        try (Arena arena = Arena.openConfined()) {
            final GLFWVidMode.Value videoMode = GLFW.getVideoMode(arena, GLFW.getPrimaryMonitor());
            if (videoMode != null) {
                final Value2.OfInt windowSize = GLFW.getWindowSize(window);
                GLFW.setWindowPos(window,
                    (videoMode.width() - windowSize.x()) / 2,
                    (videoMode.height() - windowSize.y()) / 2);
            }
        }

        GLFW.makeContextCurrent(window);
        GLLoader.loadConfined(true, GLFW::getProcAddress);

        keyboard = new Keyboard(window);
        mouse = new Mouse(window);

        // init GL
        GL.clearColor(.4f, .6f, .9f, 1f);
        gameRenderer = new GameRenderer();
        gameRenderer.init();
        world = new World(256, 256, 2);
        worldRenderer = new WorldRenderer(this, world);
        player = new PlayerEntity();
        player.keyboard = keyboard;

        // block atlas
        final TextureAtlas atlas = new TextureAtlas();
        atlas.pack(images(
            Identifier.recx("block/bedrock"),
            Identifier.recx("block/cobblestone"),
            Identifier.recx("block/dirt"),
            Identifier.recx("block/grass_block_side"),
            Identifier.recx("block/stone")
        ));
        RenderSystem.putTexture2D(TextureAtlas.BLOCK, atlas);

        final Value2.OfInt framebufferSize = GLFW.getFramebufferSize(window);
        resize(framebufferSize.x(), framebufferSize.y());

        GLFW.showWindow(window);

        timer = Timer.ofGetter(20.0, GLFW::getTime);
    }

    private static Map<Identifier, NativeImage> images(Identifier... ids) {
        final Map<Identifier, NativeImage> map = HashMap.newHashMap(ids.length);
        for (Identifier id : ids) {
            try {
                map.put(id, NativeImage.load(id.toTexturePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private void tick() {
        player.tick();
    }

    private void update() {
        final HitResult hitResult = worldRenderer.hitResult();
        if (hitResult != null && !hitResult.miss) {
            final int x = hitResult.x;
            final int y = hitResult.y;
            final int z = hitResult.z;
            if (mouse.isButtonDown(GLFW.MOUSE_BUTTON_LEFT)) {
                world.setBlock(Blocks.AIR, x, y, z);
            }
            if (mouse.isButtonDown(GLFW.MOUSE_BUTTON_RIGHT) && world.getBlock(x, y, z).canBeReplaced()) {
                world.setBlock(Blocks.STONE, x, y, z);
            }
        }
    }

    private void resize(int width, int height) {
        GLStateManager.setViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    private void onCursorPos(double cursorX, double cursorY) {
        mouse.updateCursor(cursorX, cursorY);
    }

    private void renderWorld(double partialTick) {
        // update camera
        final Camera camera = gameRenderer.camera();
        camera.moveToPlayer(player, partialTick);
        camera.update(width, height);
        // update projection view matrix
        RenderSystem.setProjectionViewMatrix(camera.projection(), camera.view());

        worldRenderer.pick(gameRenderer.camera());

        GL.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);
        worldRenderer.render(partialTick);
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

    public Mouse mouse() {
        return mouse;
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
