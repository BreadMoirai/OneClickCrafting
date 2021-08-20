package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.mixin.KeyBindingAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.SlotActionType;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class OneClickCraftingClient implements ClientModInitializer {

    public static ItemStack lastCraft;
    private static boolean isDropping;
    private static OneClickCraftingConfig config;

    @Override
    public void onInitializeClient() {
        OneClickCraftingConfig.loadModConfig();
        config = OneClickCraftingConfig.getInstance();
    }

    public static void recipeClicked(Recipe<?> recipe) {
        if (config.isAlwaysOn()) {
            if (config.isAltHold() && Screen.hasAltDown()) return;
            if (config.isCtrlHold() && Screen.hasControlDown()) return;
            isDropping = config.isDropEnable() && isDropPressed() && !Screen.hasShiftDown();
            lastCraft = recipe.getOutput();
        } else if ((config.isAltHold() && Screen.hasAltDown()) ||
                (config.isAltHold() && Screen.hasControlDown())) {
            isDropping = config.isDropEnable() && isDropPressed() && !Screen.hasShiftDown();
            lastCraft = recipe.getOutput();
        }
    }

    private static boolean isDropPressed() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), ((KeyBindingAccessor) MinecraftClient.getInstance().options.keyDrop).getBoundKey().getCode());
    }

    public static void onResultSlotUpdated(ItemStack itemStack) {
        if (lastCraft == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.interactionManager == null) return;
        if (!(client.currentScreen instanceof HandledScreen)) return;
        if (!itemStack.isItemEqual(OneClickCraftingClient.lastCraft)) return;
        int syncId = ((HandledScreen<?>) client.currentScreen).getScreenHandler().syncId;
        client.interactionManager.clickSlot(syncId, 0, 0, isDropping ? SlotActionType.THROW : SlotActionType.QUICK_MOVE, client.player);
        OneClickCraftingClient.lastCraft = null;
    }
}
