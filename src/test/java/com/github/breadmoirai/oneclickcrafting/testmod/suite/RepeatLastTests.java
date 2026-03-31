package com.github.breadmoirai.oneclickcrafting.testmod.suite;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingClient;
import com.github.breadmoirai.oneclickcrafting.testmod.CraftContext;
import com.github.breadmoirai.oneclickcrafting.testmod.OneClickTests;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
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

   private final CraftContext recipeCtx;
   private final CraftContext craftingTableCtx;
   private final CraftContext stoneCtx;
   private final List<CraftContext> contexts;

   public RepeatLastTests(ClientGameTestContext context, TestSingleplayerContext world) {
      super(context, world);
      recipeCtx        = recipeBookContext("oak_planks", Items.OAK_PLANKS, 4);
      craftingTableCtx = craftingTableContext("oak_planks", Items.OAK_PLANKS, 4);
      stoneCtx         = stonecutterContext("minecraft:cobblestone", Items.COBBLESTONE, 2);
      contexts         = List.of(recipeCtx, craftingTableCtx, stoneCtx);
   }

   // -------------------------------------------------------------------------
   // Key-binding helpers
   // -------------------------------------------------------------------------

   private void bindRepeatKey() {
      context.runOnClient(mc -> {
         OneClickCraftingClient.getInstance().repeatLastKey
            .setBoundKey(InputUtil.Type.KEYSYM.createFromCode(REPEAT_KEY_CODE));
         KeyBinding.updateKeysByCode();
      });
   }

   private void unbindRepeatKey() {
      context.runOnClient(mc -> {
         OneClickCraftingClient.getInstance().repeatLastKey
            .setBoundKey(InputUtil.UNKNOWN_KEY);
         KeyBinding.updateKeysByCode();
      });
   }

   private void setRepeatDelay(int delay) {
      context.runOnClient(mc ->
         OneClickCraftingClient.getInstance().config.setRepeatDelay(delay));
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
         ctx.prepare(ctx.outputPerCraft() * 2); // enough for 2 crafts

         ctx.click(1);
         wait(2);
         assertInventoryAtLeast(ctx.result(), ctx.outputPerCraft());

         context.getInput().pressKey(REPEAT_KEY_CODE);
         wait(2);

         assertInventoryAtLeast(ctx.result(), ctx.outputPerCraft() * 2);
         ctx.close();
      }

      unbindRepeatKey();
   }


   /**
    * Holds the repeat-last key long enough to craft 64 batches
    * (64 oak logs → 256 planks), filling 4 inventory slots.
    * Runs for recipe-book and crafting-table contexts.
    */
   public void repeatLastSingleTwoStacks() {
      final int TOTAL_CRAFTS = 64;

      for (CraftContext ctx : contexts) {
         bindRepeatKey();
         setRepeatDelay(0);

         ctx.prepare(TOTAL_CRAFTS * ctx.outputPerCraft());
         ctx.click(1);
         wait(2);

         context.getInput().holdKey(REPEAT_KEY_CODE);
         wait(65);
         context.getInput().releaseKey(REPEAT_KEY_CODE);

         ctx.close();
         assertInventoryExact(ctx.result(), TOTAL_CRAFTS * ctx.outputPerCraft());

         unbindRepeatKey();
         setRepeatDelay(6);
      }
   }


   /**
    * Holds the repeat-last key long enough to craft 576 batches
    * (576 oak logs → 2304 planks), filling all 36 inventory slots.
    * Runs for recipe-book and crafting-table contexts.
    */
   public void repeatLastStacksFullInventory() {
      final int TOTAL_CRAFTS = 576; // 576 logs × 4 planks = 2304 = 36 slots × 64

      for (CraftContext ctx : contexts) {
         bindRepeatKey();
         setRepeatDelay(0);

         ctx.prepare(TOTAL_CRAFTS * ctx.outputPerCraft());
         ctx.click(1);
         wait(2);

         context.getInput().holdShift();
         context.getInput().holdKey(REPEAT_KEY_CODE);
         wait(37);
         context.getInput().releaseShift();
         context.getInput().releaseKey(REPEAT_KEY_CODE);

         ctx.close();
         assertInventoryExact(ctx.result(), TOTAL_CRAFTS * ctx.outputPerCraft());

         unbindRepeatKey();
         setRepeatDelay(6);
      }
   }

   /**
    * Holds the drop key and repeat-last key simultaneously, crafting in rapid
    * succession while dropping every result to the ground. Runs for all three contexts.
    */
   public void repeatLastDropKeyDropsManyItems() {
      doRepeatLastDropKey(recipeCtx,        160, 162); // ceil(160 * 1.01)
      doRepeatLastDropKey(craftingTableCtx, 160, 162);
      doRepeatLastDropKey(stoneCtx,          40, 41);  // ceil(40 * 1.01)
   }

   private void doRepeatLastDropKey(CraftContext ctx, int totalCrafts, int holdTicks) {
      bindRepeatKey();
      setRepeatDelay(0);

      clearGroundItems();
      ctx.prepare(totalCrafts * ctx.outputPerCraft());

      int dropKeyCode = context.computeOnClient(mc -> mc.options.dropKey.boundKey.getCode());

      context.getInput().holdKey(dropKeyCode);
      ctx.click(1);
      wait(2);

      context.getInput().holdKey(REPEAT_KEY_CODE);
      wait(holdTicks);
      context.getInput().releaseKey(REPEAT_KEY_CODE);
      context.getInput().releaseKey(dropKeyCode);

      ctx.close();
      assertInventoryCount(ctx.result(), 0);
      assertItemOnGround(ctx.result());

      unbindRepeatKey();
      setRepeatDelay(6);
   }
}
