package com.github.breadmoirai.oneclickcrafting.mixin;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StonecutterScreen.class)
public class StonecutterScreenMixin {

   @Inject(at = @At(value = "RETURN"), method = "mouseClicked(Lnet/minecraft/client/gui/Click;Z)Z")
   public void mouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      StonecutterScreen screen = (StonecutterScreen) ((Object) this);
      int selectedRecipe = screen.getScreenHandler().getSelectedRecipe();
      if (selectedRecipe != -1) {
         OneClickCraftingClient.getInstance().stonecuttingHandler.recipeClicked(screen, click, selectedRecipe);
      }
   }
}
