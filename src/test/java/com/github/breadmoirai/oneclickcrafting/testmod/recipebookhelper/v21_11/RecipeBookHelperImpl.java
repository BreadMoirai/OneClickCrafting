//? >=1.21.10 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.testmod.recipebookhelper.v26_1;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.mixin.ClientRecipeBookAccessor;
import com.github.breadmoirai.oneclickcrafting.mixin.AbstractRecipeBookScreenAccessor;
import com.github.breadmoirai.oneclickcrafting.mixin.RecipeBookComponentAccessor;
import com.github.breadmoirai.oneclickcrafting.testmod.recipebookhelper.RecipeBookHelper;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class RecipeBookHelperImpl extends RecipeBookHelper {

   public RecipeBookHelperImpl(ClientGameTestContext context) {
      super(context);
   }

   @Override
   public void open() {
      if (context.computeOnClient(mc -> {
         if (!(mc.screen instanceof AbstractRecipeBookScreen<? extends RecipeBookMenu> screen)) {
            throw new AssertionError("openRecipeBook: not in an AbstractRecipeBookScreen");
         }
         RecipeBookComponent<?> recipeBook = ((AbstractRecipeBookScreenAccessor) screen).getRecipeBookComponent();
         if (!recipeBook.isVisible()) {
            recipeBook.toggleVisibility();
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
         if (!(mc.screen instanceof AbstractRecipeBookScreen<? extends RecipeBookMenu> screen)) {
            throw new AssertionError("clickRecipeButton: not in an AbstractRecipeBookScreen");
         }
         RecipeBookComponent<?> recipeBook = ((AbstractRecipeBookScreenAccessor) screen).getRecipeBookComponent();
         RecipeBookPage recipesArea = getRecipesArea(recipeBook);
         List<RecipeButton> buttons = getResultButtons(recipesArea);
         Map<RecipeDisplayId, RecipeDisplayEntry> known =
            ((ClientRecipeBookAccessor) mc.player.getRecipeBook()).getKnown();
         ContextMap ctx = SlotDisplayContext.fromLevel(mc.level);

         for (RecipeButton button : buttons) {
            RecipeDisplayId id = button.getCurrentRecipe();
            if (id == null) continue;
            RecipeDisplayEntry entry = known.get(id);
            if (entry == null) continue;
            ItemStack stack = entry.display().result().resolveForFirstStack(ctx);
            if (!stack.isEmpty() &&
                  BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(targetItemId)) {
               double guiCx = button.getX() + button.getWidth() / 2.0;
               double guiCy = button.getY() + button.getHeight() / 2.0;
               double scale = mc.getWindow().getGuiScale();
               return new double[]{guiCx * scale, guiCy * scale};
            }
         }
         // Diagnostic: report what IS visible in the result buttons
         StringBuilder dbg = new StringBuilder(
            "Recipe button not found for item: " + targetItemId);
         dbg.append("; buttons.size=").append(buttons.size());
         dbg.append("; recipeBook.isVisible=").append(recipeBook.isVisible());
         dbg.append("; knownRecipes=").append(
            mc.player != null ? known.size() : -1);
         dbg.append("; visible=[");
         for (RecipeButton btn : buttons) {
            RecipeDisplayId id = btn.getCurrentRecipe();
            if (id == null) {
               dbg.append("null,");
            } else {
               RecipeDisplayEntry entry = known.get(id);
               if (entry == null) {
                  dbg.append("unknownId,");
               } else {
                  ItemStack s = entry.display().result().resolveForFirstStack(ctx);
                  dbg.append(BuiltInRegistries.ITEM.getKey(s.getItem())).append(",");
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
         if (!(mc.screen instanceof AbstractRecipeBookScreen<? extends RecipeBookMenu> screen)) {
            throw new AssertionError("clickRecipeButtonCraftAll: not in an AbstractRecipeBookScreen");
         }
         RecipeBookComponent<?> recipeBook = ((AbstractRecipeBookScreenAccessor) screen).getRecipeBookComponent();
         RecipeBookPage recipesArea = getRecipesArea(recipeBook);
         List<RecipeButton> buttons = getResultButtons(recipesArea);
         Map<RecipeDisplayId, RecipeDisplayEntry> known =
            ((ClientRecipeBookAccessor) mc.player.getRecipeBook()).getKnown();
         ContextMap ctx = SlotDisplayContext.fromLevel(mc.level);

         for (RecipeButton button : buttons) {
            RecipeDisplayId id = button.getCurrentRecipe();
            if (id == null) continue;
            RecipeDisplayEntry entry = known.get(id);
            if (entry == null) continue;
            ItemStack stack = entry.display().result().resolveForFirstStack(ctx);
            if (!stack.isEmpty() &&
                  BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(targetItemId)) {
               ((RecipeBookComponentAccessor) recipeBook).callTryPlaceRecipe(
                  button.getCollection(), button.getCurrentRecipe(), true);
               OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(
                  button.getCurrentRecipe().index(), 0);
               return;
            }
         }
         throw new AssertionError("Recipe button not found for item: " + targetItemId);
      });
   }

   @Override
   public void placeLastRecipe() {
      context.runOnClient(mc -> {
         if (!(mc.screen instanceof AbstractRecipeBookScreen<? extends RecipeBookMenu> screen)) return;
         RecipeBookComponentAccessor accessor = (RecipeBookComponentAccessor) ((AbstractRecipeBookScreenAccessor) screen).getRecipeBookComponent();
         if (accessor.getLastRecipeCollection() == null || accessor.getLastRecipe() == null) return;
         accessor.callTryPlaceRecipe(accessor.getLastRecipeCollection(), accessor.getLastRecipe(), false);
      });
   }

   // -------------------------------------------------------------------------
   // Reflection helpers (private field access)
   // -------------------------------------------------------------------------

   private static RecipeBookPage getRecipesArea(RecipeBookComponent<?> component) {
      try {
         Field field = RecipeBookComponent.class.getDeclaredField("recipeBookPage");
         field.setAccessible(true);
         return (RecipeBookPage) field.get(component);
      } catch (NoSuchFieldException ignored) {
         throw new RuntimeException(
            "Cannot find recipeBookPage field in RecipeBookComponent. " +
               "Update the field name in RecipeBookHelperImpl.getRecipesArea().");
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("Cannot access RecipeBookComponent.recipeBookPage", e);
      }
   }

   @SuppressWarnings("unchecked")
   private static List<RecipeButton> getResultButtons(RecipeBookPage page) {
      try {
         Field field = RecipeBookPage.class.getDeclaredField("buttons");
         field.setAccessible(true);
         return (List<RecipeButton>) field.get(page);
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("Cannot access RecipeBookPage.buttons", e);
      }
   }
}
*///?}
