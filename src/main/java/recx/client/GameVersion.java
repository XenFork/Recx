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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import recx.util.FileUtil;
import recx.util.JsonHelper;

import java.io.IOException;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class GameVersion {
    private static GameVersion instance;
    private final String version;
    private final boolean isSnapshot;

    private GameVersion(String version, boolean isSnapshot) {
        this.version = version;
        this.isSnapshot = isSnapshot;
    }

    public static GameVersion getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static GameVersion load() {
        try {
            final JsonObject object = JsonParser.parseString(FileUtil.readString("version.json")).getAsJsonObject();
            return new GameVersion(
                JsonHelper.getString(object, "version"),
                Boolean.parseBoolean(JsonHelper.getString(object, "isSnapshot"))
            );
        } catch (IOException e) {
            // todo: log
            e.printStackTrace();
            return new GameVersion("Version Unknown", true);
        }
    }

    public String version() {
        return version;
    }

    public boolean isSnapshot() {
        return isSnapshot;
    }
}
