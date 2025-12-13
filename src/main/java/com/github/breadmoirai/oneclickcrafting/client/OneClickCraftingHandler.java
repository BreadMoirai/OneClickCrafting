package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.util.InputHelper;
import com.github.breadmoirai.oneclickcrafting.util.InventoryUtils;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.display.SlotDisplayContexts;

import java.util.Map;

public class OneClickCraftingHandler extends OneClickHandler {


   @Override
   public void onInitialize() {
      ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
         if (screen instanceof InventoryScreen || screen instanceof CraftingScreen) {
            ScreenKeyboardEvents.afterKeyPress(screen).register((screen2, key) -> {
               RecipeBookWidget<?> recipeBook = ((RecipeBookScreen<?>) screen).recipeBook;
               if (InputHelper.isKeybindingPressed(
                  OneClickCraftingClient.getInstance().repeatLastKey) && !InputHelper.isToggleKey(
                  key) && recipeBook.selectedRecipeResults != null && recipeBook.selectedRecipe != null)
                  recipeBook.select(recipeBook.selectedRecipeResults, recipeBook.selectedRecipe,
                     InputHelper.isShiftDown());
            });
            ScreenEvents.remove(screen).register(screen1 -> reset());
         }
      });
   }


   public void recipeClicked(NetworkRecipeId recipe) {
      OneClickCraftingConfig config = OneClickCraftingClient.getInstance().config;
      if (!isEnabled()) {
         reset();
         return;
      }
      isDropping = config.isDropEnable() && InputHelper.isDropKeyPressed();
      isShiftDropping = isDropping && InputHelper.isShiftDown();
      MinecraftClient client = MinecraftClient.getInstance();
      ClientWorld world = client.world;
      if (world == null) return;
      ClientPlayerEntity player = client.player;
      if (player == null) return;
      Map<NetworkRecipeId, RecipeDisplayEntry> recipes = player.getRecipeBook().recipes;
      ItemStack result = recipes.get(recipe).display().result().getStacks(SlotDisplayContexts.createParameters(world))
         .getFirst();
      setLastCraft(result);
   }

   @Override
   public void onResultSlotUpdated(ItemStack itemStack) {
      if (lastCraft == null) return;
      if (itemStack.getItem() == Items.AIR) {
         return;
      }
      if (!ItemStack.areItemsEqual(itemStack, lastCraft)) {
         return;
      }
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.interactionManager == null) return;
      if (!(client.currentScreen instanceof HandledScreen<?> gui)) return;
      if (isDropping) {
         if (isShiftDropping) {
            InventoryUtils.dropStack(gui, 0);
         } else {
            InventoryUtils.dropItem(gui, 0);
         }
      } else {
         InventoryUtils.shiftClickSlot(gui, 0);
      }
      reset();
   }
}
