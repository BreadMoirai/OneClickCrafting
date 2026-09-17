//? >=1.21.9 {
package com.github.breadmoirai.oneclickcrafting.mixin.v21_9;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import com.github.breadmoirai.oneclickcrafting.input.MouseButtons;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent$OverlayRecipeButton")
public abstract class OverlayRecipeButtonMixin extends AbstractWidget {
   public OverlayRecipeButtonMixin(int x, int y, int width, int height, Component message) {
      super(x, y, width, height, message);
   }

   @Override
   public boolean isValidClickButton(MouseButtonInfo buttonInfo) {
      if (buttonInfo.button() == MouseButtons.RIGHT && OneClickCraftingConfig.getInstance().isEnableRightClick()) {
         return true;
      }
      return super.isValidClickButton(buttonInfo);
   }
}
//?}
