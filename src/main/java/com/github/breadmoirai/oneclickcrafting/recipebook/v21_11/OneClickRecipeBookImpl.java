//? >=1.21.10 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.recipebook.v21_11;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.ClientRecipeBookAccessor;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.RecipeBookScreenAccessor;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.RecipeBookWidgetAccessor;
import com.github.breadmoirai.oneclickcrafting.recipebook.OneClickRecipeBook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class OneClickRecipeBookImpl implements OneClickRecipeBook {
   @Override
   public int selectLast(boolean isShift) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (!(client.currentScreen instanceof RecipeBookScreen<?> screen)) return -1;
      RecipeBookWidgetAccessor recipeBook = (RecipeBookWidgetAccessor) ((RecipeBookScreenAccessor) screen).getRecipeBook();
      if (recipeBook.getSelectedRecipeResults() == null || recipeBook.getSelectedRecipe() == null) return -1;
      recipeBook.callSelect(recipeBook.getSelectedRecipeResults(), recipeBook.getSelectedRecipe(), isShift);
      ClickableWidget.playClickSound(MinecraftClient.getInstance().getSoundManager());
      return recipeBook.getSelectedRecipe().index();
   }

   @Override
   public OneClickItemStack recipeResult(int recipeId) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      ClientWorld world = minecraft.world;
      if (world == null) return null;
      ClientPlayerEntity player = minecraft.player;
      if (player == null) return null;
      Map<NetworkRecipeId, RecipeDisplayEntry> recipes = ((ClientRecipeBookAccessor) player.getRecipeBook()).getRecipes();
      return new OneClickItemStack(recipes.get(new NetworkRecipeId(recipeId)).display().result().getStacks(
            SlotDisplayContexts.createParameters(world))
         .getFirst());
   }
}

*///?}