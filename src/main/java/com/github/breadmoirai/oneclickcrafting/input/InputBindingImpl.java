package com.github.breadmoirai.oneclickcrafting.input;

import com.github.breadmoirai.oneclickcrafting.mixin.KeyMappingAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class InputBindingImpl implements InputBinding<KeyMapping> {
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
   public boolean guard(int keycode) {
      return !matches(keycode);
   }

   @Override
   public boolean matches(int keycode) {
      return ((KeyMappingAccessor) bind).getKey().getValue() == keycode;
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
