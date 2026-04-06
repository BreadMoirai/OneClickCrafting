package com.github.breadmoirai.oneclickcrafting.operation;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.stonecutter.OneClickStonecutterRecipe;
import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;

public class OneClickStonecuttingOperation extends OneClickOperation {
   OneClickStonecutterRecipe recipe;
   private DeferredOperation onNextUpdate;

   public OneClickStonecuttingOperation(OneClickCraftingMod mod, int selectedRecipe, OneClickStonecutterRecipe recipe, int button) {
      super(mod, selectedRecipe, button, recipe.result());
      this.recipe = recipe;
   }

   @Override
   protected boolean checkEnabled() {
      if (!getMod().config.isEnableStonecutter()) return false;
      return super.checkEnabled();
   }

   @Override
   public boolean craft() {
      if (onNextUpdate != null) {
         debug("craft(stonecutter): running deferred action");
         DeferredOperation next = onNextUpdate;
         onNextUpdate = next.onNextUpdate();
         return onNextUpdate == null;
      }

      OneClickItemStack input = getMod().inventory.getSlot(0);
      debug("craft(stonecutter): drop=" + isDrop() + " shift=" + isShift() + " input.count=" + input.count());

      if (isDrop()) {
         if (isShift()) {
            if (input.count() == 1) {
               debug("craft(stonecutter): stacking to 64, deferring drop+refill");
               getMod().inventory.moveMatchingIntoSlot(0);
               getMod().stonecutter.selectRecipe(getRecipeId());
               onNextUpdate = () -> {
                  getMod().inventory.dropStack(1);
                  refill();
                  return null;
               };
               return false;
            } else {
               debug("craft(stonecutter): drop stack (slot 1) + refill");
               getMod().inventory.dropStack(1);
               refill();
               return true;
            }
         } else {
            boolean reinsert = input.count() > 1;
            debug("craft(stonecutter): drop item (slot 1), reinsert=" + reinsert);
            getMod().inventory.dropItem(1);
            if (reinsert) {
               getMod().inventory.shiftClickSlot(0);
            }
            refill();
            return true;
         }
      } else {
         if (isShift()) {
            if (input.count() == 1) {
               debug("craft(stonecutter): stacking to 64, deferring shift-click+refill");
               getMod().inventory.moveMatchingIntoSlot(0);
               getMod().stonecutter.selectRecipe(getRecipeId());
               onNextUpdate = () -> {
                  getMod().inventory.shiftClickSlot(1);
                  refill();
                  return null;
               };
               return false;
            } else {
               debug("craft(stonecutter): shift-click slot 1 + refill");
               getMod().inventory.shiftClickSlot(1);
               refill();
               return true;
            }
         } else {
            if (input.count() != 1) {
               debug("craft(stonecutter): isolating single input, deferring shift-click");
               getMod().inventory.shiftClickSlot(0);
               refill();
               getMod().stonecutter.selectRecipe(getRecipeId());
               onNextUpdate = () -> {
                  getMod().inventory.shiftClickSlot(1);
                  return null;
               };
               return false;
            } else {
               debug("craft(stonecutter): shift-click slot 1 + refill");
               getMod().inventory.shiftClickSlot(1);
               refill();
               return true;
            }
         }
      }
   }

   private void refill() {
      debug("refill(stonecutter): searching for ingredient");
      int slot = getMod().inventory.findMatchingSlot(recipe.ingredient());
      debug("refill(stonecutter): search returned " + slot);
      if (slot == -1) return;
      int count = getMod().inventory.getSlot(slot).count();
      boolean multi = count > 1;
      debug("refill(stonecutter): slot has " + count + " items");
      if (multi) {
         getMod().inventory.leftClickSlot(slot);
         getMod().inventory.rightClickSlot(0);
         getMod().inventory.leftClickSlot(slot);
      } else {
         getMod().inventory.shiftClickSlot(slot);
      }
   }
}
