//? 26.1 {
package com.github.breadmoirai.oneclickcrafting.testmod.mixin.v26_1;

import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookPage.class)
public interface RecipeBookPageOverlayAccessor {
   @Accessor
   OverlayRecipeComponent getOverlay();
}
//?}
