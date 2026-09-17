package com.github.breadmoirai.oneclickcrafting.testmod.inputhelper;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.mixin.KeyMappingAccessor;
import com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.v26_1.InputHelperImpl;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

@SuppressWarnings("UnstableApiUsage")
public abstract class InputHelper {
   public static int REPEAT_KEY_CODE = InputConstants.KEY_R;

   public static InputHelper getInstance(ClientGameTestContext context) {
      return new InputHelperImpl(context);
   }

   protected ClientGameTestContext context;

   public InputHelper(ClientGameTestContext context) {
      this.context = context;
   }

   public void holdShift() {
      keyDown(InputConstants.KEY_LSHIFT);
   }

   public void releaseShift() {
      keyUp(InputConstants.KEY_LSHIFT);
   }

   // InputConstants.MOD_SHIFT only exists from 1.21.9 on; before that the modifier bit came from
   // GLFW_MOD_SHIFT, which has the same value (1).
   //? if >=1.21.9 {
   private static final int MOD_SHIFT = InputConstants.MOD_SHIFT;
   //? } else {
   /*private static final int MOD_SHIFT = 1;
   *///? }

   public int getShiftMod() {
      return VirtualKeyState.isHeld(InputConstants.KEY_LSHIFT) ? MOD_SHIFT : 0;
   }

   public void holdDrop() {
      int code = context.computeOnClient(mc -> ((KeyMappingAccessor) mc.options.keyDrop).getKey().getValue());
      keyDown(code);
   }

   public void releaseDrop() {
      int code = context.computeOnClient(mc -> ((KeyMappingAccessor) mc.options.keyDrop).getKey().getValue());
      keyUp(code);
   }

   /**
    * Presses the repeat key once.  When the repeat key is SPACE, also calls
    * {@code placeLastRecipe()} after waiting one tick so the result-slot update
    * that vanilla would fire via its SPACE handling is replicated.
    */
   public void pressRepeatKey() {
      keyDown(REPEAT_KEY_CODE);
      keyUp(REPEAT_KEY_CODE);
      context.waitTick();
   }

   /**
    * Begins holding the repeat key.  When the repeat key is SPACE, also waits
    * one tick and calls {@code placeLastRecipe()} to kick-start the first craft;
    * subsequent crafts chain automatically via the server's grid auto-refill.
    */
   public void holdRepeatKey() {
      keyDown(REPEAT_KEY_CODE);
   }

   public void releaseRepeatKey() {
      keyUp(REPEAT_KEY_CODE);
   }

   public void bindRepeatKey() {
      context.runOnClient(mc -> {
         OneClickCraftingMod.getInstance().input.repeatLast.setKey(REPEAT_KEY_CODE);
         KeyMapping.resetMapping();
      });
   }

   public void unbindRepeatKey() {
      context.runOnClient(mc -> {
         OneClickCraftingMod.getInstance().input.repeatLast.setKey(InputConstants.UNKNOWN.getValue());
         KeyMapping.resetMapping();
      });
   }

   public void pressMouse(int button) {
      mouseDown(button);
      mouseUp(button);
      context.waitTick();
   }

   private void keyDown(int keycode) {
      VirtualKeyState.hold(keycode);
      context.getInput().holdKey(keycode);
   }

   private void keyUp(int keycode) {
      VirtualKeyState.release(keycode);
      context.getInput().releaseKey(keycode);
   }

   private void mouseDown(int button) {
      mouseAction(button, InputConstants.PRESS);
   }

   private void mouseUp(int button) {
      mouseAction(button, InputConstants.RELEASE);
   }

   protected abstract void mouseAction(int button, int action);
}
