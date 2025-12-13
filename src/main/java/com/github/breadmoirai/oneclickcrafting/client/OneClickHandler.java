package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.util.InputHelper;
import net.minecraft.item.ItemStack;

public abstract class OneClickHandler {

   protected ItemStack lastCraft;
   protected boolean isDropping;
   protected boolean isShiftDropping;
   protected int lastButton;

   public abstract void onInitialize();

   public void reset() {
      isDropping = false;
      isShiftDropping = false;
      lastCraft = null;
      lastButton = -1;
   }

   public boolean isEnabled() {
      OneClickCraftingClient client = OneClickCraftingClient.getInstance();
      if (!InputHelper.isKeybindingPressed(client.repeatLastKey)) {
         if (lastButton == 0 && !client.config.isEnableLeftClick()) {
            return false;
         } else if (lastButton == 1 && !client.config.isEnableRightClick()) {
            return false;
         } else if (lastButton == -1) {
            return false;
         }
      }
      boolean alwaysOn = client.config.isAlwaysOn();
      if (client.config.isCtrlHold() && InputHelper.isControlDown()) return !alwaysOn;
      if (client.config.isAltHold() && InputHelper.isAltDown()) return !alwaysOn;
      if (!client.toggleHoldKey.isUnbound() && InputHelper.isKeybindingPressed(client.toggleHoldKey)) return !alwaysOn;
      return alwaysOn;
   }

   public abstract void onResultSlotUpdated(ItemStack slot);

   public void setLastButton(int i) {
      this.lastButton = i;
   }

   public void setLastCraft(ItemStack stack) {
      this.lastCraft = stack;
   }
}
