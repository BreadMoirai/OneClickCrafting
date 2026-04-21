//? 26.1 {
/*package com.github.breadmoirai.oneclickcrafting.recipebook.v20_1;

import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.mixin.AbstractRecipeBookScreenAccessor;
import com.github.breadmoirai.oneclickcrafting.mixin.ClientRecipeBookAccessor;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_9.RecipeBookComponentAccessor;
import com.github.breadmoirai.oneclickcrafting.recipebook.OneClickRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
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

   @Override
   public OneClickItemStack recipeResult(int recipeId) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level == null) return null;
      if (minecraft.player == null) return null;
      LocalPlayer player = minecraft.player;
      Map<RecipeDisplayId, RecipeDisplayEntry> recipes = ((ClientRecipeBookAccessor) player.getRecipeBook()).getKnown();
      ContextMap context = SlotDisplayContext.fromLevel(minecraft.level);
      return new OneClickItemStack(recipes.get(new RecipeDisplayId(recipeId)).display().result().resolveForFirstStack(context));
   }
}

*///?}