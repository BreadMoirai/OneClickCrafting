//? <1.21.9 {
package com.github.breadmoirai.oneclickcrafting.mixin.v21_8;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {

   @Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true)
   private void suppressRepeatLastKey(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
      OneClickCraftingMod mod = OneClickCraftingMod.getInstance();
      if (mod != null && mod.input.repeatLast.matches(keyCode)) {
         cir.setReturnValue(false);
      }
   }
}
//?}
