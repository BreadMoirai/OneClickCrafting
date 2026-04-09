package com.github.breadmoirai.oneclickcrafting.item;

import net.minecraft.world.item.ItemStack;

public record OneClickItemStack(ItemStack stack) {
   public static OneClickItemStack EMPTY = new OneClickItemStack(ItemStack.EMPTY);

   public boolean matches(OneClickItemStack other) {
      return ItemStack.isSameItem(stack, other.stack);
   }

   public boolean isEmpty() {
      return stack.isEmpty();
   }

   public int count() {
      return stack.getCount();
   }

   public int getMaxCount() { return stack.getMaxStackSize(); }
}

