package com.github.breadmoirai.oneclickcrafting.testmod.suite;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.testmod.VirtualKeyState;
import com.github.breadmoirai.oneclickcrafting.testmod.context.CraftContext;
import com.github.breadmoirai.oneclickcrafting.testmod.OneClickTests;
import com.github.breadmoirai.oneclickcrafting.testmod.context.StonecutterContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;


/**
 * Repeat-last-key tests for the recipe-book (inventory), crafting table, and stonecutter.
 */
@SuppressWarnings("UnstableApiUsage")
public class RepeatLastTests extends OneClickTests {

   public static int REPEAT_KEY_CODE = GLFW.GLFW_KEY_R;

   public RepeatLastTests(ClientGameTestContext context, TestSingleplayerContext world) {
      super(context, world);
   }

   // -------------------------------------------------------------------------
   // Key-binding helpers
   // -------------------------------------------------------------------------

   /**
    * Presses the repeat key once.  When the repeat key is SPACE, also calls
    * {@code placeLastRecipe()} after waiting one tick so the result-slot update
    * that vanilla would fire via its SPACE handling is replicated.
    */
   private void pressRepeatKey() {
      context.getInput().pressKey(REPEAT_KEY_CODE);
   }

   /**
    * Begins holding the repeat key.  When the repeat key is SPACE, also waits
    * one tick and calls {@code placeLastRecipe()} to kick-start the first craft;
    * subsequent crafts chain automatically via the server's grid auto-refill.
    */
   private void holdRepeatKey() {
      context.getInput().holdKey(REPEAT_KEY_CODE);
      VirtualKeyState.hold(REPEAT_KEY_CODE);
   }

   private void releaseRepeatKey() {
      context.getInput().releaseKey(REPEAT_KEY_CODE);
      VirtualKeyState.release(REPEAT_KEY_CODE);
   }

   private void bindRepeatKey() {
      context.runOnClient(mc -> {
         OneClickCraftingMod.getInstance().input.repeatLast.setKey(REPEAT_KEY_CODE);
         KeyMapping.resetMapping();
      });
   }

   private void unbindRepeatKey() {
      context.runOnClient(mc -> {
         OneClickCraftingMod.getInstance().input.repeatLast.setKey(GLFW.GLFW_KEY_UNKNOWN);
         KeyMapping.resetMapping();
      });
   }

   private void setRepeatDelay(int delay) {
      context.runOnClient(mc ->
         OneClickCraftingMod.getInstance().config.setRepeatDelay(delay));
   }

   // -------------------------------------------------------------------------
   // Tests
   // -------------------------------------------------------------------------

   public void repeatLastReCrafts() {
      bindRepeatKey();

      for (CraftContext ctx : contexts) {
         ctx.prepare(3);

         ctx.click(1);
         wait(2);
         if (ctx instanceof StonecutterContext) {
            assertInventoryExact(ctx.outputItem, ctx.outputCount, ctx.inputItem, ctx.inputCount);
         } else {
            assertInventoryExact(ctx.outputItem, ctx.outputCount, ctx.inputItem, ctx.inputCount * 2);
         }

         pressRepeatKey();
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

      unbindRepeatKey();
   }

   public void repeatLastStack() {
      for (CraftContext ctx : contexts) {
         bindRepeatKey();
         setRepeatDelay(0);

         ctx.prepare(64);
         ctx.click(1);
         wait(2);

         holdRepeatKey();
         wait(65);
         releaseRepeatKey();

         ctx.close();
         assertInventoryExact(ctx.outputItem, ctx.outputCount * 64);

         unbindRepeatKey();
         setRepeatDelay(6);
      }
   }

   public void repeatLastStacksFullInventory() {
      for (CraftContext ctx : contexts) {
         bindRepeatKey();
         setRepeatDelay(0);

         ctx.prepare(9 * 64);
         ctx.click(1);
         wait(2);

         context.getInput().holdShift();
         holdRepeatKey();
         wait(24);
         context.getInput().releaseShift();
         releaseRepeatKey();

         ctx.close();
         assertInventoryExact(ctx.outputItem, ctx.outputCount * 9 * 64);

         unbindRepeatKey();
         setRepeatDelay(6);
      }
   }

   public void repeatLastDropKeyDropsManyItems() {
      for (CraftContext ctx : contexts) {
         bindRepeatKey();
         setRepeatDelay(0);

         clearGroundItems();
         ctx.prepare(64);

         holdDrop();
         ctx.click(1);
         wait(2);

         holdRepeatKey();
         wait(66);
         releaseRepeatKey();
         releaseDrop();

         ctx.close();
         assertInventoryEmpty();
         assertItemOnGround(ctx.outputItem);

         unbindRepeatKey();
         setRepeatDelay(6);
      }
   }
}
