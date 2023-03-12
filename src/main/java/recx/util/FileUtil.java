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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class FileUtil {
    private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public static String readString(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            final StringBuilder sb = new StringBuilder(512);
            String line = reader.readLine();
            if (line != null) {
                sb.append(line);
            }
            while ((line = reader.readLine()) != null) {
                sb.append('\n').append(line);
            }
            return sb.toString();
        }
    }

    public static String readString(ClassLoader loader, String filename) throws IOException {
        return readString(Objects.requireNonNull(loader.getResourceAsStream(filename),
            "Failed to get resource '" + filename + '\''));
    }

    public static String readString(Class<?> cls, String filename) throws IOException {
        return readString(cls.getClassLoader(), filename);
    }

    public static String readString(String filename) throws IOException {
        return readString(WALKER.getCallerClass(), filename);
    }
}
