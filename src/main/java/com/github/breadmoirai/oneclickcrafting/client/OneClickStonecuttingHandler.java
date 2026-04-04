package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.operation.OneClickStonecuttingOperation;
import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;
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
      debug("fireRepeatCraft(stonecutter): repeating recipe=" + lastSelected);
      MinecraftClient client = MinecraftClient.getInstance();
      if (!(client.currentScreen instanceof StonecutterScreen screen)) return;
      // Create the op before touching the screen handler so that getAvailableRecipes()
      // is still populated when onStonecutterClick reads it.
      onStonecutterClick(lastSelected, mod.config.isEnableLeftClick() ? 0 : 1);
      if (!hasOp()) return;
      screen.getScreenHandler().onButtonClick(client.player, lastSelected);
      if (client.interactionManager == null) return;
      client.interactionManager.clickButton(screen.getScreenHandler().syncId, lastSelected);
   }

   @Override
   public void onResultSlotUpdate(OneClickItemStack stack) {
      if (!hasOp()) {
         debug("onResultSlotUpdate(stonecutter): no active operation, ignoring");
         return;
      }
      if (!op.checkOutput(stack)) {
         debug("onResultSlotUpdate(stonecutter): output mismatch (expected=" + op.getResult() + " got=" + stack + "), ignoring");
         return;
      }
      debug("onResultSlotUpdate(stonecutter): output matched " + stack + ", executing craft");
      if (op.craft()) {
         debug("onResultSlotUpdate(stonecutter): craft complete");
         onCraftComplete();
      } else {
         debug("onResultSlotUpdate(stonecutter): craft deferred, waiting for next update");
      }
   }

   @Override
   public void onStonecutterClick(int selectedRecipe, int button) {
      debug("onStonecutterClick: recipe=" + selectedRecipe + " button=" + button);
      MinecraftClient client = MinecraftClient.getInstance();
      if (!(client.currentScreen instanceof StonecutterScreen screen)) {
         debug("onStonecutterClick: currentScreen is not StonecutterScreen (got "
               + (client.currentScreen == null ? "null" : client.currentScreen.getClass().getSimpleName()) + "), ignoring");
         return;
      }
      StonecutterScreenHandler screenHandler = screen.getScreenHandler();
      CuttingRecipeDisplay.Grouping<StonecuttingRecipe> recipes = screenHandler.getAvailableRecipes();
      if (recipes.isEmpty()) {
         debug("onStonecutterClick: getAvailableRecipes() is empty, ignoring");
         return;
      }
      CuttingRecipeDisplay.GroupEntry<StonecuttingRecipe> group = recipes.entries().get(selectedRecipe);
      ItemStack result = ((SlotDisplay.StackSlotDisplay) group.recipe().optionDisplay()).stack();
      setOp(new OneClickStonecuttingOperation(mod, selectedRecipe, new OneClickItemStack(result), group.input(), button));
      if (op.notValid()) {
         debug("onStonecutterClick: operation invalid, discarding");
         clearOp();
         return;
      }
      debug("onStonecutterClick: operation valid, waiting for result slot update");
      lastSelected = selectedRecipe;
   }
}
