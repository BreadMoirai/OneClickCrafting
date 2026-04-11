//? >=1.21.9 <=26.1 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v21_9;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StonecutterScreen.class)
public class StonecutterScreenMixin {

   // Ordinal 0: a recipe button was clicked and clickMenuButton returned true (sends handleInventoryButtonClick).
   // Ordinal 1: fallthrough to AbstractContainerScreen.mouseClicked (slot click or miss).
   @Inject(at = @At(value = "RETURN", ordinal = 0), method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z")
   public void mouseClicked(MouseButtonEvent click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      StonecutterScreen screen = (StonecutterScreen) (Object) this;
      int selectedRecipe = screen.getMenu().getSelectedRecipeIndex();
      if (selectedRecipe != -1) {
         OneClickEvents.STONECUTTER_CLICK.invoker().onStonecutterClick(selectedRecipe, click.button());
      }
   }
}
*///?}
