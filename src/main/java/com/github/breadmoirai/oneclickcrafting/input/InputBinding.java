package com.github.breadmoirai.oneclickcrafting.input;

//? 26.1 {
/*import net.minecraft.client.input.KeyEvent;
*///?} >=1.21.10 <=1.21.11 {
import net.minecraft.client.input.KeyInput;
//?}

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

//? 26.1 {
   /*public boolean matches(KeyEvent key) {
      return key.key() == keycode;
   }
*///?} >=1.21.10 <=1.21.11 {
   public boolean matches(KeyInput key) {
      return key.getKeycode() == keycode;
   }
//?}

   void setUpdateAction(InputBindingUpdateAction updateAction) {
      this.updateAction = updateAction;
   }

   public void update(int keycode) {
      this.keycode = keycode;
      this.updateAction.update(keycode);
   }
}
