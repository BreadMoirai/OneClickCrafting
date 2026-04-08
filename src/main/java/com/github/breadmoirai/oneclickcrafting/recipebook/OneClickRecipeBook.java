package com.github.breadmoirai.oneclickcrafting.recipebook;

import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
//? 26.1 {
import com.github.breadmoirai.oneclickcrafting.recipebook.v26_1.OneClickRecipeBookImpl;
//?} >=1.21.10 <=1.21.11 {
/*import com.github.breadmoirai.oneclickcrafting.recipebook.v21_11.OneClickRecipeBookImpl;
*///?}

public interface OneClickRecipeBook {
   static OneClickRecipeBook getInstance() {
      return new OneClickRecipeBookImpl();
   }

   int selectLast(boolean isShift);

   OneClickItemStack recipeResult(int recipeId);
}