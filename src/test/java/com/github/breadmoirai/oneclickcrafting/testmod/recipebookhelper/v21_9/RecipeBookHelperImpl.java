//? >=1.21.9 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.testmod.recipebookhelper.v21_6;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.mixin.ClientRecipeBookAccessor;
import com.github.breadmoirai.oneclickcrafting.mixin.AbstractRecipeBookScreenAccessor;
import com.github.breadmoirai.oneclickcrafting.recipebook.OneClickRecipeBook;
import com.github.breadmoirai.oneclickcrafting.testmod.recipebookhelper.RecipeBookHelper;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
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
      input.pressMouse(mouseButton);
   }

   @Override
   public String clickMultiOptionButton(int mouseButton, String... possibleItems) {
      Set<String> possibleSet = new HashSet<>(Arrays.asList(possibleItems));
      String[] selectedItem = {null};
      double[] windowCoords = context.computeOnClient(mc -> {
         if (!(mc.screen instanceof AbstractRecipeBookScreen<? extends RecipeBookMenu> screen)) {
            throw new AssertionError("clickMultiOptionButton: not in an AbstractRecipeBookScreen");
         }
         RecipeBookComponent<?> recipeBook = ((AbstractRecipeBookScreenAccessor) screen).getRecipeBookComponent();
         RecipeBookPage page = getRecipesArea(recipeBook);
         List<RecipeButton> buttons = getResultButtons(page);

         for (RecipeButton button : buttons) {
            if (button.isOnlyOption()) continue;
            // Use reflection to check all entries in this cycling button
            List<Object> entries = getSelectedEntries(button);
            for (Object entry : entries) {
               for (ItemStack stack : getEntryDisplayItems(entry)) {
                  String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                  if (possibleSet.contains(itemId)) {
                     ItemStack display = button.getDisplayStack();
                     selectedItem[0] = BuiltInRegistries.ITEM.getKey(display.getItem()).toString();
                     double guiCx = button.getX() + button.getWidth() / 2.0;
                     double guiCy = button.getY() + button.getHeight() / 2.0;
                     double scale = mc.getWindow().getGuiScale();
                     return new double[]{guiCx * scale, guiCy * scale};
                  }
               }
            }
         }
         throw new AssertionError("No multi-option button found for items: " + Arrays.toString(possibleItems));
      });

      context.getInput().setCursorPos(windowCoords[0], windowCoords[1]);
      input.pressMouse(mouseButton);
      return selectedItem[0];
   }

   @Override
   public void clickOverlayButton(String targetItemId, int mouseButton) {
      double[] windowCoords = context.computeOnClient(mc -> {
         if (!(mc.screen instanceof AbstractRecipeBookScreen<? extends RecipeBookMenu> screen)) {
            throw new AssertionError("clickOverlayButton: not in an AbstractRecipeBookScreen");
         }
         RecipeBookComponent<?> recipeBook = ((AbstractRecipeBookScreenAccessor) screen).getRecipeBookComponent();
         RecipeBookPage page = getRecipesArea(recipeBook);
         OverlayRecipeComponent overlay = getOverlay(page);
         if (!overlay.isVisible()) {
            throw new AssertionError("clickOverlayButton: overlay is not visible");
         }
         List<Object> overlayButtons = getOverlayButtons(overlay);
         Map<RecipeDisplayId, RecipeDisplayEntry> known =
            ((ClientRecipeBookAccessor) mc.player.getRecipeBook()).getKnown();
         ContextMap ctx = SlotDisplayContext.fromLevel(mc.level);

         for (Object btn : overlayButtons) {
            RecipeDisplayId recipeId = getOverlayButtonRecipe(btn);
            if (recipeId == null) continue;
            RecipeDisplayEntry entry = known.get(recipeId);
            if (entry == null) continue;
            ItemStack result = entry.display().result().resolveForFirstStack(ctx);
            if (!result.isEmpty() &&
               BuiltInRegistries.ITEM.getKey(result.getItem()).toString().equals(targetItemId)) {
               net.minecraft.client.gui.components.AbstractWidget widget =
                  (net.minecraft.client.gui.components.AbstractWidget) btn;
               double guiCx = widget.getX() + widget.getWidth() / 2.0;
               double guiCy = widget.getY() + widget.getHeight() / 2.0;
               double scale = mc.getWindow().getGuiScale();
               return new double[]{guiCx * scale, guiCy * scale};
            }
         }
         throw new AssertionError("Overlay button not found for item: " + targetItemId);
      });

      context.getInput().setCursorPos(windowCoords[0], windowCoords[1]);
      input.pressMouse(mouseButton);
   }

   @Override
   public void placeLastRecipe() {
      context.runOnClient(mc -> OneClickRecipeBook.getInstance().selectLast(false));
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

   @SuppressWarnings("unchecked")
   private static List<Object> getSelectedEntries(RecipeButton button) {
      try {
         Field field = RecipeButton.class.getDeclaredField("selectedEntries");
         field.setAccessible(true);
         return (List<Object>) field.get(button);
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("Cannot access RecipeButton.selectedEntries", e);
      }
   }

   @SuppressWarnings("unchecked")
   private static List<ItemStack> getEntryDisplayItems(Object entry) {
      try {
         Field field = entry.getClass().getDeclaredField("displayItems");
         field.setAccessible(true);
         return (List<ItemStack>) field.get(entry);
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("Cannot access ResolvedEntry.displayItems", e);
      }
   }

   private static OverlayRecipeComponent getOverlay(RecipeBookPage page) {
      try {
         Field field = RecipeBookPage.class.getDeclaredField("overlay");
         field.setAccessible(true);
         return (OverlayRecipeComponent) field.get(page);
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("Cannot access RecipeBookPage.overlay", e);
      }
   }

   @SuppressWarnings("unchecked")
   private static List<Object> getOverlayButtons(OverlayRecipeComponent overlay) {
      try {
         Field field = OverlayRecipeComponent.class.getDeclaredField("recipeButtons");
         field.setAccessible(true);
         return (List<Object>) field.get(overlay);
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("Cannot access OverlayRecipeComponent.recipeButtons", e);
      }
   }

   private static RecipeDisplayId getOverlayButtonRecipe(Object btn) {
      try {
         Field field = btn.getClass().getSuperclass().getDeclaredField("recipe");
         field.setAccessible(true);
         return (RecipeDisplayId) field.get(btn);
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("Cannot access OverlayRecipeButton.recipe", e);
      }
   }
}
*///?}
