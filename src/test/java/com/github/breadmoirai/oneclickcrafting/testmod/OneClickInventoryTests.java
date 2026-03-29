package com.github.breadmoirai.oneclickcrafting.testmod;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.item.Items;

@SuppressWarnings("UnstableApiUsage")
public class OneClickInventoryTests extends OneClickTests {
   public OneClickInventoryTests(ClientGameTestContext context, TestSingleplayerContext world) {
      super(context, world);
   }

   public void open() {
      openInventory();
   }

   public void close() {
      closeScreen();
   }

   /**
    * When "Enable Left Click" is OFF (default), a left-click on a recipe button
    * should NOT trigger the auto-craft action.
    */
   public void placeOne() {
      open();
      IngredientGiver recipe = recipe("oak_planks");
      recipe.give(4);
      recipeBook.open();
      recipeBook.leftClick(Items.OAK_PLANKS);
      assertInventoryEmpty();
      close();
      open();
      assertInventoryExact(Items.OAK_LOG, 1);
      close();
   }

   /**
    * When "Enable Right Click" is ON (default), a right-click on a recipe button
    * should trigger the auto-craft action.
    */
   public void craftOne() {
      clearInventory();
      waitTick();
      open();
      IngredientGiver recipe = recipe("oak_planks");
      recipe.give(4);
      recipeBook.open();
      recipeBook.rightClick(Items.OAK_PLANKS);
      assertInventoryExact(Items.OAK_PLANKS, 4);
      close();
      open();
      assertInventoryExact(Items.OAK_PLANKS, 4);
      close();
   }


}
