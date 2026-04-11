package com.github.breadmoirai.oneclickcrafting.recipebook;

import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.recipebook.v21_8.OneClickRecipeBookImpl;

public interface OneClickRecipeBook {
   static OneClickRecipeBook getInstance() {
      return new OneClickRecipeBookImpl();
   }

   int selectLast(boolean isShift);

   OneClickItemStack recipeResult(int recipeId);

   void craftRecipe(net.minecraft.client.gui.screens.recipebook.RecipeCollection collection,
                    net.minecraft.world.item.crafting.display.RecipeDisplayId id,
                    boolean shift);
}