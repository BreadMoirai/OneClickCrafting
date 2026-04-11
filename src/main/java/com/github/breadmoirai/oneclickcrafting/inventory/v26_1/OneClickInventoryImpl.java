//? 26.1 {
/*package com.github.breadmoirai.oneclickcrafting.inventory.v21_8;

import com.github.breadmoirai.oneclickcrafting.inventory.OneClickInventory;
import com.github.breadmoirai.oneclickcrafting.inventory.OneClickInventoryAction;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public class OneClickInventoryImpl extends OneClickInventory {
   @Nullable
   private static AbstractContainerMenu getMenu() {
      Minecraft minecraft = Minecraft.getInstance();
      if (!(minecraft.screen instanceof AbstractContainerScreen<? extends AbstractContainerMenu> containerScreen))
         return null;
      return containerScreen.getMenu();
   }

   @Override
   public void clickSlot(int slotNum, int mouseButton, OneClickInventoryAction action) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player == null) return;
      if (minecraft.gameMode == null) return;
      AbstractContainerMenu menu = getMenu();
      if (menu == null) return;
      minecraft.gameMode.handleClickType(menu.containerId, slotNum, mouseButton, action.mapping(), player);
   }

   @Override
   public OneClickItemStack getSlot(int slotNum) {
      AbstractContainerMenu menu = getMenu();
      if (menu == null) {
         return null;
      }
      Slot slot = menu.getSlot(slotNum);
      return new OneClickItemStack(slot.getItem());
   }

   @Override
   public int findMatchingSlot(Predicate<OneClickItemStack> predicate) {
      AbstractContainerMenu menu = getMenu();
      if (menu == null) return -1;
      NonNullList<Slot> slots = menu.slots;
      for (int i = 0; i < slots.size(); i++) {
         Slot slot = menu.getSlot(i);
         if (!(slot.container instanceof Inventory)) continue;
         if (!predicate.test(new OneClickItemStack(slot.getItem()))) continue;
         return slot.index;
      }
      return -1;
   }

}

*///?}