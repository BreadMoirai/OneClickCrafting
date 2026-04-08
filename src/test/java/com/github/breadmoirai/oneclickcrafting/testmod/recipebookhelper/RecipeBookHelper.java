package com.github.breadmoirai.oneclickcrafting.testmod.recipebookhelper;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

//? 26.1 {
import com.github.breadmoirai.oneclickcrafting.testmod.recipebookhelper.v26_1.RecipeBookHelperImpl;
//?} >=1.21.10 <=1.21.11 {
/*import com.github.breadmoirai.oneclickcrafting.testmod.v21_11.RecipeBookHelperImpl;
*///?}

@SuppressWarnings("UnstableApiUsage")
public abstract class RecipeBookHelper {

   protected final ClientGameTestContext context;

   protected RecipeBookHelper(ClientGameTestContext context) {
      this.context = context;
   }

   public static RecipeBookHelper create(ClientGameTestContext context) {
      return new RecipeBookHelperImpl(context);
   }

   /**
    * Ensures the recipe book is open in the currently active recipe-book screen.
    * If the recipe book is already open, this is a no-op.
    */
   public abstract void open();

   public void leftClick(String targetItemId) {
      clickRecipeButton(targetItemId, 0);
   }

   public void rightClick(String targetItemId) {
      clickRecipeButton(targetItemId, 1);
   }

   /**
    * Finds the recipe button displaying {@code targetItemId} in the currently open
    * recipe book, positions the cursor over it, and simulates a mouse click.
    *
    * @param targetItemId namespaced item ID, e.g. {@code "minecraft:oak_planks"}
    * @param mouseButton  {@code 0} for left-click, {@code 1} for right-click
    */
   public abstract void clickRecipeButton(String targetItemId, int mouseButton);

   /**
    * Finds the recipe button displaying {@code targetItemId} and directly invokes
    * the recipe book's select method with {@code craftAll=true}, then fires the
    * recipe-click event.
    *
    * @param targetItemId namespaced item ID, e.g. {@code "minecraft:oak_planks"}
    */
   public abstract void clickRecipeButtonCraftAll(String targetItemId);

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
}
