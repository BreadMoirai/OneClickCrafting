//? >=1.21.10 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.input.v21_11;

import com.github.breadmoirai.oneclickcrafting.input.InputBinding;
import com.github.breadmoirai.oneclickcrafting.input.OneClickCraftingInput;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.KeyBindingAccessor;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class InputBindingImpl implements InputBinding<KeyBinding, KeyInput> {
   private final String id;
   private KeyBinding bind;

   public InputBindingImpl(String id) {
      this.id = id;
   }

   @Override
   public String getId() {
      return id;
   }

   @Override
   public boolean isDown() {
      return OneClickCraftingInput.isKeyDown(((KeyBindingAccessor) bind).getBoundKey().getCode());
   }

   @Override
   public boolean guard(KeyInput key) {
      return !bind.matchesKey(key);
   }

   @Override
   public boolean guard(int keycode) {
      return ((KeyBindingAccessor) bind).getBoundKey().getCode() != keycode;
   }

   @Override
   public void setBind(KeyBinding bind) {
      this.bind = bind;
   }

   @Override
   public void setKey(int keycode) {
      this.bind.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(keycode));
   }
}

*///?}