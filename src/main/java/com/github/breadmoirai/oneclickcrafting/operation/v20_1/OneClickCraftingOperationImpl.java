//? <1.21.1 {
package com.github.breadmoirai.oneclickcrafting.operation.v20_1;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;
import com.github.breadmoirai.oneclickcrafting.operation.OneClickCraftingOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class OneClickCraftingOperationImpl extends OneClickCraftingOperation {

   public OneClickCraftingOperationImpl(OneClickCraftingMod mod, int recipeId, int button) {
      super(mod, recipeId, button, mod.recipeBook.recipeResult(recipeId));
   }

   @Override
   public boolean anySlotMax() {
      Minecraft minecraft = Minecraft.getInstance();
      boolean isInventory = minecraft.screen instanceof InventoryScreen;
      boolean isCrafting = minecraft.screen instanceof CraftingScreen;
      if (!isInventory && !isCrafting) return false;
      int maxGridSlot = isInventory ? 4 : 9;
      for (int slotIdx = 1; slotIdx <= maxGridSlot; slotIdx++) {
         var s = getMod().inventory.getSlot(slotIdx);
         if (!s.isEmpty() && s.count() >= s.stack().getMaxStackSize()) return true;
      }
      return false;
   }

   @Override
   public boolean shouldWaitForResultSlotUpdate() {
      return false;
   }

   @Override
   public boolean canCraftMore() {
      return false;
   }

   @Override
   public boolean craft() {
      Minecraft client = Minecraft.getInstance();
      if (!(client.screen instanceof InventoryScreen || client.screen instanceof CraftingScreen)) return false;
      if (isDrop()) {
         if (isShift()) {
            debug("craft: drop stack (slot 0)");
            getMod().inventory.dropStack(0);
         } else {
            debug("craft: drop item (slot 0)");
            getMod().inventory.dropItem(0);
         }
      } else {
         debug("craft: shift-click (slot 0)");
         getMod().inventory.shiftClickSlot(0);
      }
      return true;
   }
}
//? }
