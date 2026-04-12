//? >=26.1 {
package com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.v26_1;

import com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.InputHelper;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.mixin.client.gametest.input.MouseHandlerAccessor;
import net.minecraft.client.input.MouseButtonInfo;

@SuppressWarnings("UnstableApiUsage")
public class InputHelperImpl extends InputHelper {

   public InputHelperImpl(ClientGameTestContext context) {
      super(context);
   }

   @Override
   protected void mouseAction(int button, int action) {
      context.runOnClient(minecraft -> {
         MouseButtonInfo buttonInfo = new MouseButtonInfo(button, getShiftMod());
         System.out.println("buttonInfo = " + buttonInfo);
         ((MouseHandlerAccessor) minecraft.mouseHandler).invokeOnButton(
            minecraft.getWindow().handle(),
            buttonInfo,
            action);
      });
   }
}
//?}