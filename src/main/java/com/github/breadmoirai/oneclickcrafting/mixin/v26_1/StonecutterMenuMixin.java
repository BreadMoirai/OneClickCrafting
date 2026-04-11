//? 26.1 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v26_1;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class StonecutterMenuMixin {

   @Inject(at = @At("TAIL"), method = "setItem(IILnet/minecraft/world/item/ItemStack;)V")
   public void onSetItem(int slot, int stateId, ItemStack itemStack, CallbackInfo ci) {
      if (!(Minecraft.getInstance().screen instanceof StonecutterScreen)) return;
      if (slot == StonecutterMenu.RESULT_SLOT && !itemStack.isEmpty()) {
         OneClickEvents.RESULT_SLOT_UPDATE.invoker().onResultSlotUpdate(new OneClickItemStack(itemStack));
      }
   }
}
*///?}
