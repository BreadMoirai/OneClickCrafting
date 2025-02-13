package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class OneClickCraftingClient implements ClientModInitializer {

    private static OneClickCraftingClient INSTANCE;

    public ItemStack lastCraft;
    private boolean isDropping;
    private boolean isShiftDropping;
    private boolean startedDropCrafting;
    private int lastButton;
    private OneClickCraftingConfig config;
    private KeyBinding toggleHoldKey;
    private KeyBinding repeatLastKey;
    private RecipeResultCollection lastResult;
    private NetworkRecipeId lastRecipe;


    public static OneClickCraftingClient getInstance() {
        return INSTANCE;
    }

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        OneClickCraftingConfig.loadModConfig();
        config = OneClickCraftingConfig.getInstance();
        toggleHoldKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.oneclickcrafting.toggle_hold",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.oneclickcrafting.keybindings"
        ));
        repeatLastKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.oneclickcrafting.repeat_last",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.oneclickcrafting.keybindings"
        ));
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof InventoryScreen || screen instanceof CraftingScreen) {
                ScreenKeyboardEvents.afterKeyPress(screen).register((screen2, key, scancode, modifiers) -> {
                    if (isKeybindingPressed(repeatLastKey)) {
                        RecipeBookScreen<?> recipeBookScreen = (RecipeBookScreen<?>) screen;
                        if (repeatLastCraft(recipeBookScreen)) {
                            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        }
                    }
                });
                ScreenEvents.remove(screen).register(screen1 -> reset());
            }
        });
        reset();
    }

    public boolean repeatLastCraft(RecipeBookScreen<?> screen) {
        if (startedDropCrafting) return false;
        if (lastRecipe == null) return false;
        if (config.isEnableLeftClick()) {
            setLastButton(0);
        } else if (config.isEnableRightClick()) {
            setLastButton(1);
        }
        screen.recipeBook.selectedRecipe = lastRecipe;
        return screen.recipeBook.select(lastResult, lastRecipe);
    }

    public void setLastButton(int lastButton) {
        this.lastButton = lastButton;
    }

    public void recipeClicked(RecipeResultCollection results, NetworkRecipeId recipe) {
        lastResult = results;
        lastRecipe = recipe;
        if (isEnabled()) {
            isDropping = config.isDropEnable() && isDropPressed();
            isShiftDropping = isDropping && Screen.hasShiftDown();
            MinecraftClient client = MinecraftClient.getInstance();
            Map<NetworkRecipeId, RecipeDisplayEntry> recipes = client.player.getRecipeBook().recipes;
            lastCraft = recipes.get(recipe).display().result().getStacks(SlotDisplayContexts.createParameters(client.world)).getFirst();
            System.out.println("lastCraft = " + lastCraft.getItem().getName());
        } else {
            reset();
        }
    }

    private void reset() {
        isDropping = false;
        isShiftDropping = false;
        lastCraft = null;
        startedDropCrafting = false;
        lastButton = -1;
    }


    private boolean isEnabled() {
        if ((lastButton == 0 && !config.isEnableLeftClick()) || (lastButton == 1 && !config.isEnableRightClick())) {
            return false;
        }
        boolean alwaysOn = config.isAlwaysOn();
        if (config.isCtrlHold() && Screen.hasControlDown()) return !alwaysOn;
        if (config.isAltHold() && Screen.hasAltDown()) return !alwaysOn;
        if (!toggleHoldKey.isUnbound() && isToggleHoldPressed()) return !alwaysOn;
        return alwaysOn;
    }

    private boolean isToggleHoldPressed() {
        return isKeybindingPressed(toggleHoldKey);
    }

    private boolean isDropPressed() {
        return isKeybindingPressed(MinecraftClient.getInstance().options.dropKey);
    }

    private boolean isKeybindingPressed(KeyBinding keyBinding) {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyBinding.boundKey.getCode());
    }

    public void onResultSlotUpdated(ItemStack itemStack) {
        if (lastCraft == null) return;
        if (itemStack.getItem() == Items.AIR) {
            if (startedDropCrafting) {
                reset();
            }
            return;
        }
        if (!ItemStack.areItemsEqual(itemStack, lastCraft)) {
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
