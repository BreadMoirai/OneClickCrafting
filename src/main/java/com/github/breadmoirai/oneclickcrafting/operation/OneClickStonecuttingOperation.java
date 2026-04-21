package com.github.breadmoirai.oneclickcrafting.operation;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.operation.v20_1.OneClickStonecuttingOperationImpl;
import com.github.breadmoirai.oneclickcrafting.stonecutter.OneClickStonecutterRecipe;

public abstract class OneClickStonecuttingOperation extends OneClickOperation {
   public static OneClickStonecuttingOperation create(OneClickCraftingMod mod, int selectedRecipe, OneClickStonecutterRecipe recipe, int button) {
      return new OneClickStonecuttingOperationImpl(mod, selectedRecipe, recipe, button);
   }

   protected OneClickStonecutterRecipe recipe;
   protected DeferredOperation onNextUpdate;

   public OneClickStonecuttingOperation(OneClickCraftingMod mod, int selectedRecipe, OneClickStonecutterRecipe recipe, int button) {
      super(mod, selectedRecipe, button, recipe.result());
      this.recipe = recipe;
   }

   @Override
   protected boolean checkEnabled() {
      if (!getMod().config.isEnableStonecutter()) return false;
      return super.checkEnabled();
   }

   protected void refill() {
      int slot = getMod().inventory.findMatchingSlot(recipe.ingredient());
      if (slot == -1) return;
      int count = getMod().inventory.getSlot(slot).count();
      boolean multi = count > 1;
      if (multi) {
         getMod().inventory.leftClickSlot(slot);
         getMod().inventory.rightClickSlot(0);
         getMod().inventory.leftClickSlot(slot);
      } else {
         getMod().inventory.shiftClickSlot(slot);
      }
   }
}
