//? >=1.21.10 <=1.21.11 {
package com.github.breadmoirai.oneclickcrafting.testmod.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ShapelessRecipe.class)
public interface ShapelessRecipeAccessor {
   @Accessor
   ItemStack getResult();

   @Accessor
   List<Ingredient> getIngredients();
}
//?}
