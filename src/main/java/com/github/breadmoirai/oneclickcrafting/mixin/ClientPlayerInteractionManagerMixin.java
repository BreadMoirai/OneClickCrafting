package com.github.breadmoirai.oneclickcrafting.mixin;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(at = @At("TAIL"), method = "clickRecipe(ILnet/minecraft/recipe/Recipe;Z)V")
    private void clickRecipe(int syncId, Recipe<?> recipe, boolean craftAll, CallbackInfo ci) {
        OneClickCraftingClient.lastCraft = recipe.getOutput();
    }
}
