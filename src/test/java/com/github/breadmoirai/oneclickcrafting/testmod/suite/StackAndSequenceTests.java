package com.github.breadmoirai.oneclickcrafting.testmod.suite;

import com.github.breadmoirai.oneclickcrafting.testmod.CraftContext;
import com.github.breadmoirai.oneclickcrafting.testmod.OneClickTests;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.item.Items;

import java.util.List;

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

   private final CraftContext recipeCtx;
   private final CraftContext craftingTableCtx;
   private final CraftContext stoneCtx;
   private final List<CraftContext> contexts;

   public StackAndSequenceTests(ClientGameTestContext context, TestSingleplayerContext world) {
      super(context, world);
      recipeCtx        = recipeBookContext("oak_planks", Items.OAK_PLANKS, 4);
      craftingTableCtx = craftingTableContext("oak_planks", Items.OAK_PLANKS, 4);
      stoneCtx         = stonecutterContext("minecraft:cobblestone", Items.COBBLESTONE, 2);
      contexts         = List.of(recipeCtx, craftingTableCtx, stoneCtx);
   }

   private void enableLeftClick() {
      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_LEFT_CLICK, true);
      config.saveAndCloseYacl();
      config.closeModsScreen();
   }

   private void disableLeftClick() {
      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_LEFT_CLICK, false);
      config.saveAndCloseYacl();
      config.closeModsScreen();
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
         ctx.prepare(3 * ctx.outputPerCraft());

         ctx.click(1); wait(2);
         ctx.click(1); wait(2);
         ctx.click(1); wait(2);

         ctx.close();
         assertInventoryAtLeast(ctx.result(), 3 * ctx.outputPerCraft());
      }
   }

   /**
    * Sixteen sequential right-clicks accumulate 16 batches in the inventory.
    * Runs for both contexts.
    */
   public void rightClickManyTimes() {
      final int CRAFTS = 16;
      for (CraftContext ctx : contexts) {
         ctx.prepare(CRAFTS * ctx.outputPerCraft());

         for (int i = 0; i < CRAFTS; i++) {
            ctx.click(1);
            wait(2);
         }

         ctx.close();
         assertInventoryAtLeast(ctx.result(), CRAFTS * ctx.outputPerCraft());
      }
   }

   /**
    * Verifies that crafted results accumulate into existing partial/full stacks.
    * Runs for recipe-book and crafting-table contexts.
    *
    * <p>Partial-stack scenario: pre-fill inventory with 60 planks (4 short of a
    * full stack). Shift+left fills the partial stack to 64; right-click starts a
    * new stack of 4. Expected total: 68.
    *
    * <p>Full-stack scenario: pre-fill with 64 planks (exactly one full stack).
    * Shift+left starts a new stack of 4; right-click adds 4 more to that stack.
    * Expected total: 72.
    */
   public void stackAccumulationShiftLeftRight() {
      enableLeftClick();

      for (CraftContext ctx : List.of(recipeCtx, craftingTableCtx)) {
         // --- partial stack: 60 planks already in inventory ---
         clearInventory();
         giveItem("minecraft:oak_planks", 60);
         recipe("oak_planks"); // unlock recipe; we give ingredients separately
         giveItem("minecraft:oak_log", 2);
         waitTick();
         ctx.open();

         context.getInput().holdShift();
         ctx.click(0); // shift+left: 60 + 4 = fills the partial stack to 64
         context.getInput().releaseShift();
         wait(2);
         ctx.click(1); // right: 4 more → new stack
         wait(2);
         ctx.close();
         assertInventoryAtLeast(ctx.result(), 68);

         // --- full stack: 64 planks already in inventory ---
         clearInventory();
         giveItem("minecraft:oak_planks", 64);
         recipe("oak_planks");
         giveItem("minecraft:oak_log", 2);
         waitTick();
         ctx.open();

         context.getInput().holdShift();
         ctx.click(0); // shift+left: full slot → new stack of 4
         context.getInput().releaseShift();
         wait(2);
         ctx.click(1); // right: 4 more → same new stack = 8
         wait(2);
         ctx.close();
         assertInventoryAtLeast(ctx.result(), 72);
      }

      disableLeftClick();
   }

   // =========================================================================
   // Stack accumulation — context-specific
   // =========================================================================

   /**
    * Recipe-book and crafting-table: a single shift+left-click crafts one batch
    * and moves the result to the inventory, leaving one unconsumed log behind.
    */
   public void shiftLeftClickCraftsOnce() {
      enableLeftClick();

      for (CraftContext ctx : List.of(recipeCtx, craftingTableCtx)) {
         ctx.prepare(ctx.outputPerCraft() * 2); // 2 logs; only 1 consumed by shift+left

         context.getInput().holdShift();
         ctx.click(0); // shift+left
         context.getInput().releaseShift();
         wait(2);

         ctx.close();
         assertInventoryExact(Items.OAK_LOG, 1, ctx.result(), ctx.outputPerCraft());
      }

      disableLeftClick();
   }

   /**
    * Stonecutter only: a single shift+left-click moves the result to the inventory
    * rather than dropping it to the ground.
    */
   public void shiftLeftClickMovesToInventory() {
      enableLeftClick();

      clearInventory();
      clearGroundItems();
      giveItem("minecraft:cobblestone", 1);
      wait(2);

      openStonecutter();
      putItemInInputSlot(Items.COBBLESTONE);
      var result = getRecipeResult(0);

      context.getInput().holdShift();
      clickRecipeButton(0, 0); // shift+left-click
      context.getInput().releaseShift();
      wait(2);

      closeScreen();
      assertInventoryAtLeast(result, 1);
      assertNoItemOnGround(result);

      disableLeftClick();
   }

   // =========================================================================
   // Click sequences — shared
   // =========================================================================

   /**
    * Left-click followed by right-click (left → right) with both buttons enabled.
    * Runs for both contexts.
    */
   public void leftThenRightSequence() {
      enableLeftClick();

      for (CraftContext ctx : contexts) {
         ctx.prepare(ctx.outputPerCraft() * 2);
         ctx.click(0); wait(2);
         ctx.click(1); wait(2);
         ctx.close();
         assertInventoryAtLeast(ctx.result(), ctx.outputPerCraft() * 2);
      }

      disableLeftClick();
   }

   /**
    * Three left-clicks followed by one right-click with both buttons enabled.
    * Runs for both contexts.
    */
   public void leftLeftLeftRightSequence() {
      enableLeftClick();

      for (CraftContext ctx : contexts) {
         ctx.prepare(ctx.outputPerCraft() * 4);
         ctx.click(0); wait(2);
         ctx.click(0); wait(2);
         ctx.click(0); wait(2);
         ctx.click(1); wait(2);
         ctx.close();
         assertInventoryAtLeast(ctx.result(), ctx.outputPerCraft() * 4);
      }

      disableLeftClick();
   }

   // =========================================================================
   // Click sequences — context-specific (shift+left behaviour differs)
   // =========================================================================

   /**
    * Recipe-book and crafting-table: shift+left-click followed by a right-click,
    * each producing one batch. Both results land in the inventory.
    */
   public void shiftLeftThenRightSequenceRecipeBook() {
      enableLeftClick();

      for (CraftContext ctx : List.of(recipeCtx, craftingTableCtx)) {
         ctx.prepare(ctx.outputPerCraft() * 2); // 2 logs

         context.getInput().holdShift();
         ctx.click(0); // shift+left
         context.getInput().releaseShift();
         wait(2);

         ctx.click(1); // right
         wait(2);

         ctx.close();
         assertInventoryExact(ctx.result(), ctx.outputPerCraft() * 2);
      }

      disableLeftClick();
   }

   // =========================================================================
   // Extended accumulation and batch sequences — recipe-book / crafting-table
   // =========================================================================

   /**
    * Seven sequential left-clicks followed by one right-click craft 8 batches
    * (= 8 logs worth of oak planks) in total.
    * Runs for recipe-book and crafting-table contexts.
    */
   public void leftClicksAccumulateThenRight() {
      enableLeftClick();

      for (CraftContext ctx : List.of(recipeCtx, craftingTableCtx)) {
         ctx.prepare(ctx.outputPerCraft() * 8); // 8 logs

         for (int i = 0; i < 7; i++) { ctx.click(0); wait(2); }
         ctx.click(1); wait(2);

         ctx.close();
         assertInventoryAtLeast(ctx.result(), ctx.outputPerCraft() * 8);
      }

      disableLeftClick();
   }

   /**
    * Shift+left-click crafts one batch and moves it to inventory; the subsequent
    * right-click crafts one more batch. Total: 2 batches in inventory.
    * Runs for recipe-book and crafting-table contexts.
    */
   public void shiftLeftStagedIngredientsThenRight() {
      enableLeftClick();

      for (CraftContext ctx : List.of(recipeCtx, craftingTableCtx)) {
         ctx.prepare(ctx.outputPerCraft() * 8); // 8 logs; only 2 consumed

         context.getInput().holdShift();
         ctx.click(0); // shift+left: 1 craft
         context.getInput().releaseShift();
         wait(2);

         ctx.click(1); // right: 1 craft
         wait(2);

         ctx.close();
         assertInventoryAtLeast(ctx.result(), ctx.outputPerCraft() * 2);
      }

      disableLeftClick();
   }

   /**
    * Shift+left-click followed by a right-click with a large ingredient pool.
    * Each click crafts exactly one batch; both are moved to inventory.
    *
    * <p>Variant A: 64 primary logs + 64 extra logs in inventory.
    * <p>Variant B: 64 logs only.
    *
    * Runs for recipe-book and crafting-table contexts.
    */
   public void shiftLeftStages64LogsThenRight() {
      enableLeftClick();

      for (CraftContext ctx : List.of(recipeCtx, craftingTableCtx)) {
         // Variant A — 64 primary logs + 64 extra logs in inventory
         clearInventory();
         recipe("oak_planks");
         giveItem("minecraft:oak_log", 64);
         giveItem("minecraft:oak_log", 64);
         waitTick();
         ctx.open();

         context.getInput().holdShift();
         ctx.click(0); // shift+left
         context.getInput().releaseShift();
         wait(2);

         ctx.click(1); // right
         wait(2);

         ctx.close();
         assertInventoryAtLeast(ctx.result(), ctx.outputPerCraft() * 2); // shift+left + right = 2 crafts

         // Variant B — 64 logs only, nothing else in inventory
         clearInventory();
         recipe("oak_planks");
         giveItem("minecraft:oak_log", 64);
         waitTick();
         ctx.open();

         context.getInput().holdShift();
         ctx.click(0); // shift+left
         context.getInput().releaseShift();
         wait(2);

         ctx.click(1); // right
         wait(2);

         ctx.close();
         assertInventoryAtLeast(ctx.result(), ctx.outputPerCraft() * 2); // shift+left + right = 2 crafts
      }

      disableLeftClick();
   }

   /**
    * Eight left-clicks each craft one batch to inventory; a subsequent
    * shift+right-click crafts one more batch, yielding 9 batches total.
    * Runs for recipe-book and crafting-table contexts.
    */
   public void leftClicksAccumulateThenShiftRight() {
      enableLeftClick();

      for (CraftContext ctx : List.of(recipeCtx, craftingTableCtx)) {
         ctx.prepare(ctx.outputPerCraft() * 64); // 64 logs; 9 consumed

         for (int i = 0; i < 8; i++) { ctx.click(0); wait(2); }

         context.getInput().holdShift();
         ctx.click(1); // shift+right: 1 more craft
         context.getInput().releaseShift();
         wait(2);

         ctx.close();
         assertInventoryAtLeast(ctx.result(), ctx.outputPerCraft() * 9); // 8 left + 1 shift+right
      }

      disableLeftClick();
   }

   /**
    * Stonecutter: shift+left-click followed by a right-click with both buttons
    * enabled. The stonecutter's shift+left batches all input-slot items, so a
    * fresh cobblestone must be seeded before the right-click.
    */
   public void shiftLeftThenRightSequenceStonecutter() {
      enableLeftClick();

      clearInventory();
      giveItem("minecraft:cobblestone", 1);
      wait(2);

      openStonecutter();
      putItemInInputSlot(Items.COBBLESTONE); // QUICK_MOVE 1 → input
      var expectedResult = getRecipeResult(0);

      context.getInput().holdShift();
      clickRecipeButton(0, 0); // shift+left
      context.getInput().releaseShift();
      wait(2); // wait for the full batch sequence to complete

      // Give 1 more cobblestone and seed the input so the right-click can fire
      giveItem("minecraft:cobblestone", 1);
      wait(2);
      putItemInInputSlot(Items.COBBLESTONE);

      clickRecipeButton(1, 0); // right-click
      wait(2);

      closeScreen();
      assertInventoryAtLeast(expectedResult, 2);

      disableLeftClick();
   }
}
