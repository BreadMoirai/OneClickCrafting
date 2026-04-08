//? >=1.21.10 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v21_11;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
   @Invoker
   void callOnMouseClick(Slot slot, int slotNum, int mouseButton, SlotActionType action);
}
*///?}
