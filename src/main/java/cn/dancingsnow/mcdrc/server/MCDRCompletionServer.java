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

package cn.dancingsnow.mcdrc.server;

import cn.dancingsnow.mcdrc.MCDRCompletion;
import cn.dancingsnow.mcdrc.ModConfig;
import cn.dancingsnow.mcdrc.command.NodeData;
import net.fabricmc.api.DedicatedServerModInitializer;
//#if MC >= 11900
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
//#else
//$$ import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
//#endif
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

import static cn.dancingsnow.mcdrc.MCDRCompletion.MOD_ID;

public class MCDRCompletionServer implements DedicatedServerModInitializer {
    public static NodeData nodeData = null;
    public static ModConfig modConfig = new ModConfig(
            FabricLoader.getInstance().getConfigDir().resolve("%s.json".formatted(MOD_ID))
    );
    @Override
    public void onInitializeServer() {
        if (!modConfig.load()) {
            MCDRCompletion.LOGGER.error("MCDR-Completion load config fail.");
            throw new IllegalStateException("MCDR-Completion init server fail");
        }

        modConfig.save();

        CommandRegistrationCallback.EVENT.register(NodeReloadCommand::register);

        NodeChangeWatcher.init();
        loadNodeData();
    }

    public static void loadNodeData() {
        try {
            Path nodePath = Path.of(modConfig.getNodePath());
            if (Files.exists(nodePath)) {
                NodeData data = MCDRCompletion.GSON.fromJson(Files.newBufferedReader(nodePath), NodeData.class);
                if (data != null) nodeData = data;
            } else {
                MCDRCompletion.LOGGER.error("MCDR-Completion node file not exist.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            MCDRCompletion.LOGGER.error("MCDR-Completion has some error:", e);
        }
    }

    public static NodeData getNodeData() {
        return nodeData;
    }
}
