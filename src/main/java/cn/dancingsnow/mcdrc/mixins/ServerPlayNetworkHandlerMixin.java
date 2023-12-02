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

package cn.dancingsnow.mcdrc.mixins;

import cn.dancingsnow.mcdrc.MCDRCompletionNetwork;
import cn.dancingsnow.mcdrc.command.NodeData;
import cn.dancingsnow.mcdrc.server.MCDRCompletionServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Unique
    private int mcdrc$sentCommandTree = 0;

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        final NodeData nodeData = MCDRCompletionServer.getNodeData();
        if (System.identityHashCode(nodeData) != mcdrc$sentCommandTree) {
            MCDRCompletionNetwork.sendNodeDataToClient((ServerPlayNetworkHandler) (Object) this, nodeData);
            mcdrc$sentCommandTree = System.identityHashCode(nodeData);
        }
    }
}
