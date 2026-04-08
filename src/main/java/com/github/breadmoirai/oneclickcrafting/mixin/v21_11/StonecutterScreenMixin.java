//? >=1.21.10 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v21_11;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
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
      // Only fire when a recipe button was actually clicked and a ButtonClickC2SPacket was sent.
      // If onButtonClick returned false (same recipe already selected), mouseClicked returns false
      // and no packet is sent — firing the callback here would create a phantom craft event.
      if (!Boolean.TRUE.equals(cir.getReturnValue())) return;
      StonecutterScreen screen = (StonecutterScreen) ((Object) this);
      int selectedRecipe = screen.getScreenHandler().getSelectedRecipe();
      if (selectedRecipe != -1) {
         OneClickEvents.STONECUTTER_CLICK.invoker().onStonecutterClick(selectedRecipe, click.button());
      }
   }
}

*///?}