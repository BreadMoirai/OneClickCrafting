//? >=1.21.10 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v21_11;

import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.NetworkRecipeId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeBookWidget.class)
public interface RecipeBookWidgetAccessor {
   @Accessor
   RecipeResultCollection getSelectedRecipeResults();

   @Accessor
   NetworkRecipeId getSelectedRecipe();

   @Invoker
   boolean callSelect(RecipeResultCollection results, NetworkRecipeId recipeId, boolean shift);
}
*///?}
