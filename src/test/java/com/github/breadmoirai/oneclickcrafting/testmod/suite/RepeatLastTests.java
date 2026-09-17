package com.github.breadmoirai.oneclickcrafting.testmod.suite;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.VirtualKeyState;
import com.github.breadmoirai.oneclickcrafting.testmod.context.CraftContext;
import com.github.breadmoirai.oneclickcrafting.testmod.OneClickTests;
import com.github.breadmoirai.oneclickcrafting.testmod.context.StonecutterContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;


/**
 * Repeat-last-key tests for the recipe-book (inventory), crafting table, and stonecutter.
 */
@SuppressWarnings("UnstableApiUsage")
public class RepeatLastTests extends OneClickTests {

   public static int REPEAT_KEY_CODE = InputConstants.KEY_R;

   /** Upper bound for holding the repeat key until all crafts are done (slow CI machines need far more
    * ticks than a desktop, so the hold waits on the result instead of a fixed tick count). */
   private static final int REPEAT_TIMEOUT_TICKS = 400;
   /** Keep holding briefly after the last craft: releasing mid-attempt can leave the client inventory
    * out of sync with the server until the next repeat attempt resyncs it. */
   private static final int REPEAT_SETTLE_TICKS = 20;

   public RepeatLastTests(ClientGameTestContext context, TestSingleplayerContext world) {
      super(context, world);
   }

   private void setRepeatDelay(int delay) {
      context.runOnClient(mc ->
         OneClickCraftingMod.getInstance().config.setRepeatDelay(delay));
   }

   // -------------------------------------------------------------------------
   // Tests
   // -------------------------------------------------------------------------

   public void repeatLastReCrafts() {
      input.bindRepeatKey();

      for (CraftContext ctx : contexts) {
         ctx.prepare(3);

         ctx.click(RIGHT);
         wait(2);
         if (ctx instanceof StonecutterContext) {
            assertInventoryExact(ctx.outputItem, ctx.outputCount, ctx.inputItem, ctx.inputCount);
         } else {
            assertInventoryExact(ctx.outputItem, ctx.outputCount, ctx.inputItem, ctx.inputCount * 2);
         }

         input.pressRepeatKey();
         wait(2);

         if (ctx instanceof StonecutterContext) {
            assertInventoryExact(ctx.outputItem, ctx.outputCount * 2);
         } else {
            assertInventoryExact(ctx.outputItem, ctx.outputCount * 2, ctx.inputItem, ctx.inputCount);
         }
         ctx.close();
         wait(2);
         if (ctx instanceof StonecutterContext) {
            assertInventoryExact(ctx.outputItem, ctx.outputCount * 2, ctx.inputItem, ctx.inputCount);
         }
      }

      input.unbindRepeatKey();
   }

   public void repeatLastStack() {
      for (CraftContext ctx : contexts) {
         input.bindRepeatKey();
         setRepeatDelay(0);

         ctx.prepare(64);
         ctx.click(RIGHT);
         wait(2);

         input.holdRepeatKey();
         waitUntil(mc -> inventoryCount(mc, ctx.outputItem) >= ctx.outputCount * 64, REPEAT_TIMEOUT_TICKS);
         wait(REPEAT_SETTLE_TICKS);
         input.releaseRepeatKey();

         ctx.close();
         assertInventoryExact(ctx.outputItem, ctx.outputCount * 64);

         input.unbindRepeatKey();
         setRepeatDelay(6);
      }
   }

   public void repeatLastStacksFullInventory() {
      for (CraftContext ctx : contexts) {
         input.bindRepeatKey();
         setRepeatDelay(0);

         ctx.prepare(9 * 64);
         ctx.click(RIGHT);
         wait(2);

         input.holdShift();
         input.holdRepeatKey();
         waitUntil(mc -> inventoryCount(mc, ctx.outputItem) >= ctx.outputCount * 9 * 64, REPEAT_TIMEOUT_TICKS);
         wait(REPEAT_SETTLE_TICKS);
         input.releaseShift();
         input.releaseRepeatKey();

         ctx.close();
         assertInventoryExact(ctx.outputItem, ctx.outputCount * 9 * 64);

         input.unbindRepeatKey();
         setRepeatDelay(6);
      }
   }

   public void repeatLastDropKeyDropsManyItems() {
      for (CraftContext ctx : contexts) {
         input.bindRepeatKey();
         setRepeatDelay(0);

         clearGroundItems();
         ctx.prepare(64);

         input.holdDrop();
         ctx.click(RIGHT);
         wait(2);

         input.holdRepeatKey();
         waitUntil(mc -> groundCount(mc, ctx.outputItem) >= ctx.outputCount * 64, REPEAT_TIMEOUT_TICKS);
         wait(REPEAT_SETTLE_TICKS);
         input.releaseRepeatKey();
         input.releaseDrop();

         ctx.close();
         assertInventoryEmpty();
         assertItemOnGround(ctx.outputItem);

         input.unbindRepeatKey();
         setRepeatDelay(6);
      }
   }
}
