//? 26.1 {
/*package com.github.breadmoirai.oneclickcrafting.recipebook.v26_1;


import com.github.breadmoirai.oneclickcrafting.mixin.v26_1.AbstractRecipeBookScreenAccessor;
import com.github.breadmoirai.oneclickcrafting.mixin.v26_1.RecipeBookComponentAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public class OneClickRecipeBookImpl {

   public class OneClickRecipeBook {
      public int selectLast(boolean isShift) {
         Minecraft minecraft = Minecraft.getInstance();
         LocalPlayer player = minecraft.player;
         if (player == null) return -1;
         if (!(minecraft.screen instanceof AbstractRecipeBookScreen<? extends RecipeBookMenu> recipeBookScreen)) return -1;
         RecipeBookComponent<?> bookComponent = ((AbstractRecipeBookScreenAccessor) recipeBookScreen).getRecipeBookComponent();
         RecipeBookComponentAccessor bookMixin = (RecipeBookComponentAccessor) bookComponent;
         RecipeDisplayId lastRecipe = bookMixin.getLastRecipe();
         RecipeCollection lastCollection = bookMixin.getLastRecipeCollection();
         if (lastRecipe == null || lastCollection == null) return -1;
         AbstractWidget.playButtonClickSound(minecraft.getSoundManager());
         if (!bookMixin.callTryPlaceRecipe(lastCollection, lastRecipe, isShift)) return -1;
         return lastRecipe.index();
      }
   }
}

*///?}