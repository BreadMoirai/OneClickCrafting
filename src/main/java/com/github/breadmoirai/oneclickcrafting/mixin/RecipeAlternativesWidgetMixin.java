package com.github.breadmoirai.oneclickcrafting.mixin;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingClient;
import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
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


   @Inject(method = "mouseClicked(DDI)Z", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
   private void mouseClickedRight(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      if (button == 1 && OneClickCraftingConfig.getInstance().isEnableRightClick()) {
         for (var alternativeButtonWidget : this.alternativeButtons) {
            if (alternativeButtonWidget.mouseClicked(mouseX, mouseY, 0)) {
               OneClickCraftingClient.getInstance().setLastButton(1);
               this.lastClickedRecipe = alternativeButtonWidget.recipeId;
               cir.setReturnValue(true);
            }
         }
      }
   }

   @Inject(method = "mouseClicked(DDI)Z", at = @At(value = "RETURN", ordinal = 1))
   private void mouseClickedLeft(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      OneClickCraftingClient.getInstance().setLastButton(0);
   }
}