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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
//#if MC >= 11800
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
//#else
//$$ import org.apache.logging.log4j.LogManager;
//$$ import org.apache.logging.log4j.Logger;
//#endif

public class MCDRCompletion implements ModInitializer {

    public static final Logger LOGGER =
            //#if MC >= 11800
            LogUtils.getLogger();
    //#else
    //$$ LogManager.getLogger();
    //#endif

    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    public static final String MOD_ID = "mcdrc";
    public static String MOD_NAME = "unknown";
    public static String MOD_VERSION = "unknown";

    @Override
    public void onInitialize() {

        ModMetadata metadata = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata();
        MOD_NAME = metadata.getName();
        MOD_VERSION = metadata.getVersion().getFriendlyString();

    }
}
