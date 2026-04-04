//? 26.1 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v26_1;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookComponent.class)
public interface RecipeBookComponentMixin {
   @Accessor
   public RecipeDisplayId getLastRecipe();
}

*///?}