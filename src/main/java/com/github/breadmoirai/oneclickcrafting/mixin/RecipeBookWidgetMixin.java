package com.github.breadmoirai.oneclickcrafting.mixin;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.NetworkRecipeId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {


   @Inject(at = @At("TAIL"), method= "select(Lnet/minecraft/client/gui/screen/recipebook/RecipeResultCollection;Lnet/minecraft/recipe/NetworkRecipeId;)Z")
   public void clickRecipeTail(RecipeResultCollection results, NetworkRecipeId recipeId, CallbackInfoReturnable<Boolean> cir) {
      OneClickCraftingClient.getInstance().recipeClicked(results, recipeId);
   }

}
