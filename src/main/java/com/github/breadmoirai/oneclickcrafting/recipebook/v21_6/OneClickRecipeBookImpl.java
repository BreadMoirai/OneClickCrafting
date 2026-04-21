//? >=1.21.2 <=1.21.8 {
/*package com.github.breadmoirai.oneclickcrafting.recipebook.v20_1;

import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.mixin.ClientRecipeBookAccessor;
import com.github.breadmoirai.oneclickcrafting.mixin.AbstractRecipeBookScreenAccessor;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_2.RecipeBookComponentAccessor;
import com.github.breadmoirai.oneclickcrafting.recipebook.OneClickRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import java.util.Map;

public class OneClickRecipeBookImpl implements OneClickRecipeBook {
   @Override
   public int selectLast(boolean isShift) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player == null) return -1;
      if (!(minecraft.screen instanceof AbstractRecipeBookScreen<? extends RecipeBookMenu> screen)) return -1;
      RecipeBookComponent<?> recipeBook = ((AbstractRecipeBookScreenAccessor) screen).getRecipeBookComponent();
      RecipeBookComponentAccessor accessor = (RecipeBookComponentAccessor) recipeBook;
      if (accessor.getLastRecipeCollection() == null || accessor.getLastRecipe() == null) return -1;
      accessor.callTryPlaceRecipe(accessor.getLastRecipeCollection(), accessor.getLastRecipe());
      AbstractWidget.playButtonClickSound(minecraft.getSoundManager());
      return accessor.getLastRecipe().index();
   }

   @Override
   public OneClickItemStack recipeResult(int recipeId) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level == null) return null;
      LocalPlayer player = minecraft.player;
      if (player == null) return null;
      Map<RecipeDisplayId, RecipeDisplayEntry> recipes = ((ClientRecipeBookAccessor) player.getRecipeBook()).getKnown();
      ContextMap context = SlotDisplayContext.fromLevel(minecraft.level);
      return new OneClickItemStack(recipes.get(new RecipeDisplayId(recipeId)).display().result().resolveForFirstStack(context));
   }
}
*///?}
