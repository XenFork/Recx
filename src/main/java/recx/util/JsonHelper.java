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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class JsonHelper {
    public static String getString(JsonObject object, String name) {
        if (!object.has(name)) {
            throw new IllegalArgumentException('\'' + name + "' doesn't exist");
        }
        final JsonElement element = object.get(name);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException('\'' + name + "' isn't a string");
        }
        return element.getAsString();
    }

    private static JsonElement getNumber(JsonArray array, int index) {
        final JsonElement element = array.get(index);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw new IllegalArgumentException("array[" + index + "] isn't a number");
        }
        return element;
    }

    public static int getInt(JsonArray array, int index) {
        return getNumber(array, index).getAsInt();
    }

    public static float getFloat(JsonArray array, int index) {
        return getNumber(array, index).getAsFloat();
    }
}
