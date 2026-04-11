package com.github.breadmoirai.oneclickcrafting.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
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
      //? >=1.21.9 {
      /*ResourceLocation categoryId = ResourceLocation.fromNamespaceAndPath("oneclickcrafting", "keybindings");
      KeyMapping.Category category = KeyMapping.Category.register(categoryId);
      *///? }
      for (InputBindingImpl binding : bindings) {
         KeyMapping mapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            binding.getId(),
            InputConstants.Type.KEYSYM,
            GLFW_KEY_UNKNOWN,
            //$ if >=1.21.9 'category' else '"key.categories.oneclickcrafting"'
            "key.categories.oneclickcrafting"
         ));
         binding.setBind(mapping);
      }
   }

   public static boolean isKeyDown(int keycode) {
      if (keycode == GLFW_KEY_UNKNOWN) return false;
      //? >=1.21.9 {
      /*Window window = Minecraft.getInstance().getWindow();
      *///? } else {
      long window = Minecraft.getInstance().getWindow().getWindow();
      //? }
      return InputConstants.isKeyDown(window, keycode);
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
