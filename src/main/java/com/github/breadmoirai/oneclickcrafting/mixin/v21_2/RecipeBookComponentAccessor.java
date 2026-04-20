//? >=1.21.2 <=1.21.8 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v21_2;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeBookComponent.class)
public interface RecipeBookComponentAccessor {
   @Accessor
   RecipeDisplayId getLastRecipe();

   @Accessor
   RecipeCollection getLastRecipeCollection();

   @Invoker
   boolean callTryPlaceRecipe(RecipeCollection collection, RecipeDisplayId recipeDisplayId);
}
*///?}
