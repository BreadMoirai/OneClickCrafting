//? 1.21.1 {
package com.github.breadmoirai.oneclickcrafting.recipebook.v21_1;

import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_1.RecipeBookComponentAccessor;
import com.github.breadmoirai.oneclickcrafting.recipebook.OneClickRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class OneClickRecipeBookImpl implements OneClickRecipeBook {

   public static ItemStack lastRecipeResult = ItemStack.EMPTY;

   @Override
   public int selectLast(boolean isShift) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player == null) return -1;
      RecipeBookComponent recipeBook;
      int containerId;
      if (minecraft.screen instanceof InventoryScreen inv) {
         recipeBook = inv.getRecipeBookComponent();
         containerId = inv.getMenu().containerId;
      } else if (minecraft.screen instanceof CraftingScreen craft) {
         recipeBook = craft.getRecipeBookComponent();
         containerId = craft.getMenu().containerId;
      } else {
         return -1;
      }
      RecipeBookPage page = ((RecipeBookComponentAccessor) recipeBook).getRecipeBookPage();
      RecipeHolder<?> lastRecipe = page.getLastClickedRecipe();
      if (lastRecipe == null) return -1;
      if (minecraft.level != null) {
         lastRecipeResult = lastRecipe.value().getResultItem(minecraft.level.registryAccess());
      }
      minecraft.gameMode.handlePlaceRecipe(containerId, lastRecipe, isShift);
      minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      return 0;
   }

   @Override
   public OneClickItemStack recipeResult(int recipeId) {
      if (lastRecipeResult.isEmpty()) return null;
      return new OneClickItemStack(lastRecipeResult);
   }
}
//? }
