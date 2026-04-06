package com.github.breadmoirai.oneclickcrafting.testmod.suite;

import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.KeyBindingAccessor;
import com.github.breadmoirai.oneclickcrafting.testmod.context.CraftContext;
import com.github.breadmoirai.oneclickcrafting.testmod.OneClickTests;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import java.util.List;

/**
 * Basic crafting and drop-key tests, covering both the recipe-book and stonecutter.
 *
 * <p>Each test that applies equally to both surfaces iterates over both
 * {@link CraftContext} implementations. Tests specific to one surface are
 * labelled accordingly.
 *
 * <p>Replaces: CraftingTests, DropKeyTests, StonecutterTests, StonecutterDropKeyTests.
 */
@SuppressWarnings("UnstableApiUsage")
public class BasicCraftTests extends OneClickTests {

   public BasicCraftTests(ClientGameTestContext context, TestSingleplayerContext world) {
      super(context, world);
   }

   // =========================================================================
   // Left-click tests
   // =========================================================================

   /**
    * Recipe-book only: with "Enable Left Click" OFF (default), left-clicking a
    * recipe button should NOT trigger auto-craft. The ingredient moves to the
    * crafting grid (vanilla behaviour) but no result is produced.
    */
   public void leftClickDisabledNoAction() {
      for (CraftContext ctx : contexts) {
         ctx.prepare();
         ctx.click(0); // left-click
         wait(2);
         assertInventoryEmpty();
         ctx.close();
         assertInventoryExact(ctx.inputItem, ctx.inputCount);
      }
   }

   /**
    * With "Enable Left Click" ON, left-clicking a recipe button auto-crafts
    * and moves the result to the inventory. Runs for both contexts.
    */
   public void leftClickMovesToInventory() {
      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_LEFT_CLICK, true);
      config.saveAndCloseYacl();
      config.closeModsScreen();

      for (CraftContext ctx : contexts) {
         ctx.prepare();
         ctx.click(0); // left-click
         wait(2);
         assertInventoryAtLeast(ctx.outputItem, ctx.outputCount);
         ctx.close();
      }

      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_LEFT_CLICK, false);
      config.saveAndCloseYacl();
      config.closeModsScreen();
   }

   // =========================================================================
   // Right-click tests
   // =========================================================================

   /**
    * With the feature enabled (default), right-clicking a recipe button
    * auto-crafts and moves the result to the inventory. Runs for both contexts.
    */
   public void rightClickMovesToInventory() {
      for (CraftContext ctx : contexts) {
         ctx.prepare();
         ctx.click(1); // right-click
         wait(2);
         assertInventoryAtLeast(ctx.outputItem, ctx.outputCount);
         ctx.close();
      }
   }

   /**
    * With the feature toggle OFF, right-clicking should NOT trigger auto-craft.
    * Uses {@link CraftContext#featureToggleLabel()} to disable the correct toggle
    * for each context (right-click for recipe-book; stonecutter for stonecutter).
    * Runs for both contexts.
    */
   public void rightClickDisabledNoAction() {
      for (CraftContext ctx : contexts) {
         config.openConfigViaModMenu();
         config.setYaclToggle(ctx.featureToggleLabel(), false);
         config.saveAndCloseYacl();
         config.closeModsScreen();

         ctx.prepare();
         ctx.click(1);
         wait(2);
         assertInventoryCount(ctx.outputItem, 0);
         ctx.close();

         config.openConfigViaModMenu();
         config.setYaclToggle(ctx.featureToggleLabel(), true);
         config.saveAndCloseYacl();
         config.closeModsScreen();
      }
   }

   // =========================================================================
   // Drop-key tests
   // =========================================================================

   /**
    * With drop-key enabled (default) and the drop key held during a right-click,
    * the crafted item is thrown to the ground. Runs for both contexts.
    */
   public void dropKeyDropsItem() {
      for (CraftContext ctx : contexts) {
         clearGroundItems();
         ctx.prepare();
         int dropKeyCode = context.computeOnClient(mc -> ((KeyBindingAccessor) mc.options.dropKey).getBoundKey().getCode());
         context.getInput().holdKey(dropKeyCode);
         ctx.click(1);
         wait(2);
         context.getInput().releaseKey(dropKeyCode);
         assertItemOnGround(ctx.outputItem);
         assertInventoryCount(ctx.outputItem, 0);
         ctx.close();
      }
   }

   /**
    * With "Enable Drop Key on Craft" OFF, holding the drop key should NOT drop
    * the result — it moves to the inventory as normal. Runs for both contexts.
    */
   public void dropKeyDisabledMovesToInventory() {
      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_DROP_ENABLE, false);
      config.saveAndCloseYacl();
      config.closeModsScreen();

      for (CraftContext ctx : contexts) {
         clearGroundItems();
         ctx.prepare();
         int dropKeyCode = context.computeOnClient(mc -> ((KeyBindingAccessor) mc.options.dropKey).getBoundKey().getCode());
         context.getInput().holdKey(dropKeyCode);
         ctx.click(1);
         wait(2);
         context.getInput().releaseKey(dropKeyCode);
         assertInventoryAtLeast(ctx.outputItem, ctx.outputCount);
         assertNoItemOnGround(ctx.outputItem);
         ctx.close();
      }

      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_DROP_ENABLE, true);
      config.saveAndCloseYacl();
      config.closeModsScreen();
   }

   /**
    * Holding Shift + drop key drops the crafted result to the ground rather
    * than moving it to the inventory. Runs for both contexts.
    */
   public void shiftDropDropsItem() {
      for (CraftContext ctx : contexts) {
         clearGroundItems();
         ctx.prepare();
         int dropKeyCode = context.computeOnClient(mc -> ((KeyBindingAccessor) mc.options.dropKey).getBoundKey().getCode());
         System.out.println("dropKeyCode = " + dropKeyCode);
         context.getInput().holdShift();
         context.getInput().holdKey(dropKeyCode);
         ctx.click(1);
         wait(2);
         context.getInput().releaseKey(dropKeyCode);
         context.getInput().releaseShift();
         assertItemOnGround(ctx.outputItem);
         assertInventoryCount(ctx.outputItem, 0);
         ctx.close();
      }
   }
}
