//? 26.1 {
/*package com.github.breadmoirai.oneclickcrafting.testmod.mixin.v26_1;

import com.github.breadmoirai.oneclickcrafting.testmod.VirtualKeyState;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InputConstants.class)
public class InputConstantsMixin {

    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    private static void isKeyDown(Window window, int key, CallbackInfoReturnable<Boolean> cir) {
        if (VirtualKeyState.isHeld(key)) {
            cir.setReturnValue(true);
        }
    }
}
*///?}
