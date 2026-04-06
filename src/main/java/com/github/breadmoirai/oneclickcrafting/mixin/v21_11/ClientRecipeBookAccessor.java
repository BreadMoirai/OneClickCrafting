//? >=1.21.10 <=1.21.11 {
package com.github.breadmoirai.oneclickcrafting.mixin.v21_11;

import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientRecipeBook.class)
public interface ClientRecipeBookAccessor {
   @Accessor
   Map<NetworkRecipeId, RecipeDisplayEntry> getRecipes();
}
//?}
