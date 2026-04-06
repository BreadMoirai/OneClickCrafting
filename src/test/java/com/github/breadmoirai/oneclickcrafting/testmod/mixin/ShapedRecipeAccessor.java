//? >=1.21.10 <=1.21.11 {
package com.github.breadmoirai.oneclickcrafting.testmod.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapedRecipe.class)
public interface ShapedRecipeAccessor {
   @Accessor
   ItemStack getResult();

   @Accessor
   RawShapedRecipe getRaw();
}
//?}
