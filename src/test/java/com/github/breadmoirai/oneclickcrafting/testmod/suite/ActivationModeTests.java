package com.github.breadmoirai.oneclickcrafting.testmod.suite;

import com.github.breadmoirai.oneclickcrafting.testmod.CraftContext;
import com.github.breadmoirai.oneclickcrafting.testmod.OneClickTests;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.item.Items;

import java.util.List;

/**
 * Activation-mode tests ({@code alwaysOn}, {@code ctrlHold}, {@code altHold}).
 *
 * <p>Each test runs for both the recipe-book and stonecutter contexts.
 *
 * <p>Replaces: ActivationModeTests (recipe-book) + StonecutterActivationModeTests.
 */
@SuppressWarnings("UnstableApiUsage")
public class ActivationModeTests extends OneClickTests {

   private final List<CraftContext> contexts;

   public ActivationModeTests(ClientGameTestContext context, TestSingleplayerContext world) {
      super(context, world);
      contexts = List.of(
         recipeBookContext("oak_planks", Items.OAK_PLANKS, 4),
         craftingTableContext("oak_planks", Items.OAK_PLANKS, 4),
         stonecutterContext("minecraft:cobblestone", Items.COBBLESTONE)
      );
   }

   /**
    * With {@code alwaysOn=false} and no modifier held, right-clicking should NOT
    * trigger auto-craft. Runs for both contexts.
    */
   public void alwaysOffNoModifierNoAction() {
      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_ALWAYS_ON, false);
      config.saveAndCloseYacl();
      config.closeModsScreen();

      for (CraftContext ctx : contexts) {
         ctx.prepare();
         ctx.click(1);
         wait(2);
         assertInventoryCount(ctx.result(), 0);
         ctx.close();
      }

      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_ALWAYS_ON, true);
      config.saveAndCloseYacl();
      config.closeModsScreen();
   }

   /**
    * With {@code alwaysOn=false} and {@code ctrlHold=true}, holding Ctrl while
    * right-clicking should ENABLE auto-craft. Runs for both contexts.
    */
   public void alwaysOffCtrlHoldEnables() {
      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_ALWAYS_ON, false);
      config.saveAndCloseYacl();
      config.closeModsScreen();

      for (CraftContext ctx : contexts) {
         ctx.prepare();
         context.getInput().holdControl();
         ctx.click(1);
         wait(2);
         context.getInput().releaseControl();
         assertInventoryAtLeast(ctx.result(), ctx.outputPerCraft());
         ctx.close();
      }

      config.openConfigViaModMenu();
      config.setYaclToggle(LABEL_ALWAYS_ON, true);
      config.saveAndCloseYacl();
      config.closeModsScreen();
   }

   /**
    * With {@code alwaysOn=true} (default) and {@code ctrlHold=true} (default),
    * holding Ctrl should DISABLE auto-craft. Runs for both contexts.
    */
   public void alwaysOnCtrlHoldDisables() {
      for (CraftContext ctx : contexts) {
         ctx.prepare();
         context.getInput().holdControl();
         ctx.click(1);
         wait(2);
         context.getInput().releaseControl();
         assertInventoryCount(ctx.result(), 0);
         ctx.close();
      }
   }

   /**
    * With {@code alwaysOn=true} (default) and {@code altHold=true} (default),
    * holding Alt should DISABLE auto-craft. Runs for both contexts.
    */
   public void alwaysOnAltHoldDisables() {
      for (CraftContext ctx : contexts) {
         ctx.prepare();
         context.getInput().holdAlt();
         ctx.click(1);
         wait(2);
         context.getInput().releaseAlt();
         assertInventoryCount(ctx.result(), 0);
         ctx.close();
      }
   }
}
