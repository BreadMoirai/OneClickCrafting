package com.github.breadmoirai.oneclickcrafting.mixin;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {

   @Inject(
      method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z",
      at = @At(value = "INVOKE",
         target = "Lnet/minecraft/client/gui/components/AbstractWidget;playButtonClickSound(Lnet/minecraft/client/sounds/SoundManager;)V"),
      cancellable = true
   )
   private void suppressPlaceRecipeKeyInput(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
      OneClickCraftingMod mod = OneClickCraftingMod.getInstance();
      if (event.isSelection() && mod != null && !mod.input.repeatLast.guard(event.key())) {
         cir.setReturnValue(false);
      }
   }
}

