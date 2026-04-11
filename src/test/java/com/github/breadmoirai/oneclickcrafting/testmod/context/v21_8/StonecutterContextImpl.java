//? <1.21.9 {
package com.github.breadmoirai.oneclickcrafting.testmod.context.v21_8;

import com.github.breadmoirai.oneclickcrafting.testmod.context.StonecutterContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;

public class StonecutterContextImpl extends StonecutterContext {

   public StonecutterContextImpl(ClientGameTestContext context,
                                  TestSingleplayerContext world,
                                  String inputItem,
                                  int inputCount,
                                  String outputItem,
                                  int outputCount) {
      super(context, world, inputItem, inputCount, outputItem, outputCount);
   }

   @Override
   public void open() {
      openBlock("minecraft:stonecutter", StonecutterScreen.class);
   }

   @Override
   public void clickRecipeButton(int mouseButton, int recipeIndex) {
      // Wait for the server to process any previous craft before clicking again.
      wait(3);
      context.runOnClient(mc -> {
         if (!(mc.screen instanceof StonecutterScreen screen)) {
            throw new AssertionError("clickRecipeButton: not in a StonecutterScreen");
         }
         StonecutterMenu menu = screen.getMenu();
         if (menu.getNumberOfVisibleRecipes() == 0) {
            throw new AssertionError("clickRecipeButton: no available recipes (input slot empty?)");
         }
         if (recipeIndex >= menu.getNumberOfVisibleRecipes()) {
            throw new AssertionError(
               "clickRecipeButton: recipeIndex " + recipeIndex
                  + " out of range, available=" + menu.getNumberOfVisibleRecipes());
         }
         // StonecutterScreen draws recipe buttons at (x+52, y+14), 16×18 px each, 4 columns.
         // x = (screenWidth - 176) / 2,  y = (screenHeight - 166) / 2.
         int guiLeft = (screen.width - 176) / 2;
         int guiTop  = (screen.height - 166) / 2;
         double cx = guiLeft + 52 + (recipeIndex % 4) * 16 + 8;
         double cy = guiTop  + 14 + (recipeIndex / 4) * 18 + 9;
         screen.mouseClicked(cx, cy, mouseButton);
      });
   }

   @Override
   protected void putOneItemInInputSlot(String itemId) {
      context.runOnClient(mc -> {
         if (!(mc.screen instanceof StonecutterScreen screen)) {
            throw new AssertionError("putOneItemInInputSlot: not in a StonecutterScreen");
         }
         StonecutterMenu menu = screen.getMenu();
         Slot sourceSlot = null;
         for (var slot : menu.slots) {
            if (!(slot.container instanceof Inventory)) continue;
            if (BuiltInRegistries.ITEM.getKey(slot.getItem().getItem()).toString().equals(itemId)) {
               sourceSlot = slot;
               break;
            }
         }
         if (sourceSlot == null) {
            throw new AssertionError(
               "putOneItemInInputSlot: item not found in player inventory: " + itemId);
         }
         // Pick up the full stack from inventory
         mc.gameMode.handleInventoryMouseClick(menu.containerId, sourceSlot.index, 0, ClickType.PICKUP, mc.player);
         // Place one item in stonecutter input slot (slot 0) using right-click
         mc.gameMode.handleInventoryMouseClick(menu.containerId, 0, 1, ClickType.PICKUP, mc.player);
         // Return cursor stack to inventory slot
         mc.gameMode.handleInventoryMouseClick(menu.containerId, sourceSlot.index, 0, ClickType.PICKUP, mc.player);
      });
      wait(2);
   }
}
//?}
