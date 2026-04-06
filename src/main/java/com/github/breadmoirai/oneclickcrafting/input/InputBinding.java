package com.github.breadmoirai.oneclickcrafting.input;

//? 26.1 {
/*import net.minecraft.client.input.KeyEvent;
*///?} >=1.21.10 <=1.21.11 {
import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.KeyBindingAccessor;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
//?}

public class InputBinding {
   private final String id;
   private KeyBinding bind;

   public InputBinding(String id) {
      this.id = id;
   }

   public String getId() {
      return id;
   }

   public boolean isDown() {
      //? >=1.21.10 <=1.21.11 {
      return OneClickCraftingInput.isKeyDown(((KeyBindingAccessor) bind).getBoundKey().getCode());
      //?} 26.1 {
      /*return bind.isPressed();
      *///?}
   }

//? 26.1 {
   /*public boolean guard(KeyEvent key) {
      return key.key() != getKeycode();
   }
*///?} >=1.21.10 <=1.21.11 {
   public boolean guard(KeyInput key) {
      return !bind.matchesKey(key);
   }

   public boolean guard(int keycode) {
      return ((KeyBindingAccessor) bind).getBoundKey().getCode() != keycode;
   }

   public void setBind(KeyBinding bind) {
      this.bind = bind;
   }

   public void setKey(int keycode) {
      this.bind.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(keycode));
   }

   //?}
}
