//? <1.21.1 {
package com.github.breadmoirai.oneclickcrafting.stonecutter.v20_1;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.stonecutter.OneClickStonecutter;
import com.github.breadmoirai.oneclickcrafting.stonecutter.OneClickStonecutterRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.StonecutterRecipe;

import java.util.List;

import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;

public class OneClickStonecutterImpl implements OneClickStonecutter {

   @Override
   public void selectRecipe(int recipeId) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.gameMode == null) return;
      if (!(minecraft.screen instanceof StonecutterScreen screen)) return;
      StonecutterMenu menu = screen.getMenu();
      menu.clickMenuButton(minecraft.player, recipeId);
      minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, recipeId);
      OneClickEvents.STONECUTTER_CLICK.invoker().onStonecutterClick(recipeId, OneClickCraftingMod.getInstance().config.isEnableRightClick() ? 1 : 0);
   }

   @Override
   public OneClickStonecutterRecipe getRecipe(int recipeId) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level == null) return OneClickStonecutterRecipe.EMPTY;
      if (!(minecraft.screen instanceof StonecutterScreen screen)) return OneClickStonecutterRecipe.EMPTY;
      StonecutterMenu menu = screen.getMenu();
      List<StonecutterRecipe> recipes = menu.getRecipes();
      if (recipeId < 0 || recipeId >= recipes.size()) {
         debug("onStonecutterClick: recipe index out of bounds, ignoring");
         return OneClickStonecutterRecipe.EMPTY;
      }
      StonecutterRecipe recipe = recipes.get(recipeId);
      ItemStack result = recipe.getResultItem(minecraft.level.registryAccess());
      if (result.isEmpty()) return OneClickStonecutterRecipe.EMPTY;
      return new OneClickStonecutterRecipe(
         new OneClickItemStack(result),
         ingredient -> recipe.getIngredients().get(0).test(ingredient.stack())
      );
   }
}
//? }
