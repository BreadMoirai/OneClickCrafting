//? 26.1 {
/*package com.github.breadmoirai.oneclickcrafting.stonecutter.v20_1;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.stonecutter.OneClickStonecutter;
import com.github.breadmoirai.oneclickcrafting.stonecutter.OneClickStonecutterRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;

public class OneClickStonecutterImpl implements OneClickStonecutter {

   @Override
   public void selectRecipe(int recipeId) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player == null) return;
      if (minecraft.gameMode == null) return;
      if (!(minecraft.screen instanceof StonecutterScreen stonecutterScreen)) return;
      StonecutterMenu menu = stonecutterScreen.getMenu();
      menu.clickMenuButton(minecraft.player, recipeId);
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, recipeId);
      OneClickEvents.STONECUTTER_CLICK.invoker().onStonecutterClick(recipeId, OneClickCraftingMod.getInstance().config.isEnableRightClick() ? 1 : 0);
   }

   @Override
   public OneClickStonecutterRecipe getRecipe(int recipeId) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player == null) return null;
      if (minecraft.level == null) return null;
      if (!(minecraft.screen instanceof StonecutterScreen stonecutterScreen)) return null;
      StonecutterMenu menu = stonecutterScreen.getMenu();
      if (menu.getNumberOfVisibleRecipes() == 0) {
         debug("onStonecutterClick: getAvailableRecipes() is empty, ignoring");
         return OneClickStonecutterRecipe.EMPTY;
      }
      SelectableRecipe.SingleInputEntry<StonecutterRecipe> entry = menu.getVisibleRecipes()
         .entries().get(recipeId);
      ContextMap context = SlotDisplayContext.fromLevel(minecraft.level);
      SelectableRecipe<StonecutterRecipe> selectableRecipe = entry.recipe();
      ItemStack result = selectableRecipe.optionDisplay().resolveForFirstStack(context);
      if (result.isEmpty()) return OneClickStonecutterRecipe.EMPTY;
      // Use recipe.input() if available, otherwise fall back to the entry's ingredient (client-side display recipes).
      if (selectableRecipe.recipe().isPresent()) {
         StonecutterRecipe recipe = selectableRecipe.recipe().get().value();
         return new OneClickStonecutterRecipe(new OneClickItemStack(result), ingredient -> recipe.input().test(ingredient.stack()));
      }
      return new OneClickStonecutterRecipe(new OneClickItemStack(result), ingredient -> entry.input().test(ingredient.stack()));
   }

}

*///?}