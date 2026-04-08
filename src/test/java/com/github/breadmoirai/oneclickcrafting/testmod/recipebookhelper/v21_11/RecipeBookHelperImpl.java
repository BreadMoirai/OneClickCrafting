//? >=1.21.10 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.testmod.v21_11;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.ClientRecipeBookAccessor;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.RecipeBookScreenAccessor;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.RecipeBookWidgetAccessor;
import com.github.breadmoirai.oneclickcrafting.testmod.recipebookhelper.RecipeBookHelper;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.lang.reflect.Field;
import java.util.List;

public class RecipeBookHelperImpl extends RecipeBookHelper {

   public RecipeBookHelperImpl(ClientGameTestContext context) {
      super(context);
   }

   @Override
   public void open() {
      if (context.computeOnClient(mc -> {
         if (!(mc.currentScreen instanceof RecipeBookScreen<?> screen)) {
            throw new AssertionError("openRecipeBook: not in a RecipeBookScreen");
         }
         RecipeBookWidget<?> recipeBook = ((RecipeBookScreenAccessor) screen).getRecipeBook();
         if (!recipeBook.isOpen()) {
            recipeBook.toggleOpen();
            return true;
         }
         return false;
      })) {
         context.waitTick();
      }
   }

   @Override
   public void clickRecipeButton(String targetItemId, int mouseButton) {
      double[] windowCoords = context.computeOnClient(mc -> {
         if (!(mc.currentScreen instanceof RecipeBookScreen<?> screen)) {
            throw new AssertionError("clickRecipeButton: not in a RecipeBookScreen");
         }
         RecipeBookWidget<?> recipeBook = ((RecipeBookScreenAccessor) screen).getRecipeBook();
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
            if (!displayStack.isEmpty() &&
                  Registries.ITEM.getId(displayStack.getItem()).toString().equals(targetItemId)) {
               double guiCx = button.getX() + button.getWidth() / 2.0;
               double guiCy = button.getY() + button.getHeight() / 2.0;
               double scale = mc.getWindow().getScaleFactor();
               return new double[]{guiCx * scale, guiCy * scale};
            }
         }
         // Diagnostic: report what IS visible in the result buttons
         StringBuilder dbg = new StringBuilder(
            "Recipe button not found for item: " + targetItemId);
         dbg.append("; resultButtons.size=").append(buttons.size());
         dbg.append("; recipeBook.isOpen=").append(recipeBook.isOpen());
         dbg.append("; knownRecipes=").append(
            mc.player != null ? ((ClientRecipeBookAccessor) mc.player.getRecipeBook()).getRecipes().size() : -1);
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

   @Override
   public void clickRecipeButtonCraftAll(String targetItemId) {
      context.runOnClient(mc -> {
         if (!(mc.currentScreen instanceof RecipeBookScreen<?> screen)) {
            throw new AssertionError("clickRecipeButtonCraftAll: not in a RecipeBookScreen");
         }
         RecipeBookWidget<?> recipeBook = ((RecipeBookScreenAccessor) screen).getRecipeBook();
         RecipeBookResults recipesArea = getRecipesArea(recipeBook);
         List<AnimatedResultButton> buttons = getResultButtons(recipesArea);
         for (AnimatedResultButton button : buttons) {
            if (button.getResultCollection() == null) continue;
            ItemStack displayStack;
            try { displayStack = button.getDisplayStack(); }
            catch (ArithmeticException e) { continue; }
            if (!displayStack.isEmpty() &&
                  Registries.ITEM.getId(displayStack.getItem()).toString().equals(targetItemId)) {
               ((RecipeBookWidgetAccessor) recipeBook).callSelect(
                  button.getResultCollection(), button.getCurrentId(), true);
               OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(
                  button.getCurrentId().index(), 0);
               return;
            }
         }
         throw new AssertionError("Recipe button not found for item: " + targetItemId);
      });
   }

   @Override
   public void placeLastRecipe() {
      context.runOnClient(mc -> {
         if (!(mc.currentScreen instanceof RecipeBookScreen<?> screen)) return;
         RecipeBookWidgetAccessor accessor = (RecipeBookWidgetAccessor) ((RecipeBookScreenAccessor) screen).getRecipeBook();
         if (accessor.getSelectedRecipeResults() == null || accessor.getSelectedRecipe() == null) return;
         accessor.callSelect(accessor.getSelectedRecipeResults(), accessor.getSelectedRecipe(), false);
      });
   }

   // -------------------------------------------------------------------------
   // Reflection helpers (private field access)
   // -------------------------------------------------------------------------

   private static RecipeBookResults getRecipesArea(RecipeBookWidget<?> widget) {
      try {
         Field field = RecipeBookWidget.class.getDeclaredField("recipesArea");
         field.setAccessible(true);
         return (RecipeBookResults) field.get(widget);
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("Cannot access RecipeBookWidget.recipesArea", e);
      }
   }

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
*///?}
