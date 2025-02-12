package com.github.breadmoirai.oneclickcrafting.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public class OneClickCraftingConfig {
   private static final Path CONFIG_PATH;
   private static final OneClickCraftingConfig INSTANCE;
   private static final transient Gson GSON;

   static {
      CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("oneclickcrafting.json");
      INSTANCE = new OneClickCraftingConfig();
      GSON = new GsonBuilder()
              .setPrettyPrinting()
              .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
              .create();
   }

   private boolean enableLeftClick = false;
   private boolean enableRightClick = true;
   private boolean alwaysOn = true;
   private boolean altHold = true;
   private boolean ctrlHold = true;
   private boolean dropEnable = true;

   public static OneClickCraftingConfig getInstance() {
      return INSTANCE;
   }

   public static void loadModConfig() {
      if (Files.exists(CONFIG_PATH)) {
         try {
            String s = Files.readString(CONFIG_PATH);
            OneClickCraftingConfig config = GSON.fromJson(s, OneClickCraftingConfig.class);
            OneClickCraftingConfig instance = getInstance();
            instance.enableLeftClick = config.enableLeftClick;
            instance.enableRightClick = config.enableRightClick;
            instance.alwaysOn = config.alwaysOn;
            instance.altHold = config.altHold;
            instance.ctrlHold = config.ctrlHold;
            instance.dropEnable = config.dropEnable;
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else {
         saveModConfig();
      }
   }

   public static void saveModConfig() {
      System.out.println("Saving OneClickCrafting Mod Config to " + CONFIG_PATH);
      try {
         String s = GSON.toJson(getInstance());
         Files.writeString(CONFIG_PATH, s);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public boolean isEnableLeftClick() {
      return enableLeftClick;
   }

   public void setEnableLeftClick(boolean enableLeftClick) {
      this.enableLeftClick = enableLeftClick;
   }

   public boolean isEnableRightClick() {
      return enableRightClick;
   }

   public void setEnableRightClick(boolean enableRightClick) {
      this.enableRightClick = enableRightClick;
   }

   public boolean isAlwaysOn() {
      return alwaysOn;
   }

   public void setAlwaysOn(boolean alwaysOn) {
      this.alwaysOn = alwaysOn;
   }

   public boolean isAltHold() {
      return altHold;
   }

   public void setAltHold(boolean altHold) {
      this.altHold = altHold;
   }

   public boolean isCtrlHold() {
      return ctrlHold;
   }

   public void setCtrlHold(boolean ctrlHold) {
      this.ctrlHold = ctrlHold;
   }

   public boolean isDropEnable() {
      return dropEnable;
   }

   public void setDropEnable(boolean dropEnable) {
      this.dropEnable = dropEnable;
   }
}
