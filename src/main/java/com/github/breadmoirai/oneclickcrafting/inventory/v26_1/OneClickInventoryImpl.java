//? 26.1 {
/*package com.github.breadmoirai.oneclickcrafting.inventory.v26_1;

import com.github.breadmoirai.oneclickcrafting.inventory.OneClickInventory;
import com.github.breadmoirai.oneclickcrafting.inventory.OneClickInventoryAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class OneClickInventoryImpl extends OneClickInventory {
   @Override
   public void clickSlot(int slotNum, int mouseButton, OneClickInventoryAction action) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player == null) return;
      if (!(minecraft.screen instanceof AbstractContainerScreen<? extends AbstractContainerMenu> containerScreen)) return;
      AbstractContainerMenu menu = containerScreen.getMenu();
      menu.clicked(slotNum, mouseButton, action.mapping(), player);
   }

   @Override
   public int findMatchingSlot(Object ingredient) {
      return 0;
   }

   @Override
   public int findEmptySlot() {
      return 0;
   }
}

*///?}