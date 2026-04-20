//? 1.21.1 {
package com.github.breadmoirai.oneclickcrafting.mixin.v21_1;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookComponent.class)
public interface RecipeBookComponentAccessor {
   @Accessor
   RecipeBookPage getRecipeBookPage();
}
//? }
