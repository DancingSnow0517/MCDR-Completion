/*
 * This file is part of the MCDR-Completion project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  DancingSnow and contributors
 *
 * MCDR-Completion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MCDR-Completion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MCDR-Completion.  If not, see <https://www.gnu.org/licenses/>.
 */

package cn.dancingsnow.mcdrc;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private final Path path;

    public ModConfig(Path path) {
        this.path = path;
    }

    private ConfigData data = new ConfigData();

    public boolean save() {
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
                MCDRCompletion.LOGGER.error("Save {} error: createFile fail.", path);
                return false;
            }
        }

        try (BufferedWriter bfw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            bfw.write(MCDRCompletion.GSON.toJson(getData()));
        } catch (IOException e) {
            e.printStackTrace();
            MCDRCompletion.LOGGER.error("Save {} error", path);
            return false;
        }
        return true;
    }

    public boolean load() {
        if (!Files.exists(path)) {
            return save();
        }
        try (BufferedReader bfr = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            setData(MCDRCompletion.GSON.fromJson(bfr, ConfigData.class));
        } catch (IOException e) {
            e.printStackTrace();
            MCDRCompletion.LOGGER.error("Load {} error: newBufferedReader fail.", path);
            return false;
        } catch (JsonParseException e) {
            MCDRCompletion.LOGGER.error("Json {} parser fail!!", path);
            return false;
        }
        return true;
    }

    public ConfigData getData() {
        return data;
    }

    public void setData(ConfigData data) {
        this.data = data;
    }

    public String getNodePath() {
        return data.nodePath;
    }

    public void setNodePath(String node_path) {
        data.nodePath = node_path;
        save();
    }

    public static class ConfigData {
        @SerializedName("node_path")
        public String nodePath = "config/node.json";
    }
}
