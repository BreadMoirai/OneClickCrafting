package com.github.breadmoirai.oneclickcrafting.testmod.inputhelper;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod;
import com.github.breadmoirai.oneclickcrafting.mixin.KeyMappingAccessor;
import com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.v21_4.InputHelperImpl;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("UnstableApiUsage")
public abstract class InputHelper {
   public static int REPEAT_KEY_CODE = GLFW.GLFW_KEY_R;

   public static InputHelper getInstance(ClientGameTestContext context) {
      return new InputHelperImpl(context);
   }

   protected ClientGameTestContext context;

   public InputHelper(ClientGameTestContext context) {
      this.context = context;
   }

   public void holdShift() {
      keyDown(GLFW.GLFW_KEY_LEFT_SHIFT);
   }

   public void releaseShift() {
      keyUp(GLFW.GLFW_KEY_LEFT_SHIFT);
   }

   public int getShiftMod() {
      return VirtualKeyState.isHeld(GLFW.GLFW_KEY_LEFT_SHIFT) ? GLFW.GLFW_MOD_SHIFT : 0;
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
         OneClickCraftingMod.getInstance().input.repeatLast.setKey(GLFW.GLFW_KEY_UNKNOWN);
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
      mouseAction(button, GLFW.GLFW_PRESS);
   }

   private void mouseUp(int button) {
      mouseAction(button, GLFW.GLFW_RELEASE);
   }

   protected abstract void mouseAction(int button, int action);
}
