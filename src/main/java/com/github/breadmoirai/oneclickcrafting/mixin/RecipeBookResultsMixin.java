package com.github.breadmoirai.oneclickcrafting.mixin;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingClient;
import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.Recipe;
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

   @Inject(at = @At(value = "RETURN", ordinal = 3), method = "mouseClicked(DDIIIII)Z")
   private void mouseClicked(double mouseX, double mouseY, int button, int areaLeft, int areaTop, int areaWidth, int areaHeight, CallbackInfoReturnable<Boolean> cir) {
      for (AnimatedResultButton animatedResultButton : this.resultButtons) {
         if (animatedResultButton.mouseClicked(mouseX, mouseY, button)) {
            OneClickCraftingClient.getInstance().setLastButton(button);
            if (button == 1 && OneClickCraftingConfig.getInstance().isEnableRightClick() && animatedResultButton.hasSingleResult()) {
               this.lastClickedRecipe = animatedResultButton.getCurrentId();
               this.resultCollection = animatedResultButton.getResultCollection();
               return;
            }
            return;
         }
      }
   }
}