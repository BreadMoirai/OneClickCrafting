package com.github.breadmoirai.oneclickcrafting.testmod.suite;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.KeyBindingAccessor;
import com.github.breadmoirai.oneclickcrafting.testmod.context.CraftContext;
import com.github.breadmoirai.oneclickcrafting.testmod.OneClickTests;
import com.github.breadmoirai.oneclickcrafting.testmod.context.StonecutterContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Repeat-last-key tests for the recipe-book (inventory), crafting table, and stonecutter.
 *
 * <p>Replaces: RepeatLastTests (recipe-book) + StonecutterRepeatLastTests.
 */
@SuppressWarnings("UnstableApiUsage")
public class RepeatLastTests extends OneClickTests {

   private static final int REPEAT_KEY_CODE = GLFW.GLFW_KEY_R;

   public RepeatLastTests(ClientGameTestContext context, TestSingleplayerContext world) {
      super(context, world);
   }

   // -------------------------------------------------------------------------
   // Key-binding helpers
   // -------------------------------------------------------------------------

   private void bindRepeatKey() {
      context.runOnClient(mc -> {
         OneClickCraftingMod.getInstance().input.repeatLast
            .setKey(REPEAT_KEY_CODE);
         KeyBinding.updateKeysByCode();
      });
   }

   private void unbindRepeatKey() {
      context.runOnClient(mc -> {
         OneClickCraftingMod.getInstance().input.repeatLast
            .setKey(GLFW.GLFW_KEY_UNKNOWN);
         KeyBinding.updateKeysByCode();
      });
   }

   private void setRepeatDelay(int delay) {
      context.runOnClient(mc ->
         OneClickCraftingMod.getInstance().config.setRepeatDelay(delay));
   }

   // -------------------------------------------------------------------------
   // Tests
   // -------------------------------------------------------------------------

   /**
    * After clicking a recipe button once, pressing the repeat-last key should
    * re-craft the same recipe. Runs for all three contexts.
    */
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

         context.getInput().pressKey(REPEAT_KEY_CODE);
         wait(2);

         if (ctx instanceof StonecutterContext) {
            assertInventoryExact(ctx.outputItem, ctx.outputCount * 2);
            ctx.close();
            wait(2);
         }
         assertInventoryExact(ctx.outputItem, ctx.outputCount * 2, ctx.inputItem, ctx.inputCount);
      }

      unbindRepeatKey();
   }


   /**
    * Holds the repeat-last key long enough to craft 64 batches
    * (64 oak logs → 256 planks), filling 4 inventory slots.
    * Runs for recipe-book and crafting-table contexts.
    */
   public void repeatLastSingleTwoStacks() {
      for (CraftContext ctx : contexts) {
         bindRepeatKey();
         setRepeatDelay(0);

         ctx.prepare(64);
         ctx.click(1);
         wait(2);

         context.getInput().holdKey(REPEAT_KEY_CODE);
         wait(65);
         context.getInput().releaseKey(REPEAT_KEY_CODE);

         ctx.close();
         assertInventoryExact(ctx.outputItem, ctx.outputCount * 64);

         unbindRepeatKey();
         setRepeatDelay(6);
      }
   }


   /**
    * Holds the repeat-last key long enough to craft 9 batches
    * (9 * 64 oak logs → 2304 planks), filling all 36 inventory slots.
    * Runs for recipe-book and crafting-table contexts.
    */
   public void repeatLastStacksFullInventory() {
      for (CraftContext ctx : contexts) {
         bindRepeatKey();
         setRepeatDelay(0);

         ctx.prepare(9 * 64);
         ctx.click(1);
         wait(2);

         context.getInput().holdShift();
         context.getInput().holdKey(REPEAT_KEY_CODE);
         wait(37);
         context.getInput().releaseShift();
         context.getInput().releaseKey(REPEAT_KEY_CODE);

         ctx.close();
         assertInventoryExact(ctx.outputItem, ctx.outputCount * 9 * 64);

         unbindRepeatKey();
         setRepeatDelay(6);
      }
   }

   /**
    * Holds the drop key and repeat-last key simultaneously, crafting in rapid
    * succession while dropping every result to the ground. Runs for all three contexts.
    */
   public void repeatLastDropKeyDropsManyItems() {
      for (CraftContext ctx : contexts) {
         bindRepeatKey();
         setRepeatDelay(0);

         clearGroundItems();
         ctx.prepare(96);

         int dropKeyCode = context.computeOnClient(
            mc -> ((KeyBindingAccessor) mc.options.dropKey).getBoundKey().getCode());

         context.getInput().holdKey(dropKeyCode);
         ctx.click(1);
         wait(2);

         context.getInput().holdKey(REPEAT_KEY_CODE);
         wait(99);
         context.getInput().releaseKey(REPEAT_KEY_CODE);
         context.getInput().releaseKey(dropKeyCode);

         ctx.close();
         assertInventoryEmpty();
         assertItemOnGround(ctx.outputItem);

         unbindRepeatKey();
         setRepeatDelay(6);
      }
   }
}
