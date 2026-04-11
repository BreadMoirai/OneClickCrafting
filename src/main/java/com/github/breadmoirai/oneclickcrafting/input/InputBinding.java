package com.github.breadmoirai.oneclickcrafting.input;

public interface InputBinding<B> {
   String getId();

   boolean isDown();

   boolean guard(int keycode);

   void setBind(B bind);

   void setKey(int keycode);

   boolean matches(int key);
}
