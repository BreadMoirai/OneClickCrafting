//? 26.1 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v26_1;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.input.MouseButtonEvent;
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

   // Ordinal 4: a RecipeButton was clicked (returns true). The 6 returns are:
   //   0/1 — overlay handled or dismissed, 2/3 — arrow buttons, 4 — button hit, 5 — miss.
   // lastClickedRecipe is set by normal code for left-click only; find the button
   // by mouse position to handle both left-click (fire) and right-click (single-option only).
   @Inject(method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;IIIIZ)Z", at = @At(value = "RETURN", ordinal = 4))
   private void onButtonClicked(MouseButtonEvent click, int left, int top, int width, int height, boolean filtering, CallbackInfoReturnable<Boolean> cir) {
      for (RecipeButton button : this.buttons) {
         if (!button.isMouseOver(click.x(), click.y())) continue;
         if (click.button() == 1 && OneClickCraftingConfig.getInstance().isEnableRightClick() && button.isOnlyOption()) {
            this.lastClickedRecipe = button.getCurrentRecipe();
            this.lastClickedRecipeCollection = button.getCollection();
            OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(button.getCurrentRecipe().index(), 1);
         } else if (click.button() == 0) {
            OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(button.getCurrentRecipe().index(), 0);
         }
         return;
      }
   }
}
*///?}
