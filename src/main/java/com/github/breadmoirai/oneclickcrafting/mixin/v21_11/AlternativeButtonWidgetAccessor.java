//? >=1.21.10 <=1.21.11 {
package com.github.breadmoirai.oneclickcrafting.mixin.v21_11;

import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.recipe.NetworkRecipeId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeAlternativesWidget.AlternativeButtonWidget.class)
public interface AlternativeButtonWidgetAccessor {
   @Accessor
   NetworkRecipeId getRecipeId();
}
//?}
