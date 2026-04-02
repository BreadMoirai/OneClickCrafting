package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.input.OneClickCraftingInput;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OneClickCraftingMod implements ClientModInitializer {

   private static OneClickCraftingMod INSTANCE;

   public OneClickCraftingConfig config;
   public OneClickCraftingInput input;
   public OneClickCraftingHandler craftingHandler;
   public OneClickStonecuttingHandler stonecuttingHandler;

   public static OneClickCraftingMod getInstance() {
      return INSTANCE;
   }

   @Override
   public void onInitializeClient() {
      INSTANCE = this;
      OneClickCraftingConfig.loadModConfig();
      config = OneClickCraftingConfig.getInstance();
      input = new OneClickCraftingInput();
      input.registerBindings();
      craftingHandler = new OneClickCraftingHandler(this);
      craftingHandler.onInitialize();
      stonecuttingHandler = new OneClickStonecuttingHandler(this);
      stonecuttingHandler.onInitialize();
   }

}
