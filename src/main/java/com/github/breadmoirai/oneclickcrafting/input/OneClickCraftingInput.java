package com.github.breadmoirai.oneclickcrafting.input;

//? 26.1 {
/*import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
*///?} >=1.21.10 <=1.21.11 {

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
//?}

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

public class OneClickCraftingInput {
   public final InputBinding drop;
   public final InputBinding toggleHold;
   public final InputBinding repeatLast;

   public OneClickCraftingInput() {
      drop = new DropBinding();
      toggleHold = new InputBinding(
         "key.oneclickcrafting.toggle_hold"
      );
      repeatLast = new InputBinding(
         "key.oneclickcrafting.repeat_last"
      );
   }

   public void registerBindings() {
      List<InputBinding> bindings = Arrays.asList(toggleHold, repeatLast);
      //? 26.1 {

      //?} >=1.21.10 <=1.21.11 {
      KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("category.oneclickcrafting.keybindings"));
      for (InputBinding binding : bindings) {
         KeyBinding bind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            binding.getId(),
            InputUtil.Type.KEYSYM,
            GLFW_KEY_UNKNOWN,
            category
         ));
         binding.setBind(bind);
      }
      //?}
   }

   public static boolean isKeyDown(int keycode) {
      if (keycode == GLFW_KEY_UNKNOWN) return false;
      //? 26.1 {
      /*return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), keycode);
       *///?} >=1.21.10 <=1.21.11  {
      return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), keycode);
      //?}
   }

   public boolean isShiftDown() {
      return isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
   }

   public boolean isControlDown() {
      return isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
   }

   public boolean isAltDown() {
      return isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT);
   }
}
