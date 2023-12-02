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

import cn.dancingsnow.mcdrc.command.NodeData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

public class MCDRCompletionNetwork {
    public static final Identifier COMMAND_PACKET_ID = new Identifier(MCDRCompletion.MOD_ID, "command");

    public static void sendNodeDataToClient(ServerPlayNetworkHandler handler, NodeData nodeData) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(MCDRCompletion.GSON.toJson(nodeData), 1 << 20);
        ServerPlayNetworking.getSender(handler).sendPacket(COMMAND_PACKET_ID, buf);
    }
}
