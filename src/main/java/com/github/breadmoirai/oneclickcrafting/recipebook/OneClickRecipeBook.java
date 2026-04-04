package com.github.breadmoirai.oneclickcrafting.recipebook;
//? 26.1 {
/*import com.github.breadmoirai.oneclickcrafting.mixin.v26_1.AbstractRecipeBookScreenMixin;
import com.github.breadmoirai.oneclickcrafting.mixin.v26_1.RecipeBookComponentMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public class OneClickRecipeBook {
   public int selectLast() {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player == null) return -1;
      if (!(minecraft.screen instanceof AbstractRecipeBookScreen<? extends RecipeBookMenu> recipeBookScreen)) return -1;
      RecipeBookComponent<?> bookComponent = ((AbstractRecipeBookScreenMixin) recipeBookScreen).getRecipeBookComponent();
      RecipeDisplayId lastRecipe = ((RecipeBookComponentMixin) bookComponent).getLastRecipe();
      if (lastRecipe == null) {
         return -1;
      }
      return lastRecipe.index();
   }
}
*///?} >=1.21.10 <=1.21.11 {

import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.display.SlotDisplayContexts;

import java.util.Map;

public class OneClickRecipeBook {
   public int selectLast(boolean isShift) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (!(client.currentScreen instanceof RecipeBookScreen<?> screen)) return -1;
      RecipeBookWidget<?> recipeBook = screen.recipeBook;
      if (recipeBook.selectedRecipeResults == null || recipeBook.selectedRecipe == null) return -1;
      recipeBook.select(recipeBook.selectedRecipeResults, recipeBook.selectedRecipe, isShift);
      return recipeBook.selectedRecipe.index();
   }

   public OneClickItemStack recipeResult(int recipeId) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      ClientWorld world = minecraft.world;
      if (world == null) return null;
      ClientPlayerEntity player = minecraft.player;
      if (player == null) return null;
      Map<NetworkRecipeId, RecipeDisplayEntry> recipes = player.getRecipeBook().recipes;
      return new OneClickItemStack(recipes.get(new NetworkRecipeId(recipeId)).display().result().getStacks(
            SlotDisplayContexts.createParameters(world))
         .getFirst());
   }
}
//?}
