package com.github.breadmoirai.oneclickcrafting.stonecutter;

import com.github.breadmoirai.oneclickcrafting.stonecutter.v21_5.OneClickStonecutterImpl;

public interface OneClickStonecutter {
   static OneClickStonecutter getInstance() {
      return new OneClickStonecutterImpl();
   }

   void selectRecipe(int recipeId);

   OneClickStonecutterRecipe getRecipe(int recipeId);
}
