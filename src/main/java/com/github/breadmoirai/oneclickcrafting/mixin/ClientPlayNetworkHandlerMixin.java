package com.github.breadmoirai.oneclickcrafting.mixin;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingClient;
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
        if (packet.getSlot() != 0) return;
        if (packet.getItemStack() == null) return;
        OneClickCraftingClient.getInstance().onResultSlotUpdated(packet.getItemStack());
    }
}
