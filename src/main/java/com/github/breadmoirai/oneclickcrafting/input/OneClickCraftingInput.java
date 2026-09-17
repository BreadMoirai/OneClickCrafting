package com.github.breadmoirai.oneclickcrafting.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.util.Arrays;
import java.util.List;

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
      Identifier categoryId = Identifier.fromNamespaceAndPath("oneclickcrafting", "keybindings");
      KeyMapping.Category category = KeyMapping.Category.register(categoryId);
      //? }
      for (InputBindingImpl binding : bindings) {
         KeyMapping mapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            binding.getId(),
            InputConstants.Type.KEYBOARD,
            InputConstants.UNKNOWN.getValue(),
            //$ if >=1.21.9 'category' else '"key.category.oneclickcrafting.keybindings"'
            category
         ));
         binding.setBind(mapping);
      }
   }

   public static boolean isKeyDown(int keycode) {
      if (keycode == InputConstants.UNKNOWN.getValue()) return false;
      //? >=1.21.9 {
      Window window = Minecraft.getInstance().getWindow();
      //? } else {
      /*long window = Minecraft.getInstance().getWindow().getWindow();
      *///? }
      // MC 26.3 (SDL) dropped the window argument; `window` is unused there.
      //? if >=26.3 {
      return InputConstants.isKeyDown(keycode);
      //? } else {
      /*return InputConstants.isKeyDown(window, keycode);
      *///? }
   }

   public boolean isShiftDown() {
      return isKeyDown(InputConstants.KEY_LSHIFT) || isKeyDown(InputConstants.KEY_RSHIFT);
   }

   public boolean isControlDown() {
      return isKeyDown(InputConstants.KEY_LCONTROL) || isKeyDown(InputConstants.KEY_RCONTROL);
   }

   public boolean isAltDown() {
      return isKeyDown(InputConstants.KEY_LALT) || isKeyDown(InputConstants.KEY_RALT);
   }
}
