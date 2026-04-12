//? >=1.21.9 <=26.1 {
package com.github.breadmoirai.oneclickcrafting.mixin.v21_9;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {

   @Redirect(
      method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z",
      at = @At(value = "INVOKE",
         target = "Lnet/minecraft/client/input/KeyEvent;isSelection()Z")
   )
   private boolean overrideIsSelection(KeyEvent event) {
      System.out.println("event.key() = " + event.key());
      OneClickCraftingMod mod = OneClickCraftingMod.getInstance();
      if (event.isSelection() && mod != null && mod.input.repeatLast.matches(event.key())) {
         System.out.println("suppress!");
         return false;
      }
      return event.isSelection();
   }
}
//?}
