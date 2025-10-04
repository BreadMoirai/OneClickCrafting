package com.github.breadmoirai.oneclickcrafting.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
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
}