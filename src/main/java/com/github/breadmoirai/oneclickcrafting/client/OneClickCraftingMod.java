package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.input.OneClickCraftingInput;
import com.github.breadmoirai.oneclickcrafting.inventory.OneClickInventory;
import com.github.breadmoirai.oneclickcrafting.recipebook.OneClickRecipeBook;
import com.github.breadmoirai.oneclickcrafting.stonecutter.OneClickStonecutter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class OneClickCraftingMod implements ClientModInitializer {

   private static final Logger LOGGER = LoggerFactory.getLogger("one-click-crafting");
   private static OneClickCraftingMod INSTANCE;

   public static void debug(String message) {
      if (INSTANCE != null && INSTANCE.config.isDebugLogging()) {
         LOGGER.info("[OCC] {}", message);
      }
   }

   public OneClickCraftingConfig config;
   public OneClickCraftingInput input;
   public OneClickCraftingHandler craftingHandler;
   public OneClickStonecuttingHandler stonecuttingHandler;
   public OneClickInventory inventory;
   public OneClickRecipeBook recipeBook;
   public OneClickStonecutter stonecutter;

   public static OneClickCraftingMod getInstance() {
      return INSTANCE;
   }

   @Override
   public void onInitializeClient() {
      INSTANCE = this;
      OneClickCraftingConfig.loadModConfig();
      config = OneClickCraftingConfig.getInstance();
      inventory = OneClickInventory.getInstance();
      recipeBook = OneClickRecipeBook.getInstance();
      stonecutter = OneClickStonecutter.getInstance();
      input = new OneClickCraftingInput();
      input.registerBindings();
      craftingHandler = new OneClickCraftingHandler(this);
      craftingHandler.onInitialize();
      stonecuttingHandler = new OneClickStonecuttingHandler(this);
      stonecuttingHandler.onInitialize();
   }

}
