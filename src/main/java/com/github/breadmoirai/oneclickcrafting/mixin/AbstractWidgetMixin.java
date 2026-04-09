package com.github.breadmoirai.oneclickcrafting.mixin;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractWidget.class)
public class AbstractWidgetMixin {
   @Inject(method = "isValidClickButton(Lnet/minecraft/client/input/MouseButtonInfo;)Z", at = @At("HEAD"), cancellable = true)
   private void injectIsValidClickButton(MouseButtonInfo buttonInfo, CallbackInfoReturnable<Boolean> cir) {
      System.out.println("AbstractWidgetMixin = " + this.getClass().getSimpleName());
      if (this.getClass().getSimpleName().equals("OverlayCraftingRecipeButton")
         && buttonInfo.button() == 1
         && OneClickCraftingConfig.getInstance().isEnableRightClick()) {
         System.out.println("AbstractWidgetMixin = true");
         cir.setReturnValue(true);
      }
   }
}
