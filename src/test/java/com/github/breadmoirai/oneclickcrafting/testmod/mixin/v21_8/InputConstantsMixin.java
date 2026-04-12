//? <=1.21.8 {
/*package com.github.breadmoirai.oneclickcrafting.testmod.mixin.v21_8;

import com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.VirtualKeyState;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InputConstants.class)
public class InputConstantsMixin {

    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    private static void isKeyDown(long window, int keycode, CallbackInfoReturnable<Boolean> cir) {
        if (VirtualKeyState.isHeld(keycode)) {
            cir.setReturnValue(true);
        }
    }
}
*///?}
