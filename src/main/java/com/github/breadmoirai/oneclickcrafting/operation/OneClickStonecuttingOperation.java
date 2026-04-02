package com.github.breadmoirai.oneclickcrafting.operation;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.util.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.slot.Slot;

import java.util.Optional;

public class OneClickStonecuttingOperation extends OneClickOperation {
   private final Ingredient ingredient;
   private Runnable onNextUpdate;

   public OneClickStonecuttingOperation(OneClickCraftingMod mod, int recipeId, ItemStack result, Ingredient ingredient, int button) {
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
         Runnable next = onNextUpdate;
         onNextUpdate = null;
         next.run();
         return true;
      }

      Slot input = InventoryUtils.getSlot(gui, 0);

      if (isDrop()) {
         if (isShift()) {
            if (input.getStack().getCount() != 64) {
               InventoryUtils.moveMatchingIntoSlot(gui, 0);
               gui.getScreenHandler().onButtonClick(client.player, getRecipeId());
               client.interactionManager.clickButton(gui.getScreenHandler().syncId, getRecipeId());
               onNextUpdate = () -> {
                  InventoryUtils.dropStack(gui, 1);
                  refill(gui);
               };
               return false;
            } else {
               InventoryUtils.dropStack(gui, 1);
               refill(gui);
               return true;
            }
         } else {
            boolean shouldRefill = input.getStack().getCount() == 1;
            InventoryUtils.dropItem(gui, 1);
            InventoryUtils.leftClickSlot(gui, 0);
            InventoryUtils.leftClickSlot(gui, 0);
            if (shouldRefill) {
               refill(gui);
               return true;
            }
         }
      } else {
         if (isShift()) {
            if (input.getStack().getCount() != 64) {
               InventoryUtils.moveMatchingIntoSlot(gui, 0);
               gui.getScreenHandler().onButtonClick(client.player, getRecipeId());
               client.interactionManager.clickButton(gui.getScreenHandler().syncId, getRecipeId());
               onNextUpdate = () -> {
                  InventoryUtils.shiftClickSlot(gui, 1);
                  refill(gui);
               };
               return false;
            } else {
               InventoryUtils.shiftClickSlot(gui, 1);
               refill(gui);
               return true;
            }
         } else {
            if (input.getStack().getCount() != 1) {
               InventoryUtils.leftClickSlot(gui, 0);
               InventoryUtils.rightClickSlot(gui, 0);
               gui.getScreenHandler().onButtonClick(client.player, getRecipeId());
               client.interactionManager.clickButton(gui.getScreenHandler().syncId, getRecipeId());
               onNextUpdate = () -> {
                  InventoryUtils.shiftClickSlot(gui, 1);
                  InventoryUtils.leftClickSlot(gui, 0);
               };
               return false;
            } else {
               InventoryUtils.shiftClickSlot(gui, 1);
               refill(gui);
               return true;
            }
         }
      }
      return false;
   }

   private void refill(HandledScreen<?> gui) {
      Optional<Slot> slot = InventoryUtils.findMatchingSlot(gui, ingredient);
      slot.ifPresent(s -> {
         boolean multi = s.getStack().getCount() > 1;
         InventoryUtils.leftClickSlot(gui, s);
         InventoryUtils.rightClickSlot(gui, 0);
         if (multi) {
            InventoryUtils.leftClickSlot(gui, s);
         }
      });
   }
}
