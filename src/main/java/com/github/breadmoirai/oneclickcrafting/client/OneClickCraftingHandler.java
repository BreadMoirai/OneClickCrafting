package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.operation.OneClickCraftingOperation;

import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class OneClickCraftingHandler extends OneClickHandler implements OneClickEvents.RecipeClick, OneClickEvents.ResultSlotUpdate {

   public OneClickCraftingHandler(OneClickCraftingMod mod) {
      super(mod);
   }

   @Override
   public void onInitialize() {
      OneClickEvents.RECIPE_CLICK.register(this);
      OneClickEvents.RESULT_SLOT_UPDATE.register(this);
      ScreenEvents.BEFORE_INIT.register((_, screen, _, _) -> {
         if (screen instanceof InventoryScreen || screen instanceof CraftingScreen) {
            ScreenEvents.afterTick(screen).register(_ -> tick());
            ScreenKeyboardEvents.beforeKeyPress(screen)
               //$ if >= 1.21.9 '.register((screen2, key) -> {' else '.register((screen2, key, _, _) -> {'
               .register((screen2, key) -> {
                  if (hasOp()) return;
                  //~ if >=1.21.9 'key' -> 'key.key()'
                  if (mod.input.repeatLast.guard(key.key())) return;
                  if (isRepeating) return;
                  isRepeating = true;
                  fireRepeatCraft();
               });
            ScreenEvents.remove(screen).register(_ -> clearOp());
         }
      });
   }

   @Override
   public void onRecipeClick(int recipe, int button) {
      debug("onRecipeClick: recipe=" + recipe + " button=" + button);
      setOp(OneClickCraftingOperation.create(mod, recipe, button));
      if (op.notValid()) {
         debug("onRecipeClick: operation invalid, discarding");
         clearOp();
         return;
      }
      if (op.shouldWaitForResultSlotUpdate()) {
         debug("onRecipeClick: waiting for result slot update");
         return;
      }
      debug("onRecipeClick: crafting immediately (no slot update needed)");
      op.craft();
   }

   @Override
   protected void fireRepeatCraft() {
      int recipe = mod.recipeBook.selectLast(mod.input.isShiftDown());
      debug("fireRepeatCraft: recipe=" + recipe);
      if (recipe == -1) return;
      OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(recipe, mod.config.isEnableLeftClick() ? 0 : 1);
   }

   @Override
   public void onResultSlotUpdate(OneClickItemStack stack) {
      if (!hasOp()) {
         debug("onResultSlotUpdate(crafting): no active operation, ignoring " + stack.count() + " " + stack.stack()
            .getItemName().getString(), () -> {
            Screen screen = Minecraft.getInstance().screen;
            return screen instanceof InventoryScreen || screen instanceof CraftingScreen;
         });
         return;
      }
      if (!op.checkOutput(stack)) {
         debug(
            "onResultSlotUpdate(crafting): output mismatch (expected=" + op.getResult() + " got=" + stack + "), ignoring");
         return;
      }
      debug("onResultSlotUpdate(crafting): output matched " + stack + ", executing craft");
      op.craft();
      onCraftComplete();
   }
}
