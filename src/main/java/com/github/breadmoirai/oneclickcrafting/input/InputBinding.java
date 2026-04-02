package com.github.breadmoirai.oneclickcrafting.input;

import net.minecraft.client.input.KeyInput;

public class InputBinding {
   private final String id;
   private int keycode;
   private InputBindingUpdateAction updateAction;

   public InputBinding(String id, int keycode) {
      this.id = id;
      this.keycode = keycode;
   }

   public String getId() {
      return id;
   }

   public int getKeycode() {
      return keycode;
   }

   public boolean isDown() {
      return OneClickCraftingInput.isKeyDown(getKeycode());
   }

   public boolean matches(KeyInput key) {
      return key.getKeycode() == keycode;
   }

   void setUpdateAction(InputBindingUpdateAction updateAction) {
      this.updateAction = updateAction;
   }

   public void update(int keycode) {
      this.keycode = keycode;
      this.updateAction.update(keycode);
   }
}
