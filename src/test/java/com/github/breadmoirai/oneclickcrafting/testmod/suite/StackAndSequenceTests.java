package com.github.breadmoirai.oneclickcrafting.testmod.suite;

import com.github.breadmoirai.oneclickcrafting.testmod.context.CraftContext;
import com.github.breadmoirai.oneclickcrafting.testmod.OneClickTests;
import com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.VirtualKeyState;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import org.lwjgl.glfw.GLFW;


/**
 * Stack-accumulation and click-sequence tests for both the recipe-book and stonecutter.
 *
 * <p>Tests that share identical logic across both surfaces use a {@link CraftContext}
 * loop. Tests whose mid-test mechanics differ per surface are kept separate.
 *
 * <p>Replaces: StackAndSequenceTests (recipe-book) + StonecutterStackAndSequenceTests.
 */
@SuppressWarnings("UnstableApiUsage")
public class StackAndSequenceTests extends OneClickTests {

   public StackAndSequenceTests(ClientGameTestContext context, TestSingleplayerContext world) {
      super(context, world);
   }

   // =========================================================================
   // Stack accumulation — shared
   // =========================================================================

   /**
    * Three sequential right-clicks accumulate 3 batches in the inventory.
    * Runs for both contexts.
    */
   public void rightClickPartialStack() {
      for (CraftContext ctx : contexts) {
         ctx.prepare(3);

         ctx.click(1);
         wait(2);
         ctx.click(1);
         wait(2);
         ctx.click(1);
         wait(2);

         ctx.close();
         assertInventoryExact(ctx.outputItem, ctx.outputCount * 3);
      }
   }

   /**
    * Sixteen sequential right-clicks accumulate 16 batches in the inventory.
    * Runs for both contexts.
    */
   public void rightClickManyTimes() {
      final int CRAFTS = 16;
      for (CraftContext ctx : contexts) {
         ctx.prepare(CRAFTS);

         for (int i = 0; i < CRAFTS; i++) {
            ctx.click(1);
            wait(2);
         }

         ctx.close();
         assertInventoryExact(ctx.outputItem, ctx.outputCount * CRAFTS);
      }
   }

   public void stackAccumulation() {
      for (CraftContext ctx : craftingContexts) {
         ctx.prepare(64 - ctx.inputCount);

         input.holdShift();
         System.out.println("VirtualKeyState.isHeld(GLFW.GLFW_KEY_LEFT_SHIFT) = " + VirtualKeyState.isHeld(GLFW.GLFW_KEY_LEFT_SHIFT));
         ctx.click(0);
         wait(2);
         input.releaseShift();
         giveItem(ctx.inputItem, ctx.inputCount);
         ctx.click(1);
         wait(2);
         assertInventoryExact(ctx.outputItem, 64 * ctx.outputCount);

         ctx.close();
      }
   }

   public void stackAccumulated() {
      for (CraftContext ctx : craftingContexts) {
         ctx.prepare(64);

         input.holdShift();
         ctx.click(0);
         input.releaseShift();
         wait(2);

         ctx.click(1);
         wait(2);
         assertInventoryExact(ctx.outputItem, 64 * ctx.outputCount);

         ctx.close();
      }
   }

   public void stackAccumulationPartial() {
      for (CraftContext ctx : craftingContexts) {
         ctx.prepare(32 - ctx.inputCount);

         input.holdShift();
         ctx.click(0);
         input.releaseShift();
         wait(2);
         giveItem(ctx.inputItem, ctx.inputCount);
         ctx.click(1);
         wait(2);
         assertInventoryExact(ctx.outputItem, 32 * ctx.outputCount);

         ctx.close();
      }
   }

   public void stackAccumulatedPartial() {
      for (CraftContext ctx : craftingContexts) {
         ctx.prepare(32);

         input.holdShift();
         ctx.click(0);
         input.releaseShift();
         wait(2);
         ctx.click(1);
         wait(2);
         assertInventoryExact(ctx.outputItem, 32 * ctx.outputCount);

         ctx.close();
      }
   }

   // =========================================================================
   // Click sequences — shared
   // =========================================================================

   /**
    * Left-click followed by right-click (left → right) with both buttons enabled.
    * Runs for both contexts.
    */
   public void leftThenRightSequence() {
      for (CraftContext ctx : craftingContexts) {
         ctx.prepare(2);
         ctx.click(0);
         wait(2);
         ctx.click(1);
         wait(2);
         ctx.close();
         assertInventoryExact(ctx.outputItem, ctx.outputCount * 2);
      }
   }

   /**
    * Three left-clicks followed by one right-click with both buttons enabled.
    * Runs for both contexts.
    */
   public void leftLeftLeftRightSequence() {
      for (CraftContext ctx : craftingContexts) {
         ctx.prepare(4);
         ctx.click(0);
         wait(2);
         ctx.click(0);
         wait(2);
         ctx.click(0);
         wait(2);
         ctx.click(1);
         wait(2);
         ctx.close();
         assertInventoryExact(ctx.outputItem, ctx.outputCount * 4);
      }
   }
}
