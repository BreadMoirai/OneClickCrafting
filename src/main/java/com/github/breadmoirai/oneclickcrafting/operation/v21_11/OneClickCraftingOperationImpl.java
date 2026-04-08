//? >=1.21.10 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.operation.v21_11;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.ClientRecipeBookAccessor;
import com.github.breadmoirai.oneclickcrafting.operation.OneClickCraftingOperation;
import com.github.breadmoirai.oneclickcrafting.operation.OneClickOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;

import java.util.Map;

public class OneClickCraftingOperationImpl extends OneClickCraftingOperation {

   public OneClickCraftingOperationImpl(OneClickCraftingMod mod, int recipeId, int button) {
      super(mod, recipeId, button, mod.recipeBook.recipeResult(recipeId));
   }

   @Override public boolean anySlotMax() {
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

   @Override public boolean canCraftMore() {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      ClientPlayerEntity player = minecraft.player;
      if (player == null) return false;
      Map<NetworkRecipeId, RecipeDisplayEntry> recipes = ((ClientRecipeBookAccessor) player.getRecipeBook()).getRecipes();
      RecipeDisplayEntry entry = recipes.get(new NetworkRecipeId(getRecipeId()));
      RecipeFinder finder = new RecipeFinder();
      player.getInventory().populateRecipeFinder(finder);
      return !entry.isCraftable(finder);
   }

   @Override public boolean craft() {
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

*///?}