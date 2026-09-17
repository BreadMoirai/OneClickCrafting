//? >=26.3 {
package com.github.breadmoirai.oneclickcrafting.testmod.mixin.v26_3;

import com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.VirtualKeyState;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * MC 26.3 moved off GLFW onto SDL and dropped the {@code Window} parameter from
 * {@code InputConstants.isKeyDown}, which changes the injected callback signature - hence a
 * separate class from {@code ..mixin.v21_9}. Each version's testmod mixins.json picks one.
 */
@Mixin(InputConstants.class)
public class InputConstantsMixin {

    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    private static void isKeyDown(int key, CallbackInfoReturnable<Boolean> cir) {
        if (VirtualKeyState.isHeld(key)) {
            cir.setReturnValue(true);
        }
    }
}
//?}
