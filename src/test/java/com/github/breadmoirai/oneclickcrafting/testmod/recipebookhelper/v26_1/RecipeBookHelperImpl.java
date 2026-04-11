//? 26.1 {
/*package com.github.breadmoirai.oneclickcrafting.testmod.recipebookhelper.v21_8;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.mixin.AbstractRecipeBookScreenAccessor;
import com.github.breadmoirai.oneclickcrafting.mixin.ClientRecipeBookAccessor;
import com.github.breadmoirai.oneclickcrafting.recipebook.OneClickRecipeBook;
import com.github.breadmoirai.oneclickcrafting.testmod.recipebookhelper.RecipeBookHelper;
import com.github.breadmoirai.oneclickcrafting.testmod.mixin.v26_1.OverlayRecipeButtonRecipeAccessor;
import com.github.breadmoirai.oneclickcrafting.testmod.mixin.v26_1.OverlayRecipeComponentButtonsAccessor;
import com.github.breadmoirai.oneclickcrafting.testmod.mixin.v26_1.RecipeBookPageButtonsAccessor;
import com.github.breadmoirai.oneclickcrafting.testmod.mixin.v26_1.RecipeBookPageOverlayAccessor;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.components.AbstractWidget;
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
         RecipeBookComponent<?> component = ((AbstractRecipeBookScreenAccessor) screen).getRecipeBookComponent();
         if (!component.isVisible()) {
            component.toggleVisibility();
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
         RecipeBookComponent<?> component = ((AbstractRecipeBookScreenAccessor) screen).getRecipeBookComponent();
         RecipeBookPage page = getRecipeBookPage(component);
         List<RecipeButton> buttons = ((RecipeBookPageButtonsAccessor) page).getButtons();
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
         StringBuilder dbg = new StringBuilder("Recipe button not found for item: " + targetItemId);
         dbg.append("; buttons.size=").append(buttons.size());
         dbg.append("; recipeBook.isVisible=").append(component.isVisible());
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
         RecipeBookComponent<?> component = ((AbstractRecipeBookScreenAccessor) screen).getRecipeBookComponent();
         RecipeBookPage page = getRecipeBookPage(component);
         List<RecipeButton> buttons = ((RecipeBookPageButtonsAccessor) page).getButtons();
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
               OneClickRecipeBook.getInstance().craftRecipe(button.getCollection(), id, true);
               OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(id.index(), 0);
               return;
            }
         }
         throw new AssertionError("Recipe button not found for item: " + targetItemId);
      });
   }

   @Override
   public void placeLastRecipe() {
      context.runOnClient(mc -> OneClickRecipeBook.getInstance().selectLast(false));
   }

   @Override
   public String clickMultiOptionButton(int mouseButton, String... possibleItems) {
      Set<String> possible = new HashSet<>(Arrays.asList(possibleItems));
      String[] data = context.computeOnClient(mc -> {
         if (!(mc.screen instanceof AbstractRecipeBookScreen<? extends RecipeBookMenu> screen)) {
            throw new AssertionError("clickMultiOptionButton: not in an AbstractRecipeBookScreen");
         }
         RecipeBookComponent<?> component = ((AbstractRecipeBookScreenAccessor) screen).getRecipeBookComponent();
         RecipeBookPage page = getRecipeBookPage(component);
         List<RecipeButton> buttons = ((RecipeBookPageButtonsAccessor) page).getButtons();
         Map<RecipeDisplayId, RecipeDisplayEntry> known =
            ((ClientRecipeBookAccessor) mc.player.getRecipeBook()).getKnown();
         ContextMap ctx = SlotDisplayContext.fromLevel(mc.level);

         for (RecipeButton button : buttons) {
            if (button.isOnlyOption()) continue;
            RecipeDisplayId id = button.getCurrentRecipe();
            if (id == null) continue;
            RecipeDisplayEntry entry = known.get(id);
            if (entry == null) continue;
            ItemStack stack = entry.display().result().resolveForFirstStack(ctx);
            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            if (possible.contains(itemId)) {
               double guiCx = button.getX() + button.getWidth() / 2.0;
               double guiCy = button.getY() + button.getHeight() / 2.0;
               double scale = mc.getWindow().getGuiScale();
               return new String[]{itemId, Double.toString(guiCx * scale), Double.toString(guiCy * scale)};
            }
         }
         throw new AssertionError("Multi-option recipe button not found for items: " + possible);
      });
      String displayedItem = data[0];
      double x = Double.parseDouble(data[1]);
      double y = Double.parseDouble(data[2]);
      context.getInput().setCursorPos(x, y);
      context.getInput().pressMouse(mouseButton);
      return displayedItem;
   }

   @Override
   public void clickOverlayButton(String targetItemId, int mouseButton) {
      double[] windowCoords = context.computeOnClient(mc -> {
         if (!(mc.screen instanceof AbstractRecipeBookScreen<? extends RecipeBookMenu> screen)) {
            throw new AssertionError("clickOverlayButton: not in an AbstractRecipeBookScreen");
         }
         RecipeBookComponent<?> component = ((AbstractRecipeBookScreenAccessor) screen).getRecipeBookComponent();
         RecipeBookPage page = getRecipeBookPage(component);
         OverlayRecipeComponent overlay = ((RecipeBookPageOverlayAccessor) page).getOverlay();
         if (!overlay.isVisible()) {
            throw new AssertionError("clickOverlayButton: overlay is not visible");
         }
         List<AbstractWidget> overlayButtons = ((OverlayRecipeComponentButtonsAccessor) overlay).getRecipeButtons();
         Map<RecipeDisplayId, RecipeDisplayEntry> known =
            ((ClientRecipeBookAccessor) mc.player.getRecipeBook()).getKnown();
         ContextMap ctx = SlotDisplayContext.fromLevel(mc.level);

         for (AbstractWidget btn : overlayButtons) {
            RecipeDisplayId recipeId = ((OverlayRecipeButtonRecipeAccessor) btn).getRecipe();
            RecipeDisplayEntry entry = known.get(recipeId);
            if (entry == null) continue;
            ItemStack stack = entry.display().result().resolveForFirstStack(ctx);
            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            if (itemId.equals(targetItemId)) {
               double guiCx = btn.getX() + btn.getWidth() / 2.0;
               double guiCy = btn.getY() + btn.getHeight() / 2.0;
               double scale = mc.getWindow().getGuiScale();
               return new double[]{guiCx * scale, guiCy * scale};
            }
         }
         throw new AssertionError("Overlay button not found for item: " + targetItemId
            + "; overlay.isVisible=" + overlay.isVisible()
            + "; overlayButtons.size=" + overlayButtons.size());
      });
      context.getInput().setCursorPos(windowCoords[0], windowCoords[1]);
      context.getInput().pressMouse(mouseButton);
   }

   // -------------------------------------------------------------------------
   // Reflection helpers
   // -------------------------------------------------------------------------

   /^*
    * Accesses the current {@link RecipeBookPage} from the {@link RecipeBookComponent}.
    * The field name may vary by Mojang mapping version; tries common names in order.
    ^/
   private static RecipeBookPage getRecipeBookPage(RecipeBookComponent<?> component) {
      try {
         Field field = RecipeBookComponent.class.getDeclaredField("recipeBookPage");
         field.setAccessible(true);
         return (RecipeBookPage) field.get(component);
      } catch (NoSuchFieldException ignored) {
         throw new RuntimeException(
            "Cannot find recipeBookPage field in RecipeBookComponent. " +
               "Update the field name in RecipeBookHelperImpl.getRecipeBookPage().");
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("Cannot access RecipeBookComponent.recipeBookPage", e);
      }
   }

}
*///?}
