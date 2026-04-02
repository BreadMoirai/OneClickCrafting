package com.github.breadmoirai.oneclickcrafting.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;

public interface OneClickEvents {
   Event<RecipeClick> RECIPE_CLICK = EventFactory.createArrayBacked(RecipeClick.class,
      (listeners) -> (recipeId, button) -> {
         for (RecipeClick listener : listeners) {
            listener.onRecipeClick(recipeId, button);
         }
      });
   Event<ResultSlotUpdate> RESULT_SLOT_UPDATE = EventFactory.createArrayBacked(ResultSlotUpdate.class,
      (listeners) -> (stack) -> {
         for (ResultSlotUpdate listener : listeners) {
            listener.onResultSlotUpdate(stack);
         }
      });
   Event<StonecutterClick> STONECUTTER_CLICK = EventFactory.createArrayBacked(StonecutterClick.class,
      (listeners) -> (selectedRecipe, button) -> {
         for (StonecutterClick listener : listeners) {
            listener.onStonecutterClick(selectedRecipe, button);
         }
      });

   interface RecipeClick {
      void onRecipeClick(NetworkRecipeId recipeId, int button);
   }

   interface ResultSlotUpdate {
      void onResultSlotUpdate(ItemStack stack);
   }

   interface StonecutterClick {
      void onStonecutterClick(int selectedRecipe, int button);
   }
}
