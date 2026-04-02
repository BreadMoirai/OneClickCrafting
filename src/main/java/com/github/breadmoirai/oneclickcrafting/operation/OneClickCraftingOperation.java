package com.github.breadmoirai.oneclickcrafting.operation;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.util.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.display.SlotDisplayContexts;

import java.util.Map;

public class OneClickCraftingOperation extends OneClickOperation {
   private static ItemStack fetchResult(int recipeId) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      ClientWorld world = minecraft.world;
      if (world == null) return null;
      ClientPlayerEntity player = minecraft.player;
      if (player == null) return null;
      Map<NetworkRecipeId, RecipeDisplayEntry> recipes = player.getRecipeBook().recipes;
      return recipes.get(new NetworkRecipeId(recipeId)).display().result().getStacks(SlotDisplayContexts.createParameters(world))
         .getFirst();
   }

   public OneClickCraftingOperation(OneClickCraftingMod mod, int recipeId, int button) {
      super(mod, recipeId, button, fetchResult(recipeId));
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
      Map<NetworkRecipeId, RecipeDisplayEntry> recipes = player.getRecipeBook().recipes;
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
            InventoryUtils.dropStack(gui, 0);
         } else {
            InventoryUtils.dropItem(gui, 0);
         }
      } else {
         InventoryUtils.shiftClickSlot(gui, 0);
      }
      return true;
   }
}
