package com.github.breadmoirai.oneclickcrafting.stonecutter;

//? 26.1 {
import com.github.breadmoirai.oneclickcrafting.stonecutter.v26_1.OneClickStonecutterImpl;
//?} >=1.21.10 <=1.21.11 {
/*import com.github.breadmoirai.oneclickcrafting.stonecutter.v21_11.OneClickStonecutterImpl;
*///?}

public interface OneClickStonecutter {
   static OneClickStonecutter getInstance() {
      return new OneClickStonecutterImpl();
   }

   void selectRecipe(int recipeId);

   OneClickStonecutterRecipe getRecipe(int recipeId);
}
