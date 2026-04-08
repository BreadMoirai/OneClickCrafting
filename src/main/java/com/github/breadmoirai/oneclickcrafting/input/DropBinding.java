package com.github.breadmoirai.oneclickcrafting.input;

//? 26.1 {
import com.github.breadmoirai.oneclickcrafting.input.v26_1.InputBindingImpl;
import com.github.breadmoirai.oneclickcrafting.mixin.v26_1.KeyMappingAccessor;
import net.minecraft.client.Minecraft;
//?} >=1.21.10 <=1.21.11 {
/*import com.github.breadmoirai.oneclickcrafting.input.v21_11.InputBindingImpl;
import com.github.breadmoirai.oneclickcrafting.mixin.v21_11.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
*///?}

public class DropBinding extends InputBindingImpl {
   public DropBinding() {
      super(null);
   }

   @Override
   public boolean isDown() {
      //? 26.1 {
      return OneClickCraftingInput.isKeyDown(((KeyMappingAccessor) Minecraft.getInstance().options.keyDrop).getKey().getValue());
      //?} >=1.21.10 <=1.21.11 {
      /*return OneClickCraftingInput.isKeyDown(((KeyBindingAccessor) MinecraftClient.getInstance().options.dropKey).getBoundKey().getCode());
      *///?}
   }
}
