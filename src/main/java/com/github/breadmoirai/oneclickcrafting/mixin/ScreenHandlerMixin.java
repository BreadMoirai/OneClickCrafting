package com.github.breadmoirai.oneclickcrafting.mixin;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Shadow public abstract void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player);

    @Shadow public abstract OptionalInt getSlotIndex(Inventory inventory, int index);

    @Inject(at = @At("TAIL"), method = "setStackInSlot(IILnet/minecraft/item/ItemStack;)V")
    private void setStackInSlot(int slot, int revision, ItemStack stack, CallbackInfo ci) {
        if (slot != 0 || stack.getItem().equals(Items.AIR)) {
            System.out.println("slot = " + slot);
            System.out.println("stack = " + stack.getItem().getName().getString());
            return;
        }

        if (OneClickCraftingClient.lastCraft == null)
            return;
        if (OneClickCraftingClient.lastCraft.isItemEqual(stack)) {
            ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
            if (interactionManager != null) {
                MinecraftClient.getInstance().interactionManager.clickSlot(0, 0, 0, SlotActionType.QUICK_MOVE, MinecraftClient.getInstance().player);
            }

            OneClickCraftingClient.lastCraft = null;
        }
    }
}
