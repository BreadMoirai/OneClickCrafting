package com.github.breadmoirai.oneclickcrafting.inventory;

import net.minecraft.world.inventory.ClickType;

public enum OneClickInventoryAction {
   PICKUP,
   QUICK_MOVE,
   SWAP,
   CLONE,
   THROW,
   QUICK_CRAFT,
   PICKUP_ALL;

   public ClickType mapping() {
      return ClickType.valueOf(this.name());
   }
   
   
   
}
