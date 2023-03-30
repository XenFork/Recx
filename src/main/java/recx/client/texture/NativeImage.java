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

package recx.client.texture;

import org.overrun.binpacking.PackerRegionSize;
import org.overrun.glib.RuntimeHelper;
import org.overrun.glib.stb.STBImage;
import org.overrun.glib.util.MemoryStack;
import org.overrun.glib.util.MemoryUtil;
import recx.util.Util;
import recx.util.ValueObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;
import java.util.Objects;

/**
 * @author squid233
 * @since 0.1.0
 */
@ValueObject
public final class NativeImage implements PackerRegionSize, AutoCloseable {
    private static final MemorySegment FAIL_DATA =
        Util.with(MemorySegment.allocateNative(ValueLayout.JAVA_INT.byteSize() * 16 * 16, SegmentScope.global()), seg -> {
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    seg.setAtIndex(ValueLayout.JAVA_INT, y * 16 + x, 0xfff800f8);
                }
                for (int x = 8; x < 16; x++) {
                    seg.setAtIndex(ValueLayout.JAVA_INT, y * 16 + x, 0xff000000);
                }
            }
            for (int y = 8; y < 16; y++) {
                for (int x = 0; x < 8; x++) {
                    seg.setAtIndex(ValueLayout.JAVA_INT, y * 16 + x, 0xff000000);
                }
                for (int x = 8; x < 16; x++) {
                    seg.setAtIndex(ValueLayout.JAVA_INT, y * 16 + x, 0xfff800f8);
                }
            }
        });
    private static final NativeImage FAIL_IMAGE = new NativeImage(16, 16, FAIL_DATA, true, Param.rgba());
    private final int width;
    private final int height;
    private final MemorySegment data;
    private final boolean failed;
    private final Param param;

    private NativeImage(int width, int height, MemorySegment data, boolean failed, Param param) {
        this.width = width;
        this.height = height;
        this.data = failed ? FAIL_DATA : data;
        this.failed = failed;
        this.param = param;
    }

    /**
     * @author squid233
     * @since 0.1.0
     */
    @ValueObject
    public static final class Param {
        private static final Param RGBA = new Param(STBImage.RGB_ALPHA);
        private static final Param GREY = new Param(STBImage.GREY);
        private final int channel;

        private Param(int channel) {
            this.channel = channel;
        }

        public static Param rgba() {
            return RGBA;
        }

        public static Param grey() {
            return GREY;
        }

        public int channel() {
            return channel;
        }
    }

    private static NativeImage load(InputStream stream, Param param) {
        final BufferedInputStream bis;
        if (stream instanceof BufferedInputStream) {
            bis = (BufferedInputStream) stream;
        } else {
            bis = new BufferedInputStream(stream);
        }

        MemorySegment segment = MemoryUtil.malloc(8192);
        try {
            try (bis) {
                long offset = 0;
                byte[] bytes = new byte[8192];
                int count;
                while ((count = bis.read(bytes)) > 0) {
                    if (offset + count >= segment.byteSize()) {
                        segment = MemoryUtil.realloc(segment, segment.byteSize() * 3 / 2);
                    }
                    MemorySegment.copy(bytes, 0, segment, ValueLayout.JAVA_BYTE, offset, count);
                    offset += count;
                }
                segment = segment.asSlice(0, offset);
            } catch (IOException e) {
                // todo: log
                e.printStackTrace();
                return FAIL_IMAGE;
            }

            int width, height;
            final MemorySegment data;

            try (MemoryStack stack = MemoryStack.stackPush()) {
                final MemorySegment pw = stack.calloc(ValueLayout.JAVA_INT);
                final MemorySegment ph = stack.calloc(ValueLayout.JAVA_INT);
                final MemorySegment pc = stack.calloc(ValueLayout.JAVA_INT);
                data = STBImage.loadFromMemory(segment, pw, ph, pc, param.channel());
                if (data.address() == RuntimeHelper.NULL) {
                    // todo: replace with logging library
                    RuntimeHelper.apiLog("Failed to load image: " + STBImage.failureReason());
                    return FAIL_IMAGE;
                }
                width = pw.get(ValueLayout.JAVA_INT, 0);
                height = ph.get(ValueLayout.JAVA_INT, 0);
            }

            return new NativeImage(width, height, data, false, param);
        } finally {
            MemoryUtil.free(segment);
        }
    }

    public static NativeImage load(ClassLoader loader, String filename, Param param) {
        try {
            return load(Objects.requireNonNull(loader.getResourceAsStream(filename),
                "Failed to get resource '" + filename + '\''), param);
        } catch (Exception e) {
            // todo: log
            System.err.println("Failed to load " + filename);
            e.printStackTrace();
            return FAIL_IMAGE;
        }
    }

    public static NativeImage load(Class<?> cls, String filename, Param param) {
        return load(cls.getClassLoader(), filename, param);
    }

    public static NativeImage load(String filename, Param param) {
        return load(Util.WALKER.getCallerClass(), filename, param);
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    public MemorySegment data() {
        return data;
    }

    public boolean failed() {
        return failed;
    }

    public Param param() {
        return param;
    }

    @Override
    public void close() {
        if (!failed) STBImage.free(data);
    }
}
