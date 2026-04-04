//? >=1.21.10 <=1.21.11 {
package com.github.breadmoirai.oneclickcrafting.inventory.v21_11;

import com.github.breadmoirai.oneclickcrafting.inventory.OneClickInventory;
import com.github.breadmoirai.oneclickcrafting.inventory.OneClickInventoryAction;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

import java.util.Optional;

public class OneClickInventoryImpl extends OneClickInventory {

   private static HandledScreen<?> getScreenHandler() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.interactionManager == null) return null;
      if (!(client.currentScreen instanceof HandledScreen<?> gui)) return null;
      return gui;
   }

   @Override
   public void clickSlot(int slotNum, int mouseButton, OneClickInventoryAction action) {
      HandledScreen<?> gui = getScreenHandler();
      if (gui == null) return;
      if (slotNum >= 0 && slotNum < gui.getScreenHandler().slots.size())
      {
         Slot slot = gui.getScreenHandler().getSlot(slotNum);
         gui.onMouseClick(slot, slotNum, mouseButton, action.mapping());
      }
      else
      {
         MinecraftClient mc = MinecraftClient.getInstance();
         ClientPlayerInteractionManager interactionManager = mc.interactionManager;
         if (interactionManager != null) {
            interactionManager.clickSlot(gui.getScreenHandler().syncId, slotNum, mouseButton, action.mapping(), mc.player);
         }
      }
   }

   @Override
   public OneClickItemStack getSlot(int slotNum) {
      HandledScreen<?> gui = getScreenHandler();
      if (gui == null) return null;
      return new OneClickItemStack(gui.getScreenHandler().getSlot(slotNum).getStack());
   }

   @Override
   public int findMatchingSlot(Object ingredient)
   {
      HandledScreen<?> gui = getScreenHandler();
      if (gui == null) return -1;
      DefaultedList<Slot> slots = gui.getScreenHandler().slots;
      for (Slot slot : slots) {
         if (!(slot.inventory instanceof PlayerInventory)) continue;
         if (!Ingredient.matches(Optional.of(((Ingredient) ingredient)), slot.getStack())) continue;
         return slot.getIndex();
      }
      return -1;
   }

   @Override
   public int findEmptySlot()
   {
      HandledScreen<?> gui = getScreenHandler();
      if (gui == null) return -1;
      DefaultedList<Slot> slots = gui.getScreenHandler().slots;
      for (Slot slot : slots) {
         if (!(slot.inventory instanceof PlayerInventory)) continue;
         if (!slot.getStack().isOf(Items.AIR)) continue;
         return slot.getIndex();
      }
      return -1;
   }

}

//?}