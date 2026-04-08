package com.github.breadmoirai.oneclickcrafting.testmod.context;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

//? 26.1 {
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
//?} >=1.21.10 <=1.21.11 {
/*import net.minecraft.client.gui.screen.ingame.CraftingScreen;
*///?}

/**
 * {@link CraftContext} implementation that uses the recipe-book in a crafting
 * table screen to perform crafting. Identical to {@link InventoryContext} except
 * that {@link #open()} places and opens a crafting table rather than the inventory.
 */
@SuppressWarnings("UnstableApiUsage")
public class CraftingTableContext extends InventoryContext {

   public CraftingTableContext(ClientGameTestContext context,
                               TestSingleplayerContext world,
                               String inputItem,
                               int inputCount,
                               String outputItem,
                               int outputCount) {
      super(context, world, inputItem, inputCount, outputItem, outputCount);
   }

   @Override
   public void open() {
      openBlock("minecraft:crafting_table", CraftingScreen.class);
      recipeBook.open();
   }
}
