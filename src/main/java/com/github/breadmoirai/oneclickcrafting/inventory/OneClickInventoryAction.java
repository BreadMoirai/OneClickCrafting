package com.github.breadmoirai.oneclickcrafting.inventory;

import net.minecraft.world.inventory.ContainerInput;

public enum OneClickInventoryAction {
   PICKUP,
   QUICK_MOVE,
   SWAP,
   CLONE,
   THROW,
   QUICK_CRAFT,
   PICKUP_ALL;

   public ContainerInput mapping() {
      return ContainerInput.valueOf(this.name());
   }
   
   
   
}
