//? >=1.21.8 <=1.21.11 {
package com.github.breadmoirai.oneclickcrafting.operation.v21_9;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;
import com.github.breadmoirai.oneclickcrafting.mixin.ClientRecipeBookAccessor;
import com.github.breadmoirai.oneclickcrafting.operation.OneClickCraftingOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

import java.util.Map;

public class OneClickCraftingOperationImpl extends OneClickCraftingOperation {

   public OneClickCraftingOperationImpl(OneClickCraftingMod mod, int recipeId, int button) {
      super(mod, recipeId, button, mod.recipeBook.recipeResult(recipeId));
   }

   @Override public boolean anySlotMax() {
      Minecraft minecraft = Minecraft.getInstance();
      if (!(minecraft.screen instanceof AbstractRecipeBookScreen<? extends RecipeBookMenu> screen)) {
         return false;
      }
      int maxGridSlot = screen instanceof InventoryScreen ? 4 : 9;
      for (int slotIdx = 1; slotIdx <= maxGridSlot; slotIdx++) {
         var s = getMod().inventory.getSlot(slotIdx);
         if (!s.isEmpty() && s.count() >= s.stack().getMaxStackSize()) {
            return true;
         }
      }
      return false;
   }

   @Override public boolean canCraftMore() {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player == null) return false;
      Map<RecipeDisplayId, RecipeDisplayEntry> recipes = ((ClientRecipeBookAccessor) player.getRecipeBook()).getKnown();
      RecipeDisplayEntry entry = recipes.get(new RecipeDisplayId(getRecipeId()));
      StackedItemContents contents = new StackedItemContents();
      player.getInventory().fillStackedContents(contents);
      return !entry.canCraft(contents);
   }

   @Override public boolean craft() {
      Minecraft client = Minecraft.getInstance();
      if (!(client.screen instanceof AbstractRecipeBookScreen<? extends RecipeBookMenu>)) return false;
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
//?}
