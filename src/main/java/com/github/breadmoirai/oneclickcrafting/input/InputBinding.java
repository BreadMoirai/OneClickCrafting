package com.github.breadmoirai.oneclickcrafting.input;

public interface InputBinding<B, I> {
   String getId();

   boolean isDown();

   boolean guard(int keycode);

   boolean guard(I input);

   void setBind(B bind);

   void setKey(int keycode);
}
