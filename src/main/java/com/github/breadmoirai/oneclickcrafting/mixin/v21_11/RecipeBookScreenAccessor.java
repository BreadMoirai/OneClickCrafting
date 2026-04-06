//? >=1.21.10 <=1.21.11 {
package com.github.breadmoirai.oneclickcrafting.mixin.v21_11;

import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookScreen.class)
public interface RecipeBookScreenAccessor {
   @Accessor
   RecipeBookWidget<?> getRecipeBook();
}
//?}
