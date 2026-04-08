//? >=1.21.10 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v21_11;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import net.minecraft.recipe.NetworkRecipeId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {

   @Inject(
      method = "keyPressed(Lnet/minecraft/client/input/KeyInput;)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookWidget;select(Lnet/minecraft/client/gui/screen/recipebook/RecipeResultCollection;Lnet/minecraft/recipe/NetworkRecipeId;Z)Z"),
      cancellable = true
   )
   private void suppressSpaceCraft(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
      OneClickCraftingMod mod = OneClickCraftingMod.getInstance();
      if (input.key() == InputUtil.GLFW_KEY_SPACE && mod != null && !mod.input.repeatLast.guard(InputUtil.GLFW_KEY_SPACE)) {
         cir.setReturnValue(false);
      }
   }
}
*///?}
