package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.operation.OneClickCraftingOperation;
import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
//? 26.1 {
/*import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
*///?} >=1.21.10 <=1.21.11 {
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
//?}

public class OneClickCraftingHandler extends OneClickHandler implements OneClickEvents.RecipeClick, OneClickEvents.ResultSlotUpdate {


   public OneClickCraftingHandler(OneClickCraftingMod mod) {
      super(mod);
   }

   @Override
   public void onInitialize() {
      OneClickEvents.RECIPE_CLICK.register(this);
      OneClickEvents.RESULT_SLOT_UPDATE.register(this);
      ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
         if (screen instanceof InventoryScreen || screen instanceof CraftingScreen) {
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
   public void onRecipeClick(int recipe, int button) {
      debug("onRecipeClick: recipe=" + recipe + " button=" + button);
      setOp(new OneClickCraftingOperation(mod, recipe, button));
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
      if (op == null) {
         debug("onResultSlotUpdate: no active operation, ignoring");
         return;
      }
      if (!op.checkOutput(stack)) {
         debug("onResultSlotUpdate: output mismatch (expected=" + op.getResult() + " got=" + stack + "), ignoring");
         return;
      }
      debug("onResultSlotUpdate: output matched " + stack + ", executing craft");
      op.craft();
      onCraftComplete();
   }
}
