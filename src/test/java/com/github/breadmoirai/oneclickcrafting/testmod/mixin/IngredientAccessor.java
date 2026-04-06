//? >=1.21.10 <=1.21.11 {
package com.github.breadmoirai.oneclickcrafting.testmod.mixin;

import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntryList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ingredient.class)
public interface IngredientAccessor {
   @Accessor
   RegistryEntryList<Item> getEntries();
}
//?}
