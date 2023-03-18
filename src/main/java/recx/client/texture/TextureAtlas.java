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

import org.overrun.binpacking.GrowingPacker;
import org.overrun.binpacking.Packer;
import org.overrun.binpacking.PackerRegion;
import org.overrun.glib.gl.GL;
import recx.client.render.RenderSystem;

import java.lang.foreign.MemorySegment;
import java.util.ArrayList;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class TextureAtlas extends Texture2D {
    public TextureAtlas() {
        super();
    }

    public void pack(NativeImage... images) {
        final GrowingPacker packer = new GrowingPacker();
        final var regions = new ArrayList<PackerRegion<NativeImage>>();
        for (NativeImage image : images) {
            regions.add(PackerRegion.delegate(image, image));
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
}
