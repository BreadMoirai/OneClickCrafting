package com.github.breadmoirai.oneclickcrafting.testmod.suite;

import com.github.breadmoirai.oneclickcrafting.testmod.OneClickTests;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

/**
 * Tests for multi-option recipe buttons — where a single cycling button in the
 * recipe book represents several craftable alternatives (e.g. oak planks and
 * birch planks share one grouped button when the player holds both log types).
 *
 * <p>Scenario setup: give the player one oak log and one birch log. Both planks
 * recipes are craftable, so the recipe book shows a single cycling button that
 * alternates between the two. Right-clicking that button opens an overlay submenu
 * with individual buttons for each alternative.
 */
@SuppressWarnings("UnstableApiUsage")
public class RecipeAlternativesTests extends OneClickTests {

   private static final String OAK_LOG     = "minecraft:oak_log";
   private static final String BIRCH_LOG   = "minecraft:birch_log";
   private static final String OAK_PLANKS  = "minecraft:oak_planks";
   private static final String BIRCH_PLANKS = "minecraft:birch_planks";

   public RecipeAlternativesTests(ClientGameTestContext context, TestSingleplayerContext world) {
      super(context, world);
   }

   // =========================================================================
   // Helpers
   // =========================================================================

   /** Clear inventory, give one oak log and one birch log, open inventory recipe book. */
   private void prepare() {
      clearInventory();
      giveItem(OAK_LOG, 1);
      giveItem(BIRCH_LOG, 1);
      wait(1);
      openInventory();
      recipeBook.open();
   }

   // =========================================================================
   // Cycling-button tests
   // =========================================================================

   /**
    * Left-click disabled: clicking the cycling button places the currently
    * displayed recipe in the crafting grid without crafting anything.
    * Closing the screen returns all items to the inventory.
    */
   public void multiOptionLeftClickDisabledPlacesRecipe() {
      prepare();
      recipeBook.clickMultiOptionButton(0, OAK_PLANKS, BIRCH_PLANKS);
      wait(2);
      // Recipe placed in crafting grid — no planks produced
      assertInventoryCount(OAK_PLANKS, 0);
      assertInventoryCount(BIRCH_PLANKS, 0);
      closeScreen();
      // Grid contents returned to inventory on close
      assertInventoryExact(OAK_LOG, 1, BIRCH_LOG, 1);
   }

   /**
    * Left-click enabled: clicking the cycling button immediately crafts
    * whichever item (oak or birch planks) is currently displayed.
    */
   public void multiOptionLeftClickEnabledCrafts() {
      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_LEFT_CLICK, true);
      config.saveAndCloseYacl();
      config.closeModsScreen();

      prepare();
      String craftedItem = recipeBook.clickMultiOptionButton(0, OAK_PLANKS, BIRCH_PLANKS);
      wait(2);
      assertInventoryAtLeast(craftedItem, 4); // 1 log → 4 planks
      closeScreen();

      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_LEFT_CLICK, false);
      config.saveAndCloseYacl();
      config.closeModsScreen();
   }

   // =========================================================================
   // Overlay (submenu) tests
   // =========================================================================

   /**
    * Right-clicking the cycling button opens the overlay submenu. Right-clicking
    * the oak-planks button in the overlay crafts oak planks.
    */
   public void multiOptionOverlayRightClickOakPlanks() {
      prepare();
      recipeBook.clickMultiOptionButton(1, OAK_PLANKS, BIRCH_PLANKS); // open overlay
      wait(2);
      recipeBook.clickOverlayButton(OAK_PLANKS, 1);
      wait(2);
      assertInventoryAtLeast(OAK_PLANKS, 4);
      closeScreen();
   }

   /**
    * Right-clicking the cycling button opens the overlay submenu. Right-clicking
    * the birch-planks button in the overlay crafts birch planks.
    */
   public void multiOptionOverlayRightClickBirchPlanks() {
      prepare();
      recipeBook.clickMultiOptionButton(1, OAK_PLANKS, BIRCH_PLANKS); // open overlay
      wait(2);
      recipeBook.clickOverlayButton(BIRCH_PLANKS, 1);
      wait(2);
      assertInventoryAtLeast(BIRCH_PLANKS, 4);
      closeScreen();
   }

   /**
    * With left-click enabled, left-clicking an overlay button crafts the item
    * (same behaviour as a normal recipe button).
    */
   public void multiOptionOverlayLeftClickEnabledCrafts() {
      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_LEFT_CLICK, true);
      config.saveAndCloseYacl();
      config.closeModsScreen();

      prepare();
      recipeBook.clickMultiOptionButton(1, OAK_PLANKS, BIRCH_PLANKS); // open overlay
      wait(2);
      recipeBook.clickOverlayButton(OAK_PLANKS, 0);
      wait(2);
      assertInventoryAtLeast(OAK_PLANKS, 4);
      closeScreen();

      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_LEFT_CLICK, false);
      config.saveAndCloseYacl();
      config.closeModsScreen();
   }

   /**
    * With right-click disabled, right-clicking an overlay button does not craft.
    */
   public void multiOptionOverlayRightClickDisabledNoAction() {
      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_RIGHT_CLICK, false);
      config.saveAndCloseYacl();
      config.closeModsScreen();

      prepare();
      recipeBook.clickMultiOptionButton(1, OAK_PLANKS, BIRCH_PLANKS); // vanilla opens overlay
      wait(2);
      recipeBook.clickOverlayButton(OAK_PLANKS, 1);
      wait(2);
      assertInventoryCount(OAK_PLANKS, 0);
      assertInventoryCount(BIRCH_PLANKS, 0);
      closeScreen();

      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_RIGHT_CLICK, true);
      config.saveAndCloseYacl();
      config.closeModsScreen();
   }
}
