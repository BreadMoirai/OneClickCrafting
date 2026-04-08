//? 26.1 {
package com.github.breadmoirai.oneclickcrafting.testmod.mixin.v26_1;

import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(RecipeBookPage.class)
public interface RecipeBookPageButtonsAccessor {
   @Accessor
   List<RecipeButton> getButtons();
}
//?}
