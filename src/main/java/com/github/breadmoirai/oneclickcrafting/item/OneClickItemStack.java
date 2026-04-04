package com.github.breadmoirai.oneclickcrafting.item;
//? 26.1 {
/*import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record OneClickItemStack(ItemStack stack) {

   public boolean matches(OneClickItemStack other) {
      return ItemStack.isSameItem(stack, other.stack);
   }

   public boolean isAir() {
      return stack.is(Items.AIR);
   }

   public int getCount() {
      return stack.getCount();
   }
}
*///?} >=1.21.10 <=1.21.11 {
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public record OneClickItemStack(ItemStack stack) {

   public boolean matches(OneClickItemStack other) {
      return ItemStack.areItemsEqual(stack, other.stack);
   }

   public boolean isAir() {
      return stack.isOf(Items.AIR);
   }

   public int getCount() {
      return stack.getCount();
   }
}
//?}

