//? 26.1 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v26_1;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OverlayRecipeComponent.class)
public abstract class OverlayRecipeComponentMixin {

   @Shadow
   public abstract RecipeDisplayId getLastRecipeClicked();

   @Unique
   private boolean simulatingLeft = false;

   // Ordinal 0: mouseClicked immediately returns false because button != 0.
   // For right-click + config enabled, simulate a left-click so the normal code
   // path runs (iterates buttons, sets lastRecipeClicked), then fire with button=1.
   @Inject(method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
   private void onRightClick(MouseButtonEvent click, boolean outsideClick, CallbackInfoReturnable<Boolean> cir) {
      if (simulatingLeft) return;
      if (click.button() == 1 && OneClickCraftingConfig.getInstance().isEnableRightClick()) {
         MouseButtonEvent leftClick = new MouseButtonEvent(click.x(), click.y(), new MouseButtonInfo(0, click.modifiers()));
         simulatingLeft = true;
         boolean handled = ((OverlayRecipeComponent) (Object) this).mouseClicked(leftClick, outsideClick);
         simulatingLeft = false;
         if (handled) {
            OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(getLastRecipeClicked().index(), 1);
            cir.setReturnValue(true);
         }
      }
   }

   // Ordinal 1: a left-click was handled — lastRecipeClicked is already set.
   @Inject(method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z", at = @At(value = "RETURN", ordinal = 1))
   private void onLeftClick(MouseButtonEvent click, boolean outsideClick, CallbackInfoReturnable<Boolean> cir) {
      if (simulatingLeft) return;
      OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(getLastRecipeClicked().index(), 0);
   }
}
*///?}
