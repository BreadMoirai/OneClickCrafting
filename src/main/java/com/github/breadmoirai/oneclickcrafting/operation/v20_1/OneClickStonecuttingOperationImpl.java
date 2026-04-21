//? <1.21.1 {
package com.github.breadmoirai.oneclickcrafting.operation.v20_1;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.operation.OneClickStonecuttingOperation;
import com.github.breadmoirai.oneclickcrafting.stonecutter.OneClickStonecutterRecipe;
import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;

public class OneClickStonecuttingOperationImpl extends OneClickStonecuttingOperation {

   public OneClickStonecuttingOperationImpl(OneClickCraftingMod mod, int selectedRecipe, OneClickStonecutterRecipe recipe, int button) {
      super(mod, selectedRecipe, recipe, button);
   }

   @Override
   public boolean craft() {
      if (onNextUpdate != null) {
         debug("craft(stonecutter): running deferred action");
         var next = onNextUpdate;
         onNextUpdate = next.onNextUpdate();
         return onNextUpdate == null;
      }

      var input = getMod().inventory.getSlot(0);
      debug("craft(stonecutter): drop=" + isDrop() + " shift=" + isShift() + " input.count=" + input.count());

      if (isDrop()) {
         boolean reinsert = input.count() > 1;
         debug("craft(stonecutter): drop item (slot 1), reinsert=" + reinsert);
         getMod().inventory.dropItem(1);
         if (reinsert) {
            getMod().inventory.shiftClickSlot(0);
         }
         refill();
         return true;
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
         }
      }
   }
}
//? }
