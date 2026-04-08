package com.github.breadmoirai.oneclickcrafting.inventory;

//? 26.1 {
import net.minecraft.world.inventory.ContainerInput;
//?} >=1.21.10 <=1.21.11 {
/*import net.minecraft.screen.slot.SlotActionType;
*///?}

public enum OneClickInventoryAction {
   PICKUP,
   QUICK_MOVE,
   SWAP,
   CLONE,
   THROW,
   QUICK_CRAFT,
   PICKUP_ALL;

//? 26.1 {
   public ContainerInput mapping() {
      return ContainerInput.valueOf(this.name());
   }
//?} >=1.21.10 <=1.21.11 {
   /*public SlotActionType mapping() {
      return SlotActionType.valueOf(this.name());
   }
*///?}
   
   
   
}
