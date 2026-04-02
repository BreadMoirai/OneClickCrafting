package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.operation.OneClickStonecuttingOperation;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.StonecutterScreenHandler;

public class OneClickStonecuttingHandler extends OneClickHandler implements OneClickEvents.StonecutterClick, OneClickEvents.ResultSlotUpdate  {

   private int lastSelected;

   public OneClickStonecuttingHandler(OneClickCraftingMod mod) {
      super(mod);
   }

   @Override
   public void onInitialize() {
      OneClickEvents.STONECUTTER_CLICK.register(this);
      OneClickEvents.RESULT_SLOT_UPDATE.register(this);
      ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
         if (screen instanceof StonecutterScreen) {
            ScreenEvents.afterTick(screen).register(screen2 -> tick());
            ScreenKeyboardEvents.beforeKeyPress(screen).register((screen2, key) -> {
               if (hasOp()) return;
               if (!mod.input.repeatLast.matches(key)) return;
               if (isRepeating) return;
               fireRepeatCraft();
            });
            ScreenEvents.remove(screen).register(screen2 -> clearOp());
         }
      });
   }

   @Override
   protected void fireRepeatCraft() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (!(client.currentScreen instanceof StonecutterScreen screen)) return;
      screen.getScreenHandler().onButtonClick(client.player, lastSelected);
      if (client.interactionManager == null) return;
      client.interactionManager.clickButton(screen.getScreenHandler().syncId, lastSelected);
      onStonecutterClick(lastSelected, mod.config.isEnableLeftClick() ? 0 : 1);
   }

   @Override
   public void onResultSlotUpdate(ItemStack stack) {
      if (!hasOp()) return;
      if (!op.checkOutput(stack)) return;
      if (op.craft()) {
         onCraftComplete();
      }
   }

   @Override
   public void onStonecutterClick(int selectedRecipe, int button) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (!(client.currentScreen instanceof StonecutterScreen screen)) return;
      StonecutterScreenHandler screenHandler = screen.getScreenHandler();
      CuttingRecipeDisplay.Grouping<StonecuttingRecipe> recipes = screenHandler.getAvailableRecipes();
      if (recipes.isEmpty()) return;
      CuttingRecipeDisplay.GroupEntry<StonecuttingRecipe> group = recipes.entries().get(selectedRecipe);
      ItemStack result = ((SlotDisplay.StackSlotDisplay) group.recipe().optionDisplay()).stack();
      setOp(new OneClickStonecuttingOperation(mod, selectedRecipe, result, group.input(), button));
      if (op.notValid()) {
         clearOp();
         return;
      }
      lastSelected = selectedRecipe;
   }
}
