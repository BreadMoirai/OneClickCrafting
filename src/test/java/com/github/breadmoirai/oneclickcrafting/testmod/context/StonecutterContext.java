package com.github.breadmoirai.oneclickcrafting.testmod.context;

import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.HandledScreenAccessor;
import com.github.breadmoirai.oneclickcrafting.testmod.ConfigHelper;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.client.input.MouseInput;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.registry.Registries;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

@SuppressWarnings("UnstableApiUsage")
public class StonecutterContext extends CraftContext {

   public StonecutterContext(ClientGameTestContext context,
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
      giveItem(inputItem, operations * inputCount);
      wait(2);
      open();
      putOneItemInInputSlot(inputItem);
   }

   @Override
   public void open() {
      openBlock("minecraft:stonecutter", StonecutterScreen.class);
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
   // Stonecutter-specific helpers
   // -------------------------------------------------------------------------

   /**
    * Shift-clicks (QUICK_MOVE) the first stack of {@code itemId} found in the
    * screen handler into the stonecutter input slot. Waits a few ticks for the
    * server to send updated available recipes.
    *
    * @param itemId namespaced item ID, e.g. {@code "minecraft:cobblestone"}
    */
   public void putItemInInputSlot(String itemId) {
      try {
         context.waitFor(mc -> {
            if (!(mc.currentScreen instanceof StonecutterScreen screen)) return false;
            return screen.getScreenHandler().slots.stream().anyMatch(slot ->
               Registries.ITEM.getId(slot.getStack().getItem()).toString().equals(itemId));
         }, 20);
      } catch (AssertionError timeout) {
         throw new AssertionError("putItemInInputSlot: item not found in inventory: " + itemId);
      }
      context.runOnClient(mc -> {
         if (!(mc.currentScreen instanceof StonecutterScreen screen)) {
            throw new AssertionError("putItemInInputSlot: not in a StonecutterScreen");
         }
         StonecutterScreenHandler handler = screen.getScreenHandler();
         for (var slot : handler.slots) {
            if (Registries.ITEM.getId(slot.getStack().getItem()).toString().equals(itemId)) {
               ((HandledScreenAccessor) screen).callOnMouseClick(slot, slot.getIndex(), 0, SlotActionType.QUICK_MOVE);
               return;
            }
         }
         throw new AssertionError("putItemInInputSlot: item not found in inventory: " + itemId);
      });
      wait(2);
   }

   /**
    * Triggers the mod's stonecutter handler for recipe {@code recipeIndex} and sends
    * the corresponding button-click packet so the server fills the output slot.
    *
    * @param mouseButton 0 = left-click, 1 = right-click
    * @param recipeIndex index into the available recipe list
    */
   public void clickRecipeButton(int mouseButton, int recipeIndex) {
      // Wait until the server has reset selectedRecipe away from recipeIndex.
      // When onButtonClick sees selectedRecipe == recipeIndex it returns false and skips
      // sending the ButtonClickC2SPacket, so no craft would happen on the server.
      // The server resets selectedRecipe to -1 after each craft (via updateInput) and
      // syncs it back to the client via sendContentUpdates on the next server tick.
      try {
         context.waitFor(mc -> {
            if (!(mc.currentScreen instanceof StonecutterScreen screen)) return false;
            return screen.getScreenHandler().getSelectedRecipe() != recipeIndex;
         }, 20);
      } catch (AssertionError timeout) {
         throw new AssertionError(
            "clickRecipeButton: selectedRecipe was not reset by server within 20 ticks"
               + " (recipeIndex=" + recipeIndex + ")");
      }
      context.runOnClient(mc -> {
         if (!(mc.currentScreen instanceof StonecutterScreen screen)) {
            throw new AssertionError("clickRecipeButton: not in a StonecutterScreen");
         }
         StonecutterScreenHandler handler = screen.getScreenHandler();
         CuttingRecipeDisplay.Grouping<?> recipes = handler.getAvailableRecipes();
         if (recipes.isEmpty()) {
            throw new AssertionError(
               "clickRecipeButton: no available recipes (input slot empty?)");
         }
         if (recipeIndex >= recipes.entries().size()) {
            throw new AssertionError(
               "clickRecipeButton: recipeIndex " + recipeIndex
                  + " out of range, available=" + recipes.entries().size());
         }
         // StonecutterScreen draws recipe buttons at (x+52, y+14), 16×18 px each, 4 columns.
         // x = (screenWidth - 176) / 2,  y = (screenHeight - 166) / 2.
         int guiLeft = (screen.width - 176) / 2;
         int guiTop  = (screen.height - 166) / 2;
         double cx = guiLeft + 52 + (recipeIndex % 4) * 16 + 8;
         double cy = guiTop  + 14 + (recipeIndex / 4) * 18 + 9;
         screen.mouseClicked(new Click(cx, cy, new MouseInput(mouseButton, 0)), false);
      });
   }

   // -------------------------------------------------------------------------
   // Private helpers
   // -------------------------------------------------------------------------

   /**
    * Moves exactly one item of {@code itemId} from the player's inventory into the
    * stonecutter input slot (slot 0), leaving any remaining items of the same type
    * in the player's inventory so that the handler's refill mechanism can use them.
    *
    * @param itemId namespaced item ID, e.g. {@code "minecraft:cobblestone"}
    */
   private void putOneItemInInputSlot(String itemId) {
      context.runOnClient(mc -> {
         if (!(mc.currentScreen instanceof StonecutterScreen screen)) {
            throw new AssertionError("putOneItemInInputSlot: not in a StonecutterScreen");
         }
         StonecutterScreenHandler handler = screen.getScreenHandler();
         Slot sourceSlot = null;
         for (var slot : handler.slots) {
            if (!(slot.inventory instanceof PlayerInventory)) continue;
            if (Registries.ITEM.getId(slot.getStack().getItem()).toString().equals(itemId)) {
               sourceSlot = slot;
               break;
            }
         }
         if (sourceSlot == null) {
            throw new AssertionError(
               "putOneItemInInputSlot: item not found in player inventory: " + itemId);
         }
         ((HandledScreenAccessor) screen).callOnMouseClick(sourceSlot, sourceSlot.getIndex(), 0, SlotActionType.PICKUP);
         ((HandledScreenAccessor) screen).callOnMouseClick(handler.getSlot(0), 0, 1, SlotActionType.PICKUP);
         ((HandledScreenAccessor) screen).callOnMouseClick(sourceSlot, sourceSlot.getIndex(), 0, SlotActionType.PICKUP);
      });
      wait(2);
   }
}
