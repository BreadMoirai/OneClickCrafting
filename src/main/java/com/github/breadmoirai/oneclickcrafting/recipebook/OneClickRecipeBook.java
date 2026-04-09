package com.github.breadmoirai.oneclickcrafting.recipebook;

import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.recipebook.v26_1.OneClickRecipeBookImpl;

public interface OneClickRecipeBook {
   static OneClickRecipeBook getInstance() {
      return new OneClickRecipeBookImpl();
   }

   int selectLast(boolean isShift);

   OneClickItemStack recipeResult(int recipeId);
}