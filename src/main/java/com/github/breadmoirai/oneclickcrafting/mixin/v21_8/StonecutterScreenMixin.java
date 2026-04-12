//? <=1.21.8 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v21_8;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StonecutterScreen.class)
public class StonecutterScreenMixin {

   // Ordinal 0: a recipe button was clicked and clickMenuButton returned true (sends handleInventoryButtonClick).
   // Ordinal 1: fallthrough to AbstractContainerScreen.mouseClicked (slot click or miss).
   @Inject(at = @At(value = "RETURN", ordinal = 0), method = "mouseClicked(DDI)Z")
   public void mouseClicked(double x, double y, int button, CallbackInfoReturnable<Boolean> cir) {
      StonecutterScreen screen = (StonecutterScreen) (Object) this;
      int selectedRecipe = screen.getMenu().getSelectedRecipeIndex();
      if (selectedRecipe != -1) {
         OneClickEvents.STONECUTTER_CLICK.invoker().onStonecutterClick(selectedRecipe, button);
      }
   }
}
*///?}
