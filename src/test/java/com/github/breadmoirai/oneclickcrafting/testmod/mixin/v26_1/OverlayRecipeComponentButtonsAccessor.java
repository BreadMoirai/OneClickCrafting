//? 26.1 {
package com.github.breadmoirai.oneclickcrafting.testmod.mixin.v26_1;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(OverlayRecipeComponent.class)
public interface OverlayRecipeComponentButtonsAccessor {
   // Field type is List<OverlayRecipeButton>, but OverlayRecipeButton is package-private.
   // AbstractWidget is its public supertype; the raw List type matches at bytecode level.
   @SuppressWarnings("unchecked")
   @Accessor("recipeButtons")
   List<AbstractWidget> getRecipeButtons();
}
//?}
