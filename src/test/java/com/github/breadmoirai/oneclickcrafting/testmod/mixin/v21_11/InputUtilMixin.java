//? >=1.21.10 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.testmod.mixin.v21_11;

import com.github.breadmoirai.oneclickcrafting.testmod.VirtualKeyState;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InputUtil.class)
public class InputUtilMixin {

    @Inject(method = "isKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void isKeyPressed(Window window, int keycode, CallbackInfoReturnable<Boolean> cir) {
        if (VirtualKeyState.isHeld(keycode)) {
            cir.setReturnValue(true);
        }
    }
}
*///?}
