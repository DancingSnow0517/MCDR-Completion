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

package cn.dancingsnow.mcdrc.client;

import cn.dancingsnow.mcdrc.MCDRCompletion;
import cn.dancingsnow.mcdrc.MCDRCompletionNetwork;
import cn.dancingsnow.mcdrc.command.Node;
import cn.dancingsnow.mcdrc.command.NodeData;
import cn.dancingsnow.mcdrc.command.NodeType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.ArrayList;
import java.util.Collection;

@Environment(EnvType.CLIENT)
public class MCDRCompletionClient implements ClientModInitializer {

    public static NodeData nodeData = null;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(MCDRCompletionNetwork.COMMAND_PACKET_ID, (client, handler, buf, responseSender) -> {
            try {
                nodeData = MCDRCompletion.GSON.fromJson(buf.readString(1 << 20), NodeData.class);
            } catch (Exception e) {
                e.printStackTrace();
                MCDRCompletion.LOGGER.error("fail to receiver command packet: ", e);
            }
        });
    }

    public static Collection<String> getSuggestion(String text) {
        if (nodeData != null) {
            Collection<String> rt = new ArrayList<>();
            String[] args = text.split(" ");
            int word = args.length;
            if (word == 1 && !text.endsWith(" ")) {
                for (Node node : nodeData.data) {
                    if (node.type.equals(NodeType.LITERAL)) rt.add(node.name);
                }
                return rt;
            } else {
                Node currNode = null;
                for (Node node : nodeData.data) {
                    if (node.name.equalsIgnoreCase(args[0])) {
                        currNode = node;
                    }
                }
                if (currNode != null) {
                    int times;
                    if (text.endsWith(" ")) {
                        // find args[word-1] suggestion
                        times = word - 1;
                    } else {
                        // find args[word-2] suggestion
                        times = word - 2;
                    }
                    for (int i = 1; i <= times; i++) {
                        boolean flag = false;
                        for (Node node : currNode.children) {
                            if (args[i].equalsIgnoreCase(node.name)) {
                                currNode = node;
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            return rt;
                        }
                    }
                    for (Node node : currNode.children) {
                        if (node.type.equals(NodeType.LITERAL)) rt.add(node.name);
                    }
                } else {
                    return rt;
                }
            }
            return rt;
        } else {
            return new ArrayList<>();
        }
    }
}
