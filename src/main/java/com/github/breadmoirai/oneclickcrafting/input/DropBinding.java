package com.github.breadmoirai.oneclickcrafting.input;

//? 26.1 {
/*import net.minecraft.client.Minecraft;
*///?} >=1.21.10 <=1.21.11 {
import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
//?}

public class DropBinding extends InputBinding {
   public DropBinding() {
      super(null);
   }

   @Override
   public boolean isDown() {
      //? 26.1 {
      /*return Minecraft.getInstance().options.keyDrop.isDown();
      *///?} >=1.21.10 <=1.21.11 {
      return OneClickCraftingInput.isKeyDown(((KeyBindingAccessor) MinecraftClient.getInstance().options.dropKey).getBoundKey().getCode());
      //?}
   }
}
