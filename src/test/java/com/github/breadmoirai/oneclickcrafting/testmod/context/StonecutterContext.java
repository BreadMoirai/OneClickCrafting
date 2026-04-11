package com.github.breadmoirai.oneclickcrafting.testmod.context;

import com.github.breadmoirai.oneclickcrafting.testmod.ConfigHelper;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import com.github.breadmoirai.oneclickcrafting.testmod.context.v21_8.StonecutterContextImpl;

@SuppressWarnings("UnstableApiUsage")
public abstract class StonecutterContext extends CraftContext {

   public StonecutterContext(ClientGameTestContext context,
                             TestSingleplayerContext world,
                             String inputItem,
                             int inputCount,
                             String outputItem,
                             int outputCount) {
      super(context, world, inputItem, inputCount, outputItem, outputCount);
   }

   public static StonecutterContext create(ClientGameTestContext context,
                                           TestSingleplayerContext world,
                                           String inputItem,
                                           int inputCount,
                                           String outputItem,
                                           int outputCount) {
      return new StonecutterContextImpl(context, world, inputItem, inputCount, outputItem, outputCount);
   }

   @Override
   public void prepare(int operations) {
      clearInventory();
      giveItem(inputItem, operations * inputCount);
      wait(2);
      open();
      putOneItemInInputSlot(inputItem);
   }

   @Override
   public void click(int mouseButton) {
      clickRecipeButton(mouseButton, 0);
   }

   @Override
   public String featureToggleLabel() {
      return ConfigHelper.LABEL_STONECUTTER;
   }

   @Override
   public void close() {
      closeScreen();
      // Wait one tick for the server to process the CloseHandledScreenC2SPacket and
      // return any item in the stonecutter input slot to the player's inventory.
      // Without this, a subsequent clearInventory() runs before the server returns
      // the item, leaving a stale cobblestone in the inventory for the next test.
      waitTick();
   }

   // -------------------------------------------------------------------------
   // Stonecutter-specific public helpers
   // -------------------------------------------------------------------------

   /**
    * Triggers the mod's stonecutter handler for recipe {@code recipeIndex} and sends
    * the corresponding button-click packet so the server fills the output slot.
    *
    * @param mouseButton 0 = left-click, 1 = right-click
    * @param recipeIndex index into the available recipe list
    */
   public abstract void clickRecipeButton(int mouseButton, int recipeIndex);

   // -------------------------------------------------------------------------
   // Protected helpers for subclasses
   // -------------------------------------------------------------------------

   /**
    * Moves exactly one item of {@code itemId} from the player's inventory into the
    * stonecutter input slot, leaving any remaining items in the inventory for refill.
    *
    * @param itemId namespaced item ID, e.g. {@code "minecraft:cobblestone"}
    */
   protected abstract void putOneItemInInputSlot(String itemId);
}
