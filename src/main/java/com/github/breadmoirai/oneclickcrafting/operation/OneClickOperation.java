package com.github.breadmoirai.oneclickcrafting.operation;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public abstract class OneClickOperation {
   private final OneClickCraftingMod mod;
   private final int recipeId;
   private final int button;
   private final boolean isDrop;
   private final boolean isShift;
   private final ItemStack result;
   private final boolean isValid;

   public OneClickOperation(OneClickCraftingMod mod, int recipeId, int button, ItemStack result) {
      this.mod = mod;
      this.recipeId = recipeId;
      this.button = button;
      this.result = result;
      this.isDrop = mod.config.isDropEnable() && mod.input.drop.isDown();
      this.isShift = mod.input.isShiftDown();
      this.isValid = checkEnabled();
   }

   protected boolean checkEnabled() {
      if (result.getItem() == Items.AIR) return false;
      if (button == 0 && !mod.config.isEnableLeftClick()) {
         return false;
      } else if (button == 1 && !mod.config.isEnableRightClick()) {
         return false;
      } else if (button == -1) {
         return false;
      }
      boolean alwaysOn = mod.config.isAlwaysOn();
      if (mod.config.isCtrlHold() && mod.input.isControlDown()) return !alwaysOn;
      if (mod.config.isAltHold() && mod.input.isAltDown()) return !alwaysOn;
      if (mod.input.toggleHold.isDown()) return !alwaysOn;
      return alwaysOn;
   }

   public boolean checkOutput(ItemStack output) {
      return ItemStack.areItemsEqual(result, output);
   }

   public boolean shouldWaitForResultSlotUpdate() {
      return true;
   }

   public boolean notValid() {
      return !isValid;
   }

   public OneClickCraftingMod getMod() {
      return mod;
   }

   public int getRecipeId() {
      return recipeId;
   }

   public int getButton() {
      return button;
   }

   public boolean isDrop() {
      return isDrop;
   }

   public boolean isShift() {
      return isShift;
   }

   public ItemStack getResult() {
      return result;
   }

   public abstract boolean craft();
}
