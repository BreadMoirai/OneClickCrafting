package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class OneClickCraftingClient implements ClientModInitializer {

   private static OneClickCraftingClient INSTANCE;

   public OneClickCraftingConfig config;
   public KeyBinding toggleHoldKey;
   public KeyBinding repeatLastKey;
   public OneClickCraftingHandler craftingHandler;
   public OneClickStonecuttingHandler stonecuttingHandler;

   public static OneClickCraftingClient getInstance() {
      return INSTANCE;
   }

   @Override
   public void onInitializeClient() {
      INSTANCE = this;
      OneClickCraftingConfig.loadModConfig();
      config = OneClickCraftingConfig.getInstance();
      KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("category.oneclickcrafting.keybindings"));
      toggleHoldKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
         "key.oneclickcrafting.toggle_hold",
         InputUtil.Type.KEYSYM,
         InputUtil.UNKNOWN_KEY.getCode(),
         category
      ));
      repeatLastKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
         "key.oneclickcrafting.repeat_last",
         InputUtil.Type.KEYSYM,
         InputUtil.UNKNOWN_KEY.getCode(),
         category
      ));
      craftingHandler = new OneClickCraftingHandler();
      craftingHandler.onInitialize();
      stonecuttingHandler = new OneClickStonecuttingHandler();
      stonecuttingHandler.onInitialize();
   }

}
