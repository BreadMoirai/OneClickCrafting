//? >=1.21.10 <=1.21.11 {
package com.github.breadmoirai.oneclickcrafting.mixin.v21_11;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.input.MouseInput;
import net.minecraft.recipe.NetworkRecipeId;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeAlternativesWidget.class)
public abstract class RecipeAlternativesWidgetMixin {

   @Final
   @Shadow
   private List<RecipeAlternativesWidget.AlternativeButtonWidget> alternativeButtons;
   @Shadow
   private NetworkRecipeId lastClickedRecipe;

   @Inject(method = "mouseClicked(Lnet/minecraft/client/gui/Click;Z)Z", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
   private void mouseClickedRight(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      if (click.button() == 1 && OneClickCraftingConfig.getInstance().isEnableRightClick()) {
         for (var alternativeButtonWidget : this.alternativeButtons) {
            if (alternativeButtonWidget.mouseClicked(new Click(click.x(), click.y(), new MouseInput(0, click.modifiers())), doubled)) {
               NetworkRecipeId recipeId = ((AlternativeButtonWidgetAccessor) alternativeButtonWidget).getRecipeId();
               this.lastClickedRecipe = recipeId;
               OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(recipeId.index(), 1);
               cir.setReturnValue(true);
            }
         }
      }
   }

   @Inject(method = "mouseClicked(Lnet/minecraft/client/gui/Click;Z)Z", at = @At(value = "RETURN", ordinal = 1))
   private void mouseClickedLeft(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(this.lastClickedRecipe.index(), 0);
   }
}

//?}