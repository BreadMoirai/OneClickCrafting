package com.github.breadmoirai.oneclickcrafting.testmod.recipebookhelper;

import com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.InputHelper;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

import com.github.breadmoirai.oneclickcrafting.testmod.recipebookhelper.v21_6.RecipeBookHelperImpl;

@SuppressWarnings("UnstableApiUsage")
public abstract class RecipeBookHelper {

   protected final ClientGameTestContext context;
   protected final InputHelper input;

   protected RecipeBookHelper(ClientGameTestContext context) {
      this.context = context;
      this.input = InputHelper.getInstance(context);
   }

   public static RecipeBookHelper create(ClientGameTestContext context) {
      return new RecipeBookHelperImpl(context);
   }

   /**
    * Ensures the recipe book is open in the currently active recipe-book screen.
    * If the recipe book is already open, this is a no-op.
    */
   public abstract void open();

   /**
    * Finds the recipe button displaying {@code targetItemId} in the currently open
    * recipe book, positions the cursor over it, and simulates a mouse click.
    *
    * @param targetItemId namespaced item ID, e.g. {@code "minecraft:oak_planks"}
    * @param mouseButton  {@code 0} for left-click, {@code 1} for right-click
    */
   public abstract void clickRecipeButton(String targetItemId, int mouseButton);

   /**
    * Calls {@code tryPlaceRecipe} for the last selected recipe.  This is the
    * same call that the vanilla recipe book makes when SPACE is pressed while the
    * recipe book is focused, which is what provides the result-slot update that
    * the mod's repeat-craft operation waits on.  Used only in test harness code to
    * compensate for the test framework not fully replicating that vanilla behaviour.
    *
    * <p>No-op when the current screen is not a recipe-book screen (e.g. stonecutter).
    */
   public abstract void placeLastRecipe();

   /**
    * Finds the multi-option recipe button (one that cycles among the given item IDs
    * because its collection contains multiple craftable alternatives), clicks it with
    * the given mouse button, and returns the item ID that was currently displayed on
    * the button at the moment of the click.
    *
    * @param mouseButton  {@code 0} for left-click, {@code 1} for right-click
    * @param possibleItems namespaced item IDs that may appear on the cycling button,
    *                      e.g. {@code "minecraft:oak_planks", "minecraft:birch_planks"}
    * @return the item ID currently displayed on the button when clicked
    */
   public abstract String clickMultiOptionButton(int mouseButton, String... possibleItems);

   /**
    * After a multi-option button has been right-clicked to open its overlay submenu,
    * finds the overlay button displaying {@code targetItemId} and clicks it.
    *
    * @param targetItemId namespaced item ID, e.g. {@code "minecraft:oak_planks"}
    * @param mouseButton  {@code 0} for left-click, {@code 1} for right-click
    */
   public abstract void clickOverlayButton(String targetItemId, int mouseButton);
}
