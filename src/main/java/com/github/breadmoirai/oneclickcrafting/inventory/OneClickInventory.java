package com.github.breadmoirai.oneclickcrafting.inventory;

import java.util.function.Predicate;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.inventory.v21_2.OneClickInventoryImpl;

public abstract class OneClickInventory {

   public static OneClickInventory getInstance() {
      return new OneClickInventoryImpl();
   }

   public abstract void clickSlot(int slotNum,
                                int mouseButton,
                                OneClickInventoryAction action);

   public void leftClickSlot(int slotNum)
   {
      clickSlot(slotNum, 0, OneClickInventoryAction.PICKUP);
   }

   public void moveMatchingIntoSlot(int slotNum)
   {
      clickSlot(slotNum, 0, OneClickInventoryAction.PICKUP);
      clickSlot(slotNum, 0, OneClickInventoryAction.PICKUP_ALL);
      clickSlot(slotNum, 0, OneClickInventoryAction.PICKUP);
   }

   public void rightClickSlot(int slotNum)
   {
      clickSlot(slotNum, 1, OneClickInventoryAction.PICKUP);
   }

   public void shiftClickSlot(int slotNum)
   {
      clickSlot(slotNum, 0, OneClickInventoryAction.QUICK_MOVE);
   }

   public void dropItemsFromCursor()
   {
      clickSlot(-999, 0, OneClickInventoryAction.PICKUP);
   }

   public void dropItem(int slotNum)
   {
      clickSlot(slotNum, 0, OneClickInventoryAction.THROW);
   }

   public void dropStack(int slotNum)
   {
      clickSlot(slotNum, 1, OneClickInventoryAction.THROW);
   }

   public abstract OneClickItemStack getSlot(int slotNum);

   public abstract int findMatchingSlot(Predicate<OneClickItemStack> predicate);
}
