package com.github.breadmoirai.oneclickcrafting.operation;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.ClientRecipeBookAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;

import java.util.Map;

public class OneClickCraftingOperation extends OneClickOperation {

   public OneClickCraftingOperation(OneClickCraftingMod mod, int recipeId, int button) {
      super(mod, recipeId, button, mod.recipeBook.recipeResult(recipeId));
   }

   @Override
   public boolean shouldWaitForResultSlotUpdate() {
      // https://github.com/BreadMoirai/OneClickCrafting/issues/25
      // Edge case handling for when
      // the server's InputSlotFiller.fill() returns early (no inventory changes,
      // no onContentChanged, no ScreenHandlerSlotUpdateS2CPacket for slot 0) only when:
      //     – the grid already matches the clicked recipe AND the player's inventory
      //       has no additional matching ingredients (countCrafts == 1, limited to
      //       whatever is already in the grid).
      // We therefore only call onResultSlotUpdated manually for this case, detected by
      // checking that the player's main inventory alone (without grid items) cannot
      // satisfy the recipe's crafting requirements — meaning no extra ingredients exist.
      //
      // This applies to both the 2×2 inventory grid (InventoryScreen, slots 1–4) and
      // the 3×3 crafting table grid (CraftingScreen, slots 1–9).
      return !(canCraftMore() || anySlotMax());
   }

   private boolean anySlotMax() {
      //   (b) slotCount >= maxCount — the slot is already at max stack size,
      //       so nothing more can be added regardless of inventory contents.
      MinecraftClient minecraft = MinecraftClient.getInstance();
      if (!(minecraft.currentScreen instanceof HandledScreen<?> screen)) {
         return false;
      }
      int maxGridSlot = screen instanceof InventoryScreen ? 4 : 9;
      for (int slotIdx = 1; slotIdx <= maxGridSlot; slotIdx++) {
         ItemStack s = screen.getScreenHandler().getSlot(slotIdx).getStack();
         if (!s.isEmpty() && s.getCount() >= s.getMaxCount()) {
            return true;
         }
      }
      return false;
   }

   private boolean canCraftMore() {
      //   (a) countCrafts <= slotCount — inventory has no extra ingredients;
      //       detected by checking the inventory alone can't satisfy the recipe.
      MinecraftClient minecraft = MinecraftClient.getInstance();
      ClientPlayerEntity player = minecraft.player;
      if (player == null) return false;
      Map<NetworkRecipeId, RecipeDisplayEntry> recipes = ((ClientRecipeBookAccessor) player.getRecipeBook()).getRecipes();
      RecipeDisplayEntry entry = recipes.get(new NetworkRecipeId(getRecipeId()));
      RecipeFinder finder = new RecipeFinder();
      player.getInventory().populateRecipeFinder(finder);
      return !entry.isCraftable(finder);
   }

   @Override
   public boolean craft() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.interactionManager == null) return false;
      if (!(client.currentScreen instanceof HandledScreen<?> gui)) return false;
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
