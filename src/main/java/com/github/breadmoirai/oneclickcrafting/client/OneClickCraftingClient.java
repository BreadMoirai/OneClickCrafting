package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.util.InputHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class OneClickCraftingClient implements ClientModInitializer {

   private static OneClickCraftingClient INSTANCE;

   public ItemStack lastCraft;
   private boolean isDropping;
   private boolean isShiftDropping;
   private int lastButton;
   private OneClickCraftingConfig config;
   private KeyBinding toggleHoldKey;
   private KeyBinding repeatLastKey;


   public static OneClickCraftingClient getInstance() {
      return INSTANCE;
   }

   @Override
   public void onInitializeClient() {
      INSTANCE = this;
      OneClickCraftingConfig.loadModConfig();
      config = OneClickCraftingConfig.getInstance();
      KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("category.oneclickcrafting.keybindings"));
      toggleHoldKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
         "key.oneclickcrafting.toggle_hold",
         InputUtil.Type.KEYSYM,
         InputUtil.UNKNOWN_KEY.getCode(),
         category
      ));
      repeatLastKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
         "key.oneclickcrafting.repeat_last",
         InputUtil.Type.KEYSYM,
         InputUtil.UNKNOWN_KEY.getCode(),
         category
      ));
      ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
         if (screen instanceof InventoryScreen || screen instanceof CraftingScreen) {
            ScreenKeyboardEvents.afterKeyPress(screen).register((screen2, key) -> {
               RecipeBookWidget<?> recipeBook = ((RecipeBookScreen<?>) screen).recipeBook;
               if (isKeybindingPressed(repeatLastKey) && !InputHelper.isToggleKey(key) && recipeBook.selectedRecipeResults != null && recipeBook.selectedRecipe != null)
                  recipeBook.select(recipeBook.selectedRecipeResults, recipeBook.selectedRecipe, InputHelper.isShiftDown());
            });
            ScreenEvents.remove(screen).register(screen1 -> reset());
         }
      });
      reset();
   }

   public void setLastButton(int lastButton) {
      this.lastButton = lastButton;
   }

   public void recipeClicked(NetworkRecipeId recipe) {
      System.out.println(this.lastButton);
      if (!isEnabled()) {
         reset();
         return;
      }
      isDropping = config.isDropEnable() && isDropPressed();
      isShiftDropping = isDropping && InputHelper.isShiftDown();
      MinecraftClient client = MinecraftClient.getInstance();
      ClientWorld world = client.world;
      if (world == null) return;
      ClientPlayerEntity player = client.player;
      if (player == null) return;
      Map<NetworkRecipeId, RecipeDisplayEntry> recipes = player.getRecipeBook().recipes;
      lastCraft = recipes.get(recipe).display().result().getStacks(SlotDisplayContexts.createParameters(world))
         .getFirst();
   }

   private void reset() {
      isDropping = false;
      isShiftDropping = false;
      lastCraft = null;
      lastButton = -1;
   }


   private boolean isEnabled() {
      if (!isKeybindingPressed(repeatLastKey)) {
         if (lastButton == 0 && !config.isEnableLeftClick()) {
            return false;
         } else if (lastButton == 1 && !config.isEnableRightClick()) {
            return false;
         } else if (lastButton == -1) {
            return false;
         }
      }
      boolean alwaysOn = config.isAlwaysOn();
      if (config.isCtrlHold() && InputHelper.isControlDown()) return !alwaysOn;
      if (config.isAltHold() && InputHelper.isAltDown()) return !alwaysOn;
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
      int code = keyBinding.boundKey.getCode();
      if (code == InputUtil.UNKNOWN_KEY.getCode())
         return false;
      return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), code);
   }

   public void onResultSlotUpdated(ItemStack itemStack) {
      if (lastCraft == null) return;
      if (itemStack.getItem() == Items.AIR) {
         reset();
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
         client.interactionManager.clickSlot(syncId, 0, isShiftDropping ? 1 : 0   , SlotActionType.THROW, client.player);
      } else {
         client.interactionManager.clickSlot(syncId, 0, 0, SlotActionType.QUICK_MOVE, client.player);
      }
   }
}
