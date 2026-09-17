//? >=1.21.9 {
package com.github.breadmoirai.oneclickcrafting.mixin.v21_9;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import com.github.breadmoirai.oneclickcrafting.input.MouseButtons;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OverlayRecipeComponent.class)
public abstract class OverlayRecipeComponentMixin {

   @Shadow
   private RecipeDisplayId lastRecipeClicked;

   @Redirect(
      method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/input/MouseButtonEvent;button()I"
      )
   )
   private int overrideLeftClickCondition(MouseButtonEvent click) {
      if (click.button() == MouseButtons.RIGHT && OneClickCraftingConfig.getInstance().isEnableRightClick())
         // Spoof a left-click so vanilla opens/handles the overlay entry as usual.
         return MouseButtons.LEFT;
      return click.button();
   }

   @Inject(method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z", at = @At(value = "RETURN", ordinal = 1))
   private void mouseClickedLeft(MouseButtonEvent click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(this.lastRecipeClicked.index(), MouseButtons.toInternal(click.button()));
   }
}
//?}
