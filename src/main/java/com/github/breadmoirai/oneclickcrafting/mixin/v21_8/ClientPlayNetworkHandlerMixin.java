//? >=1.21.7 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v21_8;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {

   @Inject(at = @At("TAIL"), method = "handleContainerSetSlot(Lnet/minecraft/network/protocol/game/ClientboundContainerSetSlotPacket;)V")
   private void onScreenHandlerSlotUpdate(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
      Screen screen = Minecraft.getInstance().screen;
      if (screen instanceof CraftingScreen || screen instanceof InventoryScreen) {
         if (packet.getSlot() == 0)
            OneClickEvents.RESULT_SLOT_UPDATE.invoker().onResultSlotUpdate(new OneClickItemStack(packet.getItem()));
      } else if (screen instanceof StonecutterScreen) {
         if (packet.getSlot() == 1)
            OneClickEvents.RESULT_SLOT_UPDATE.invoker().onResultSlotUpdate(new OneClickItemStack(packet.getItem()));
      }
   }
}
*///?}
