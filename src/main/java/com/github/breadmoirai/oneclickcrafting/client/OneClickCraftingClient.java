package com.github.breadmoirai.oneclickcrafting.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class OneClickCraftingClient implements ClientModInitializer {

    public static ItemStack lastCraft;

    @Override
    public void onInitializeClient() {

    }

    public static void recipeClicked(Recipe<?> recipe) {
        if (Screen.hasAltDown() || Screen.hasControlDown()) return;
        lastCraft = recipe.getOutput();
    }

    public static void onResultSlotUpdated(ItemStack itemStack) {
        if (lastCraft == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.interactionManager == null) return;
        if (!(client.currentScreen instanceof HandledScreen)) return;
        if (!itemStack.isItemEqual(OneClickCraftingClient.lastCraft)) return;
        int syncId = ((HandledScreen<?>) client.currentScreen).getScreenHandler().syncId;
        client.interactionManager.clickSlot(syncId, 0, 0, SlotActionType.QUICK_MOVE, client.player);
        OneClickCraftingClient.lastCraft = null;
    }
}
