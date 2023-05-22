package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.mixin.KeyBindingAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class OneClickCraftingClient implements ClientModInitializer {

    private static OneClickCraftingClient INSTANCE;

    public ItemStack lastCraft;
    private boolean isDropping;
    private boolean isShiftDropping;
    private boolean startedDropCrafting;
    private OneClickCraftingConfig config;
    private KeyBinding toggleHold;

    public static OneClickCraftingClient getInstance() {
        return INSTANCE;
    }

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        OneClickCraftingConfig.loadModConfig();
        config = OneClickCraftingConfig.getInstance();
        toggleHold = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.oneclickcrafting.toggle_hold",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.oneclickcrafting.keybindings"
        ));
    }

    public void recipeClicked(Recipe<?> recipe) {
        System.out.println("recipe clicked " + recipe.getId());
        System.out.println("enabled = " + isEnabled());
        if (isEnabled()) {
            isDropping = config.isDropEnable() && isDropPressed();
            isShiftDropping = isDropping && Screen.hasShiftDown();
            lastCraft = recipe.getOutput(MinecraftClient.getInstance().world.getRegistryManager());
        } else {
            isDropping = false;
            isShiftDropping = false;
            lastCraft = null;
        }
    }

    private boolean isEnabled() {
        boolean alwaysOn = config.isAlwaysOn();
        if (config.isCtrlHold() && Screen.hasControlDown()) return !alwaysOn;
        if (config.isAltHold() && Screen.hasAltDown()) return !alwaysOn;
        if (!toggleHold.isUnbound() && isToggleHoldPressed()) return !alwaysOn;
        return alwaysOn;
    }

    private boolean isToggleHoldPressed() {
        return isKeybindingPressed(toggleHold);
    }

    private boolean isDropPressed() {
        return isKeybindingPressed(MinecraftClient.getInstance().options.dropKey);
    }

    private boolean isKeybindingPressed(KeyBinding keyBinding) {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), ((KeyBindingAccessor) keyBinding).getBoundKey().getCode());
    }

    public void onResultSlotUpdated(ItemStack itemStack) {
        System.out.println("Result Slot Updated with " + itemStack);
        if (lastCraft == null) return;
        if (itemStack.getItem() == Items.AIR) {
            if (startedDropCrafting) {
                isDropping = false;
                startedDropCrafting = false;
                lastCraft = null;
            }
            return;
        }
        if (!itemStack.isItemEqual(lastCraft)) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.interactionManager == null) return;
        if (!(client.currentScreen instanceof HandledScreen)) return;
        int syncId = ((HandledScreen<?>) client.currentScreen).getScreenHandler().syncId;
        if (isDropping) {
            client.interactionManager.clickSlot(syncId, 0, 0, SlotActionType.THROW, client.player);
            if (isShiftDropping) {
                startedDropCrafting = true;
                isShiftDropping = false;
            }
        } else {
            client.interactionManager.clickSlot(syncId, 0, 0, SlotActionType.QUICK_MOVE, client.player);
            lastCraft = null;
        }
    }
}
