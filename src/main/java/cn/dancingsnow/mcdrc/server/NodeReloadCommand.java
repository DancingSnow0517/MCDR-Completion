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
import com.mojang.brigadier.CommandDispatcher;
//#if MC > 11900
import net.minecraft.command.CommandRegistryAccess;
//#endif
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
//#if MC > 11900
import net.minecraft.text.Text;
//#else
//$$ import net.minecraft.text.LiteralText;
//#endif

import static net.minecraft.server.command.CommandManager.literal;

public class NodeReloadCommand {
    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            //#if MC > 11900
            CommandRegistryAccess registryAccess,
            //#endif
            //#if MC > 11900
            CommandManager.RegistrationEnvironment environment
            //#else
            //$$ boolean b
            //#endif
    ) {
        dispatcher.register(
                literal(MCDRCompletion.MOD_ID)
                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                        .then(literal("reload").executes(context -> {
                            //#if MC > 12000
                            //$$ context.getSource().sendFeedback(() -> Text.literal("Reloading nodes..."), true);
                            //#elseif MC > 19000
                            context.getSource().sendFeedback(Text.literal("Reloading nodes..."), true);
                            //#else
                            //$$ context.getSource().sendFeedback(new LiteralText("Reloading nodes..."), true);
                            //#endif
                            return 1;
                        }))
        );
    }
}
