package com.github.breadmoirai.oneclickcrafting.input;

import com.github.breadmoirai.oneclickcrafting.mixin.KeyMappingAccessor;
import net.minecraft.client.Minecraft;

public class DropBinding extends InputBindingImpl {
   public DropBinding() {
      super(null);
   }

   @Override
   public boolean isDown() {
      return OneClickCraftingInput.isKeyDown(((KeyMappingAccessor) Minecraft.getInstance().options.keyDrop).getKey().getValue());
   }
}
