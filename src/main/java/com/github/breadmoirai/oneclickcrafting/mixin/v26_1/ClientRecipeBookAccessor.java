//? 26.1 {
package com.github.breadmoirai.oneclickcrafting.mixin.v26_1;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientRecipeBook.class)
public interface ClientRecipeBookAccessor {
   @Accessor
   Map<RecipeDisplayId, RecipeDisplayEntry> getKnown();
}

//?}