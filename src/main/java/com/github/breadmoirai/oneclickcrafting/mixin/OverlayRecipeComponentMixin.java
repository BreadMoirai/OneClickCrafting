package com.github.breadmoirai.oneclickcrafting.mixin;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
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
      System.out.println("OverlayRecipeComponentMixin.click = " + click.button());
      if (click.button() == 1 && OneClickCraftingConfig.getInstance().isEnableRightClick())
         return 0;
      return click.button();
   }

   @Inject(method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z", at = @At(value = "RETURN", ordinal = 1))
   private void mouseClickedLeft(MouseButtonEvent click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(this.lastRecipeClicked.index(), click.button());
   }
}