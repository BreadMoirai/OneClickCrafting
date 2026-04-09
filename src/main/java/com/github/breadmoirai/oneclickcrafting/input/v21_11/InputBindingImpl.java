//? >=1.21.10 <=1.21.11 {
/*package com.github.breadmoirai.oneclickcrafting.input.v26_1;

import com.github.breadmoirai.oneclickcrafting.input.InputBinding;
import com.github.breadmoirai.oneclickcrafting.input.OneClickCraftingInput;
import com.github.breadmoirai.oneclickcrafting.mixin.KeyMappingAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;

public class InputBindingImpl implements InputBinding<KeyMapping, KeyEvent> {
   private final String id;
   private KeyMapping bind;

   public InputBindingImpl(String id) {
      this.id = id;
   }

   @Override
   public String getId() {
      return id;
   }

   @Override
   public boolean isDown() {
      return OneClickCraftingInput.isKeyDown(((KeyMappingAccessor) bind).getKey().getValue());
   }

   @Override
   public boolean guard(KeyEvent key) {
      return !bind.matches(key);
   }

   @Override
   public boolean guard(int keycode) {
      return ((KeyMappingAccessor) bind).getKey().getValue() != keycode;
   }

   @Override
   public void setBind(KeyMapping bind) {
      this.bind = bind;
   }

   @Override
   public void setKey(int keycode) {
      this.bind.setKey(InputConstants.Type.KEYSYM.getOrCreate(keycode));
   }
}

*///?}
