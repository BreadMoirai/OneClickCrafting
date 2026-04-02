package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.operation.OneClickCraftingOperation;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;

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
   public void onRecipeClick(NetworkRecipeId recipe, int button) {
      setOp(new OneClickCraftingOperation(mod, recipe.index(), button));
      if (op.notValid()) {
         clearOp();
         return;
      }
      if (op.shouldWaitForResultSlotUpdate()) return;
      op.craft();
   }

   @Override
   protected void fireRepeatCraft() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (!(client.currentScreen instanceof RecipeBookScreen<?> screen)) return;
      RecipeBookWidget<?> recipeBook = screen.recipeBook;
      if (recipeBook.selectedRecipeResults == null || recipeBook.selectedRecipe == null) return;
      recipeBook.select(recipeBook.selectedRecipeResults, recipeBook.selectedRecipe, mod.input.isShiftDown());
      OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(recipeBook.selectedRecipe, mod.config.isEnableLeftClick() ? 0 : 1 );
   }

   @Override
   public void onResultSlotUpdate(ItemStack itemStack) {
      if (op == null) return;
      if (!op.checkOutput(itemStack)) return;
      op.craft();
      onCraftComplete();
   }
}
