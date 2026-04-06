package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import com.github.breadmoirai.oneclickcrafting.operation.OneClickStonecuttingOperation;
import com.github.breadmoirai.oneclickcrafting.stonecutter.OneClickStonecutterRecipe;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
//? >=1.21.10 <=1.21.11 {
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.sound.SoundEvents;
//?}

import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;

public class OneClickStonecuttingHandler extends OneClickHandler implements OneClickEvents.StonecutterClick, OneClickEvents.ResultSlotUpdate {

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
               if (mod.input.repeatLast.guard(key)) return;
               if (isRepeating) return;
               fireRepeatCraft();
            });
            ScreenEvents.remove(screen).register(screen2 -> clearOp());
         }
      });
   }

   @Override
   protected void fireRepeatCraft() {
      if (hasOp()) return;
      debug("fireRepeatCraft(stonecutter): repeating recipe=" + lastSelected);
      mod.stonecutter.selectRecipe(lastSelected);
//      onStonecutterClick(lastSelected, mod.config.isEnableLeftClick() ? 0 : 1);
   }

   @Override
   public void onResultSlotUpdate(OneClickItemStack stack) {
      if (!hasOp()) {
         debug("onResultSlotUpdate(stonecutter): no active operation, ignoring");
         return;
      }
      if (!op.checkOutput(stack)) {
         debug(
            "onResultSlotUpdate(stonecutter): output mismatch (expected=" + op.getResult() + " got=" + stack + "), ignoring");
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
      if (hasOp()) {
         debug("onStonecutterClick: operation in progress, ignoring re-entrant selectRecipe call");
         return;
      }
      debug("onStonecutterClick: recipe=" + selectedRecipe + " button=" + button);
      OneClickStonecutterRecipe recipe = mod.stonecutter.getRecipe(selectedRecipe);
      setOp(new OneClickStonecuttingOperation(mod, selectedRecipe, recipe, button));
      if (op.notValid()) {
         debug("onStonecutterClick: operation invalid, discarding");
         clearOp();
         return;
      }
      debug("onStonecutterClick: operation valid, waiting for result slot update");
      lastSelected = selectedRecipe;
   }
}
