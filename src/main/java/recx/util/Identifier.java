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

package recx.util;

/**
 * @author squid233
 * @since 0.1.0
 */
@ValueObject(primitive = true)
public record Identifier(String namespace, String path) {
    public static final String DEFAULT_NAMESPACE = "recx";
    public static final String ASSETS = "assets";

    public static final String SHADERS = "shaders";
    public static final String TEXTURES = "textures";

    public static final String PNG = ".png";
    public static final String JSON = ".json";
    public static final String VERT_SHADER = ".vert";
    public static final String FRAG_SHADER = ".frag";

    private static Identifier of(String[] id) {
        if (id.length >= 2) return new Identifier(id[0], id[1]);
        if (id.length == 1) return recx(id[0]);
        return recx("");
    }

    public static Identifier of(String identifier) {
        return of(identifier.split(":", 2));
    }

    public static Identifier recx(String path) {
        return new Identifier(DEFAULT_NAMESPACE, path);
    }

    public boolean recxOwns() {
        return DEFAULT_NAMESPACE.equals(namespace);
    }

    public String toPath() {
        return namespace + '/' + path;
    }

    public String toPath(String beforeNamespace) {
        return beforeNamespace + '/' + toPath();
    }

    public String toPath(String beforeNamespace, String beforePath) {
        return beforeNamespace + '/' + namespace + '/' + beforePath + '/' + path;
    }

    public String toPath(String beforeNamespace, String beforePath, String afterPath) {
        return toPath(beforeNamespace, beforePath) + afterPath;
    }

    public String toTexturePath() {
        return toPath(ASSETS, TEXTURES, PNG);
    }

    @Override
    public String toString() {
        return namespace + ':' + path;
    }
}
