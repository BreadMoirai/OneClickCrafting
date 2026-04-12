package com.github.breadmoirai.oneclickcrafting.testmod.suite;

import com.github.breadmoirai.oneclickcrafting.testmod.context.CraftContext;
import com.github.breadmoirai.oneclickcrafting.testmod.OneClickTests;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import java.util.List;

/**
 * Basic crafting and drop-key tests, covering both the recipe-book and stonecutter.
 */
@SuppressWarnings("UnstableApiUsage")
public class BasicCraftTests extends OneClickTests {

   public BasicCraftTests(ClientGameTestContext context, TestSingleplayerContext world) {
      super(context, world);
   }

   // =========================================================================
   // Left-click tests
   // =========================================================================

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

   public void rightClickMovesToInventory() {
      for (CraftContext ctx : contexts) {
         ctx.prepare();
         ctx.click(1); // right-click
         wait(2);
         assertInventoryAtLeast(ctx.outputItem, ctx.outputCount);
         ctx.close();
      }
   }

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

   public void dropKeyDropsItem() {
      for (CraftContext ctx : contexts) {
         clearGroundItems();
         ctx.prepare();
         input.holdDrop();
         ctx.click(1);
         wait(2);
         input.releaseDrop();
         assertItemOnGround(ctx.outputItem);
         assertInventoryCount(ctx.outputItem, 0);
         ctx.close();
      }
   }

   public void dropKeyDisabledMovesToInventory() {
      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_DROP_ENABLE, false);
      config.saveAndCloseYacl();
      config.closeModsScreen();

      for (CraftContext ctx : contexts) {
         clearGroundItems();
         ctx.prepare();
         input.holdDrop();
         ctx.click(1);
         wait(2);
         input.releaseDrop();
         assertInventoryAtLeast(ctx.outputItem, ctx.outputCount);
         assertNoItemOnGround(ctx.outputItem);
         ctx.close();
      }

      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_DROP_ENABLE, true);
      config.saveAndCloseYacl();
      config.closeModsScreen();
   }

   public void shiftDropDropsItem() {
      for (CraftContext ctx : contexts) {
         clearGroundItems();
         ctx.prepare();
         input.holdShift();
         input.holdDrop();
         ctx.click(1);
         wait(2);
         input.releaseDrop();
         input.releaseShift();
         assertItemOnGround(ctx.outputItem);
         assertInventoryCount(ctx.outputItem, 0);
         ctx.close();
      }
   }
}
