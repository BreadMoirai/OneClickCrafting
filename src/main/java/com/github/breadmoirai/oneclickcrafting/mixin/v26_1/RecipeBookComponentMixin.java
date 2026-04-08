//? 26.1 {
package com.github.breadmoirai.oneclickcrafting.mixin.v26_1;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {

   @Inject(
      method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;tryPlaceRecipe(Lnet/minecraft/client/gui/screens/recipebook/RecipeCollection;Lnet/minecraft/world/item/crafting/display/RecipeDisplayId;Z)Z"),
      cancellable = true
   )
   private void suppressSpaceCraft(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
      OneClickCraftingMod mod = OneClickCraftingMod.getInstance();
      if (event.key() == GLFW.GLFW_KEY_SPACE && mod != null && !mod.input.repeatLast.guard(GLFW.GLFW_KEY_SPACE)) {
         cir.setReturnValue(false);
      }
   }
}
//?}
