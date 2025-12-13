package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.util.InputHelper;
import com.github.breadmoirai.oneclickcrafting.util.InventoryUtils;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.Optional;
import java.util.function.Consumer;

public class OneClickStonecuttingHandler extends OneClickHandler {

   private boolean isShifting;
   private Ingredient lastIngredient;
   private int lastSelected;
   private Consumer<ItemStack> onNextUpdate;

   @Override
   public void onInitialize() {
      ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
         if (screen instanceof StonecutterScreen) {
            ScreenKeyboardEvents.afterKeyPress(screen).register((screen2, key) -> {
               if (InputHelper.isKeybindingPressed(
                  OneClickCraftingClient.getInstance().repeatLastKey) && !InputHelper.isToggleKey(key)) {
                  StonecutterScreen stonecutterScreen = (StonecutterScreen) screen2;
                  stonecutterScreen.getScreenHandler().onButtonClick(client.player, lastSelected);
                  client.interactionManager.clickButton(stonecutterScreen.getScreenHandler().syncId, lastSelected);
                  recipeClicked(stonecutterScreen, new Click(0, 0,
                        new MouseInput(OneClickCraftingClient.getInstance().config.isEnableLeftClick() ? 0 : 1, 0)),
                     lastSelected);
               }
            });
            ScreenEvents.remove(screen).register(screen1 -> {
               lastSelected = -1;
               lastButton = -1;
               lastCraft = null;
               lastIngredient = null;
            });
         }
      });
   }

   @Override
   public void onResultSlotUpdated(ItemStack itemStack) {
      if (this.onNextUpdate != null) {
         this.onNextUpdate.accept(itemStack);
         return;
      }
      if (resultStackDoesNotMatch(itemStack)) return;
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.interactionManager == null) return;
      if (!(client.currentScreen instanceof HandledScreen<?> gui)) return;
      Slot input = InventoryUtils.getSlot(gui, 0);
      if (isDropping) {
         if (isShifting) {
            if (input.getStack().getCount() != 64) {
               InventoryUtils.moveMatchingIntoSlot(gui, 0);
               gui.getScreenHandler().onButtonClick(client.player, lastSelected);
               client.interactionManager.clickButton(gui.getScreenHandler().syncId, lastSelected);
               onNextUpdate = (m) -> {
                  if (resultStackDoesNotMatch(m)) {
                     return;
                  }
                  this.onNextUpdate = null;
                  InventoryUtils.dropStack(gui, 1);
                  refill(gui);
               };
               return;
            }
         } else {
            boolean shouldRefill = input.getStack().getCount() == 1;
            InventoryUtils.dropItem(gui, 1);
            InventoryUtils.leftClickSlot(gui, 0);
            InventoryUtils.leftClickSlot(gui, 0);
            if (shouldRefill) {
               refill(gui);
            }
         }
      } else {
         if (isShifting) {
            if (input.getStack().getCount() != 64) {
               InventoryUtils.moveMatchingIntoSlot(gui, 0);
               gui.getScreenHandler().onButtonClick(client.player, lastSelected);
               client.interactionManager.clickButton(gui.getScreenHandler().syncId, lastSelected);
               onNextUpdate = (m) -> {
                  if (resultStackDoesNotMatch(m)) {
                     return;
                  }
                  this.onNextUpdate = null;
                  InventoryUtils.shiftClickSlot(gui, 1);
                  refill(gui);
               };
               return;
            } else {
               InventoryUtils.shiftClickSlot(gui, 1);
               refill(gui);
            }
         } else {
            if (input.getStack().getCount() != 1) {
               InventoryUtils.leftClickSlot(gui, 0);
               InventoryUtils.rightClickSlot(gui, 0);
               gui.getScreenHandler().onButtonClick(client.player, lastSelected);
               client.interactionManager.clickButton(gui.getScreenHandler().syncId, lastSelected);
               onNextUpdate = (next) -> {
                  if (resultStackDoesNotMatch(next)) {
                     return;
                  }
                  this.onNextUpdate = null;
                  InventoryUtils.shiftClickSlot(gui, 1);
                  InventoryUtils.leftClickSlot(gui, 0);
               };
               return;
            } else {
               InventoryUtils.shiftClickSlot(gui, 1);
               refill(gui);
            }
         }
      }
      reset();
   }

   private void refill(HandledScreen<?> gui) {
      Optional<Slot> refill = InventoryUtils.findMatchingSlot(gui, lastIngredient);
      refill.ifPresent(slot -> {
         boolean multi = slot.getStack().getCount() > 1;
         InventoryUtils.leftClickSlot(gui, slot);
         InventoryUtils.rightClickSlot(gui, 0);
         if (multi) {
            InventoryUtils.leftClickSlot(gui, slot);
         }
      });
   }

   private boolean resultStackDoesNotMatch(ItemStack itemStack) {
      if (lastButton == -1) return true;
      if (lastCraft == null) return true;
      if (itemStack.getItem() == Items.AIR) {
         return true;
      }
      return !ItemStack.areItemsEqual(itemStack, lastCraft);
   }

   public void recipeClicked(StonecutterScreen screen, Click click, int selectedRecipe) {
      OneClickCraftingConfig config = OneClickCraftingClient.getInstance().config;
      setLastButton(click.button());
      if (!isEnabled()) {
         reset();
         return;
      }
      isDropping = config.isDropEnable() && InputHelper.isDropKeyPressed();
      isShifting = InputHelper.isShiftDown();
      isShiftDropping = isDropping && isShifting;
      MinecraftClient client = MinecraftClient.getInstance();
      ClientWorld world = client.world;
      if (world == null) return;
      ClientPlayerEntity player = client.player;
      if (player == null) return;
      StonecutterScreenHandler screenHandler = screen.getScreenHandler();
      CuttingRecipeDisplay.Grouping<StonecuttingRecipe> recipes = screenHandler.getAvailableRecipes();
      if (recipes.isEmpty()) return;
      CuttingRecipeDisplay.GroupEntry<StonecuttingRecipe> group = recipes.entries().get(selectedRecipe);
      ItemStack result = ((SlotDisplay.StackSlotDisplay) group.recipe().optionDisplay()).stack();
      setLastButton(click.button());
      setLastCraft(result);
      setLastIngredient(group.input());
      setLastSelected(selectedRecipe);
   }

   @Override
   public boolean isEnabled() {
      OneClickCraftingClient client = OneClickCraftingClient.getInstance();
      if (!client.config.isEnableStonecutter()) return false;
      return super.isEnabled();
   }

   @Override
   public void reset() {
      this.isShifting = false;
      super.reset();
   }

   private void setLastIngredient(Ingredient ingredient) {
      this.lastIngredient = ingredient;
   }


   public void setLastSelected(int lastSelected) {
      this.lastSelected = lastSelected;
   }

}
