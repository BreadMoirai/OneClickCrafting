package com.github.breadmoirai.oneclickcrafting.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class InputHelper {
   public static boolean isToggleKey(KeyInput key) {
      int keycode = key.getKeycode();
      return keycode == GLFW.GLFW_KEY_CAPS_LOCK ||
         keycode == GLFW.GLFW_KEY_NUM_LOCK ||
         keycode == GLFW.GLFW_KEY_SCROLL_LOCK;
   }

   public static boolean isShiftDown() {
      return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) ||
         InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);

   }
   public static boolean isControlDown() {
      return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL) ||
         InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_CONTROL);
   }

   public static boolean isAltDown() {
      return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_ALT) ||
         InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_ALT);
   }

   public static boolean isKeybindingPressed(KeyBinding keyBinding) {
      int code = keyBinding.boundKey.getCode();
      if (code == InputUtil.UNKNOWN_KEY.getCode())
         return false;
      return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), code);
   }

   public static boolean isDropKeyPressed() {
      return isKeybindingPressed(MinecraftClient.getInstance().options.dropKey);
   }

   /**
    * Returns {@code true} if the given {@link KeyInput} event matches the bound key of
    * {@code binding}. Used to detect when the user presses the exact key of a binding
    * (rather than checking whether that key is currently held via {@link #isKeybindingPressed}).
    */
   public static boolean isKeyInputFor(KeyInput keyInput, KeyBinding binding) {
      int code = binding.boundKey.getCode();
      if (code == InputUtil.UNKNOWN_KEY.getCode()) return false;
      return keyInput.getKeycode() == code;
   }
}