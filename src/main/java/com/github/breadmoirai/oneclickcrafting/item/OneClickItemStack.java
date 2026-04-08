package com.github.breadmoirai.oneclickcrafting.item;
//? 26.1 {
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
//?} >=1.21.10 <=1.21.11 {
/*import net.minecraft.item.ItemStack;

public record OneClickItemStack(ItemStack stack) {

   public static OneClickItemStack EMPTY = new OneClickItemStack(ItemStack.EMPTY);

   public boolean matches(OneClickItemStack other) {
      return ItemStack.areItemsEqual(stack, other.stack);
   }

   public boolean isEmpty() {
      return stack.isEmpty();
   }

   public int count() {
      return stack.getCount();
   }

   public int getMaxCount() { return stack.getMaxCount(); }
}
*///?}

