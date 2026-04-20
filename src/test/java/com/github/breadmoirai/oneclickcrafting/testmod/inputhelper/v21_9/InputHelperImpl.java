//? >=1.21.9 <1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.v21_5;

import com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.InputHelper;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.mixin.client.gametest.input.MouseAccessor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;

@SuppressWarnings("UnstableApiUsage")
public class InputHelperImpl extends InputHelper {

   public InputHelperImpl(ClientGameTestContext context) {super(context);
      }

   @Override protected void mouseAction(int button, int action) {
      context.runOnClient(minecraft -> {
         ((MouseAccessor) minecraft.mouseHandler).invokeOnMouseButton(minecraft.getWindow().handle(),
            new MouseButtonInfo(button, getShiftMod()), action);
      });
   }

}
*///?}