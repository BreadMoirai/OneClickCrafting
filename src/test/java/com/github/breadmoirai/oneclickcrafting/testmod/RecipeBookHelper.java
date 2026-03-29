package com.github.breadmoirai.oneclickcrafting.testmod;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.lang.reflect.Field;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class RecipeBookHelper {
   ClientGameTestContext context;

   public RecipeBookHelper(ClientGameTestContext context) {
      this.context = context;
   }

   /**
    * Ensures the recipe book is open in the currently active {@link RecipeBookScreen}.
    * If the recipe book is already open, this is a no-op.
    */
   public void open() {
      if (context.computeOnClient(mc -> {
         if (!(mc.currentScreen instanceof RecipeBookScreen<?> screen)) {
            throw new AssertionError("openRecipeBook: not in a RecipeBookScreen");
         }
         RecipeBookWidget<?> recipeBook = screen.recipeBook;
         if (!recipeBook.isOpen()) {
            recipeBook.toggleOpen();
            return true;
         }
         return false;
      })) {
         context.waitTick();
      }
   }

   public void leftClick(Item targetItem) {
      clickRecipeButton(targetItem, 0);
   }

   public void rightClick(Item targetItem) {
      clickRecipeButton(targetItem, 1);
   }

   /**
    * Finds the recipe button displaying {@code targetItem} in the currently open
    * recipe book, positions the cursor over it, and simulates a mouse click.
    *
    * <p>The recipe book must already be open. The button must be visible on the
    * current page (i.e. already unlocked and on page 0 for simple recipes).
    *
    * @param mouseButton {@code 0} for left-click, {@code 1} for right-click
    */
   public void clickRecipeButton(Item targetItem, int mouseButton) {
      double[] windowCoords = context.computeOnClient(mc -> {
         if (!(mc.currentScreen instanceof RecipeBookScreen<?> screen)) {
            throw new AssertionError("clickRecipeButton: not in a RecipeBookScreen");
         }
         RecipeBookWidget<?> recipeBook = screen.recipeBook;
         RecipeBookResults recipesArea = getRecipesArea(recipeBook);
         List<AnimatedResultButton> buttons = getResultButtons(recipesArea);

         for (AnimatedResultButton button : buttons) {
            // Skip buttons without populated results (getDisplayStack divides by results.size())
            if (button.getResultCollection() == null) continue;
            ItemStack displayStack;
            try {
               displayStack = button.getDisplayStack();
            } catch (ArithmeticException e) {
               continue; // results list is empty — button not yet initialized
            }
            if (!displayStack.isEmpty() && displayStack.isOf(targetItem)) {
               double guiCx = button.getX() + button.getWidth() / 2.0;
               double guiCy = button.getY() + button.getHeight() / 2.0;
               double scale = mc.getWindow().getScaleFactor();
               return new double[]{guiCx * scale, guiCy * scale};
            }
         }
         // Diagnostic: report what IS visible in the result buttons
         StringBuilder dbg = new StringBuilder(
            "Recipe button not found for item: " + Registries.ITEM.getId(targetItem));
         dbg.append("; resultButtons.size=").append(buttons.size());
         dbg.append("; recipeBook.isOpen=").append(recipeBook.isOpen());
         dbg.append("; knownRecipes=").append(
            mc.player != null ? mc.player.getRecipeBook().recipes.size() : -1);
         dbg.append("; visible=[");
         for (AnimatedResultButton btn : buttons) {
            if (btn.getResultCollection() == null) {
               dbg.append("null,");
            } else {
               try {
                  dbg.append(Registries.ITEM.getId(btn.getDisplayStack().getItem())).append(",");
               } catch (ArithmeticException e) {
                  dbg.append("emptyResults,");
               }
            }
         }
         dbg.append("]");
         throw new AssertionError(dbg.toString());
      });

      context.getInput().setCursorPos(windowCoords[0], windowCoords[1]);
      context.getInput().pressMouse(mouseButton);
   }

   // -------------------------------------------------------------------------
   // Reflection helpers (private field access)
   // -------------------------------------------------------------------------

   /** Accesses {@code RecipeBookWidget.recipesArea} via reflection. */
   private static RecipeBookResults getRecipesArea(RecipeBookWidget<?> widget) {
      try {
         Field field = RecipeBookWidget.class.getDeclaredField("recipesArea");
         field.setAccessible(true);
         return (RecipeBookResults) field.get(widget);
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("Cannot access RecipeBookWidget.recipesArea", e);
      }
   }

   /** Accesses {@code RecipeBookResults.resultButtons} via reflection. */
   @SuppressWarnings("unchecked")
   private static List<AnimatedResultButton> getResultButtons(RecipeBookResults results) {
      try {
         Field field = RecipeBookResults.class.getDeclaredField("resultButtons");
         field.setAccessible(true);
         return (List<AnimatedResultButton>) field.get(results);
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("Cannot access RecipeBookResults.resultButtons", e);
      }
   }
}
