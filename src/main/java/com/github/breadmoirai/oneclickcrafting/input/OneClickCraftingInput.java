package com.github.breadmoirai.oneclickcrafting.input;

//? 26.1 {
import com.github.breadmoirai.oneclickcrafting.input.v26_1.InputBindingImpl;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
//?} >=1.21.10 <=1.21.11 {
/*import com.github.breadmoirai.oneclickcrafting.input.v21_11.InputBindingImpl;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
*///?}

import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

public class OneClickCraftingInput {
   public final DropBinding drop;
   public final InputBindingImpl toggleHold;
   public final InputBindingImpl repeatLast;

   public OneClickCraftingInput() {
      drop = new DropBinding();
      toggleHold = new InputBindingImpl(
         "key.oneclickcrafting.toggle_hold"
      );
      repeatLast = new InputBindingImpl(
         "key.oneclickcrafting.repeat_last"
      );
   }

   public void registerBindings() {
      List<InputBindingImpl> bindings = Arrays.asList(toggleHold, repeatLast);
      //? 26.1 {
      Identifier categoryId = Identifier.fromNamespaceAndPath("oneclickcrafting", "keybindings");
      KeyMapping.Category category = KeyMapping.Category.register(categoryId);
         for (InputBindingImpl binding : bindings) {
            KeyMapping mapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
               binding.getId(),
               InputConstants.Type.KEYSYM,
               GLFW_KEY_UNKNOWN,
               category
            ));
            binding.setBind(mapping);
         }
      //?} >=1.21.10 <=1.21.11 {
      /*KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("category.oneclickcrafting.keybindings"));
      for (InputBindingImpl binding : bindings) {
         KeyBinding bind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            binding.getId(),
            InputUtil.Type.KEYSYM,
            GLFW_KEY_UNKNOWN,
            category
         ));
         binding.setBind(bind);
      }
      *///?}
   }

   public static boolean isKeyDown(int keycode) {
      if (keycode == GLFW_KEY_UNKNOWN) return false;
      //? 26.1 {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), keycode);
       //?} >=1.21.10 <=1.21.11  {
      /*return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), keycode);
      *///?}
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
