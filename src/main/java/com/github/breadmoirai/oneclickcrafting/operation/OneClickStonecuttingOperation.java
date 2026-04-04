package com.github.breadmoirai.oneclickcrafting.operation;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.slot.Slot;

import java.util.Optional;

public class OneClickStonecuttingOperation extends OneClickOperation {
   private final Ingredient ingredient;
   private Runnable onNextUpdate;

   public OneClickStonecuttingOperation(OneClickCraftingMod mod, int recipeId, OneClickItemStack result, Ingredient ingredient, int button) {
      super(mod, recipeId, button, result);
      this.ingredient = ingredient;
   }

   @Override
   protected boolean checkEnabled() {
      if (!getMod().config.isEnableStonecutter()) return false;
      return super.checkEnabled();
   }

   @Override
   public boolean craft() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.interactionManager == null) return false;
      if (!(client.currentScreen instanceof HandledScreen<?> gui)) return false;

      if (onNextUpdate != null) {
         debug("craft(stonecutter): running deferred action");
         Runnable next = onNextUpdate;
         onNextUpdate = null;
         next.run();
         return true;
      }

      OneClickItemStack input = getMod().inventory.getSlot(0);
      debug("craft(stonecutter): drop=" + isDrop() + " shift=" + isShift() + " input.count=" + input.getCount());

      if (isDrop()) {
         if (isShift()) {
            if (input.getCount() != 64) {
               debug("craft(stonecutter): stacking to 64, deferring drop+refill");
               getMod().inventory.moveMatchingIntoSlot(0);
               gui.getScreenHandler().onButtonClick(client.player, getRecipeId());
               client.interactionManager.clickButton(gui.getScreenHandler().syncId, getRecipeId());
               onNextUpdate = () -> {
                  getMod().inventory.dropStack(1);
                  refill();
               };
               return false;
            } else {
               debug("craft(stonecutter): drop stack (slot 1) + refill");
               getMod().inventory.dropStack(1);
               refill();
               return true;
            }
         } else {
            boolean shouldRefill = input.getCount() == 1;
            debug("craft(stonecutter): drop item (slot 1), shouldRefill=" + shouldRefill);
            getMod().inventory.dropItem(1);
            getMod().inventory.leftClickSlot(0);
            getMod().inventory.leftClickSlot(0);
            if (shouldRefill) {
               refill();
               return true;
            }
         }
      } else {
         if (isShift()) {
            if (input.getCount() != 64) {
               debug("craft(stonecutter): stacking to 64, deferring shift-click+refill");
               getMod().inventory.moveMatchingIntoSlot(0);
               gui.getScreenHandler().onButtonClick(client.player, getRecipeId());
               client.interactionManager.clickButton(gui.getScreenHandler().syncId, getRecipeId());
               onNextUpdate = () -> {
                  getMod().inventory.shiftClickSlot(1);
                  refill();
               };
               return false;
            } else {
               debug("craft(stonecutter): shift-click slot 1 + refill");
               getMod().inventory.shiftClickSlot(1);
               refill();
               return true;
            }
         } else {
            if (input.getCount() != 1) {
               debug("craft(stonecutter): isolating single input, deferring shift-click");
               getMod().inventory.leftClickSlot(0);
               getMod().inventory.rightClickSlot(0);
               gui.getScreenHandler().onButtonClick(client.player, getRecipeId());
               client.interactionManager.clickButton(gui.getScreenHandler().syncId, getRecipeId());
               onNextUpdate = () -> {
                  getMod().inventory.shiftClickSlot(1);
                  getMod().inventory.leftClickSlot(0);
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
      return false;
   }

   private void refill() {
      int slot = getMod().inventory.findMatchingSlot(ingredient);
      if (slot == -1) return;
      boolean multi = getMod().inventory.getSlot(slot).getCount() > 1;
      getMod().inventory.leftClickSlot(slot);
      getMod().inventory.rightClickSlot(0);
      if (multi) {
         getMod().inventory.leftClickSlot(slot);
      }
   }
}
