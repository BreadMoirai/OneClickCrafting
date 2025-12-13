package com.github.breadmoirai.oneclickcrafting.mixin;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

   @Inject(at = @At("TAIL"), method = "onScreenHandlerSlotUpdate(Lnet/minecraft/network/packet/s2c/play/ScreenHandlerSlotUpdateS2CPacket;)V")
   private void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
      Screen screen = MinecraftClient.getInstance().currentScreen;
      OneClickCraftingClient client = OneClickCraftingClient.getInstance();
      if (screen instanceof CraftingScreen || screen instanceof InventoryScreen) {
         if (packet.getSlot() == 0 && packet.getStack() != null)
            client.craftingHandler.onResultSlotUpdated(packet.getStack());
      } else if (screen instanceof StonecutterScreen) {
          if (packet.getSlot() == 1 && packet.getStack() != null)
              client.stonecuttingHandler.onResultSlotUpdated(packet.getStack());
      }
   }
}
