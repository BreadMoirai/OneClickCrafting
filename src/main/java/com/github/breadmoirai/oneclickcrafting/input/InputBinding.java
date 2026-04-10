package com.github.breadmoirai.oneclickcrafting.input;

import net.minecraft.client.input.KeyEvent;

public interface InputBinding<B, I> {
   String getId();

   boolean isDown();

   boolean guard(int keycode);

   boolean guard(I input);

   void setBind(B bind);

   void setKey(int keycode);

   boolean matches(KeyEvent key);

   boolean matches(int key);
}
