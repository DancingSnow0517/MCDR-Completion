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

package cn.dancingsnow.mcdrc.mixins.client;
import cn.dancingsnow.mcdrc.client.MCDRCompletionClient;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin {
    @Shadow
    @Final
    private TextFieldWidget textField;

    @Shadow
    @Nullable
    private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    protected static int getStartOfCurrentWord(String input) {
        return 0;
    }

    @Shadow public abstract void show(boolean narrateFirstSuggestion);

    @Shadow private boolean completingSuggestions;

    @Shadow @Nullable private ChatInputSuggestor.@Nullable SuggestionWindow window;

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "refresh()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;getCursor()I", shift = At.Shift.AFTER), cancellable = true)
    public void refreshMixin(CallbackInfo ci) {
        String text = this.textField.getText();
        if (text.startsWith("!") || text.startsWith("！")) {
            text = text.replace('！', '!');
            String string = text.substring(0, this.textField.getCursor());
            if (this.window == null || !this.completingSuggestions) {
                int word = getStartOfCurrentWord(string);
                Collection<String> suggestion = MCDRCompletionClient.getSuggestion(string);
                if (!suggestion.isEmpty()) {
                    this.pendingSuggestions = CommandSource.suggestMatching(suggestion,
                            new SuggestionsBuilder(string, word));
                    this.pendingSuggestions.thenRun(() -> {
                        if (this.pendingSuggestions.isDone()) {
                            this.show(true);
                        }
                    });
                } else {
                    Collection<String> player_list = this.client.player.networkHandler.getCommandSource()
                            //#if MC > 11900
                            .getChatSuggestions();
                            //#else
                            //$$ .getPlayerNames();
                            //#endif
                    this.pendingSuggestions = CommandSource.suggestMatching(player_list, new SuggestionsBuilder(string, word));
                }
            }
            ci.cancel();
        }
    }
}
