//? >=1.21.10 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v21_11;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.NetworkRecipeId;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeBookResults.class)
public abstract class RecipeBookResultsMixin {
   @Final
   @Shadow
   private List<AnimatedResultButton> resultButtons;

   @Shadow
   @Nullable
   private NetworkRecipeId lastClickedRecipe;

   @Shadow
   @Nullable
   private RecipeResultCollection resultCollection;

   @Inject(at = @At(value = "RETURN", ordinal = 3), method = "mouseClicked(Lnet/minecraft/client/gui/Click;IIIIZ)Z")
   private void mouseClicked(Click click, int left, int top, int width, int height, boolean bl, CallbackInfoReturnable<Boolean> cir) {
      for (AnimatedResultButton animatedResultButton : this.resultButtons) {
         if (animatedResultButton.mouseClicked(click, bl)) {
            if (click.button() == 1 && OneClickCraftingConfig.getInstance()
               .isEnableRightClick() && animatedResultButton.hasSingleResult()) {
               this.lastClickedRecipe = animatedResultButton.getCurrentId();
               this.resultCollection = animatedResultButton.getResultCollection();
               OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(animatedResultButton.getCurrentId().index(), 1);
               return;
            }
            OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(animatedResultButton.getCurrentId().index(), click.button());
            return;
         }
      }
   }
}

*///?}