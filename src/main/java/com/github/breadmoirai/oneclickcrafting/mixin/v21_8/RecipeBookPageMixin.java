//? <=1.21.8 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v21_8;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeBookPage.class)
public abstract class RecipeBookPageMixin {

   @Shadow @Final private List<RecipeButton> buttons;
   @Shadow @Nullable private RecipeDisplayId lastClickedRecipe;
   @Shadow @Nullable private RecipeCollection lastClickedRecipeCollection;

   // Ordinal 3: a RecipeButton was clicked (returns true). The 5 returns are:
   //   0 — overlay handled or dismissed, 1/2 — arrow buttons, 3 — button hit, 4 — miss.
   // lastClickedRecipe is set by normal code for left-click only; find the button
   // by mouse position to handle both left-click (fire) and right-click (single-option only).
   // For right-click on single-option: set lastClickedRecipe/lastClickedRecipeCollection so
   // that RecipeBookComponent.mouseClicked calls tryPlaceRecipe after we return.
   @Inject(method = "mouseClicked(DDIIIIII)Z", at = @At(value = "RETURN", ordinal = 3))
   private void onButtonClicked(double mouseX, double mouseY, int btn, int left, int top, int width, int height, CallbackInfoReturnable<Boolean> cir) {
      for (RecipeButton button : this.buttons) {
         if (!button.isMouseOver(mouseX, mouseY)) continue;
         if (btn == 1 && OneClickCraftingConfig.getInstance().isEnableRightClick() && button.isOnlyOption()) {
            this.lastClickedRecipe = button.getCurrentRecipe();
            this.lastClickedRecipeCollection = button.getCollection();
            OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(button.getCurrentRecipe().index(), 1);
         } else if (btn == 0) {
            OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(button.getCurrentRecipe().index(), 0);
         }
         return;
      }
   }
}
*///?}
