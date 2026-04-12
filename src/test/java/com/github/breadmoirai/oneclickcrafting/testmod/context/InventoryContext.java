package com.github.breadmoirai.oneclickcrafting.testmod.context;

import com.github.breadmoirai.oneclickcrafting.testmod.ConfigHelper;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import java.util.Map;

/**
 * {@link CraftContext} implementation that uses the recipe-book in the player's
 * inventory screen to perform crafting.
 */
@SuppressWarnings("UnstableApiUsage")
public class InventoryContext extends CraftContext {

   public InventoryContext(ClientGameTestContext context,
                           TestSingleplayerContext world,
                           String inputItem,
                           int inputCount,
                           String outputItem,
                           int outputCount) {
      super(context, world, inputItem, inputCount, outputItem, outputCount);
   }

   @Override
   public void prepare(int operations) {
      clearInventory();
      giveItem(inputItem, inputCount * operations );
      wait(2);
      assertInventoryExact(Map.of(inputItem, inputCount * operations));
      open();
   }

   @Override
   public void open() {
      openInventory();
      recipeBook.open();
   }

   @Override
   public void click(int mouseButton) {
      recipeBook.clickRecipeButton(outputItem, mouseButton);
   }

   @Override
   public String featureToggleLabel() {
      return ConfigHelper.LABEL_RIGHT_CLICK;
   }

   @Override
   public void close() {
      closeScreen();
   }
}
