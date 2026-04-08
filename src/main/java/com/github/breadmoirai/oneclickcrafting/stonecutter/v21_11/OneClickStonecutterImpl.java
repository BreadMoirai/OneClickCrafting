//? >=1.21.10 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.stonecutter.v21_11;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.stonecutter.OneClickStonecutter;
import com.github.breadmoirai.oneclickcrafting.stonecutter.OneClickStonecutterRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.sound.SoundEvents;

import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;

public class OneClickStonecutterImpl implements OneClickStonecutter {

   @Override
   public void selectRecipe(int recipeId) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (!(client.currentScreen instanceof StonecutterScreen screen)) return;
      screen.getScreenHandler().onButtonClick(client.player, recipeId);
      MinecraftClient.getInstance().getSoundManager().play(
         PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
      if (client.interactionManager == null) return;
      client.interactionManager.clickButton(screen.getScreenHandler().syncId, recipeId);
      OneClickEvents.STONECUTTER_CLICK.invoker().onStonecutterClick(recipeId, OneClickCraftingMod.getInstance().config.isEnableRightClick() ? 1 : 0);
   }

   @Override
   public OneClickStonecutterRecipe getRecipe(int recipeId) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (!(client.currentScreen instanceof StonecutterScreen screen)) return OneClickStonecutterRecipe.EMPTY;
      screen.getScreenHandler().onButtonClick(client.player, recipeId);
      StonecutterScreenHandler screenHandler = screen.getScreenHandler();
      CuttingRecipeDisplay.Grouping<StonecuttingRecipe> recipes = screenHandler.getAvailableRecipes();
      if (recipes.isEmpty()) {
         debug("onStonecutterClick: getAvailableRecipes() is empty, ignoring");
         return OneClickStonecutterRecipe.EMPTY;
      }
      CuttingRecipeDisplay.GroupEntry<StonecuttingRecipe> group = recipes.entries().get(recipeId);
      ItemStack result = ((SlotDisplay.StackSlotDisplay) group.recipe().optionDisplay()).stack();
      Ingredient input = group.input();
      return new OneClickStonecutterRecipe(new OneClickItemStack(result), ingredient -> input.test(ingredient.stack()));
   }

}

*///?}