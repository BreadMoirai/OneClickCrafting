package com.github.breadmoirai.oneclickcrafting.operation;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.operation.v26_1.OneClickCraftingOperationImpl;

public abstract class OneClickCraftingOperation extends OneClickOperation{
   public static OneClickCraftingOperation create(OneClickCraftingMod mod, int recipeId, int button) {
      return new OneClickCraftingOperationImpl(mod, recipeId, button);
   }

   public OneClickCraftingOperation(OneClickCraftingMod mod, int recipeId, int button, OneClickItemStack result) {
      super(mod, recipeId, button, result);
   }

   /**
    * <a href="https://github.com/BreadMoirai/OneClickCrafting/issues/25">Issue #25</a>
    * Edge case handling for when
    * the server's InputSlotFiller.fill() returns early (no inventory changes,
    * no onContentChanged, no ScreenHandlerSlotUpdateS2CPacket for slot 0) only when:
    *     – the grid already matches the clicked recipe AND the player's inventory
    *       has no additional matching ingredients (countCrafts == 1, limited to
    *       whatever is already in the grid).
    * We therefore only call onResultSlotUpdated manually for this case, detected by
    * checking that the player's main inventory alone (without grid items) cannot
    * satisfy the recipe's crafting requirements — meaning no extra ingredients exist.
    * <p>
    * This applies to both the 2×2 inventory grid (InventoryScreen, slots 1–4) and
    * the 3×3 crafting table grid (CraftingScreen, slots 1–9).
    * @return false if we should move the output item immediately
    */
   public boolean shouldWaitForResultSlotUpdate() {
      return !(canCraftMore() || anySlotMax());
   }

   /**
    * (a) countCrafts <= slotCount — inventory has no extra ingredients,
    * detected by checking the inventory alone can't satisfy the recipe.
    * @return true if clicking on a recipe would add more items into the input slots
    */
   public abstract boolean canCraftMore();

   /**
    * (b) slotCount >= maxCount — the slot is already at max stack size,
    *  so nothing more can be added regardless of inventory contents.
    * @return true if any input slot is at max stack size
    */
   public abstract boolean anySlotMax();

   public abstract boolean craft();
}
