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

import org.overrun.binpacking.*;
import org.overrun.glib.gl.GL;
import recx.client.render.RenderSystem;
import recx.util.Identifier;

import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class TextureAtlas extends Texture2D {
    public static final Identifier BLOCK = Identifier.recx("block-atlas");
    private final Map<Identifier, PackerRegion<?>> regionMap = HashMap.newHashMap(256);

    public TextureAtlas() {
        super();
    }

    public void pack(Map<Identifier, NativeImage> images) {
        final GrowingPacker packer = new GrowingPacker();
        final var regions = new ArrayList<PackerRegion<NativeImage>>();
        for (var e : images.entrySet()) {
            final NativeImage image = e.getValue();
            final PackerRegion<NativeImage> region = PackerRegion.delegate(image, image);
            regions.add(region);
            regionMap.put(e.getKey(), region);
        }
        packer.fit(Packer.sort(regions));
        width = packer.width();
        height = packer.height();
        setup();
        GL.texImage2D(GL.TEXTURE_2D,
            0,
            GL.RGBA,
            width,
            height,
            0,
            GL.RGBA,
            GL.UNSIGNED_BYTE,
            MemorySegment.NULL);
        for (var region : regions) {
            //noinspection resource
            region.ifFitPresent((r, fit) ->
                GL.texSubImage2D(GL.TEXTURE_2D,
                    0,
                    fit.x(),
                    fit.y(),
                    r.width(),
                    r.height(),
                    GL.RGBA,
                    GL.UNSIGNED_BYTE,
                    r.userdata().data())
            );
        }
        RenderSystem.bindTexture2D(0);
    }

    public float normalizeByWidth(int offset) {
        return (float) offset / width;
    }

    public float normalizeByHeight(int offset) {
        return (float) offset / height;
    }

    private int getPos(Identifier id, Function<PackerFitPos, Integer> mapper) {
        final PackerRegion<?> region = regionMap.get(id);
        return region != null ? region.fit().map(mapper).orElse(0) : 0;
    }

    private int getSize(Identifier id, ToIntFunction<PackerRegionSize> mapper) {
        final PackerRegion<?> region = regionMap.get(id);
        return region != null ? mapper.applyAsInt(region) : 0;
    }

    public int getU0(Identifier id) {
        return getPos(id, PackerFitPos::x);
    }

    public int getV0(Identifier id) {
        return getPos(id, PackerFitPos::y);
    }

    public int getU1(Identifier id) {
        return getU0(id) + getWidth(id);
    }

    public int getV1(Identifier id) {
        return getV0(id) + getHeight(id);
    }

    public int getWidth(Identifier id) {
        return getSize(id, PackerRegionSize::width);
    }

    public int getHeight(Identifier id) {
        return getSize(id, PackerRegionSize::height);
    }
}
