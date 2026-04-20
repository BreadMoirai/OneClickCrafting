//? >=1.21.5 <=1.21.8 {
package com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.v21_5;

import com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.InputHelper;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.mixin.client.gametest.input.MouseAccessor;

@SuppressWarnings("UnstableApiUsage")
public class InputHelperImpl extends InputHelper {

   public InputHelperImpl(ClientGameTestContext context) {
      super(context);
   }

   @Override
   protected void mouseAction(int button, int action) {
      context.runOnClient(minecraft -> {
         ((MouseAccessor) minecraft.mouseHandler).invokeOnMouseButton(minecraft.getWindow().getWindow(), button, action,
            getShiftMod());
      });
   }
}
//?}