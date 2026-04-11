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
   private static final Gson GSON;
   public static final boolean DEFAULT_ENABLE_STONECUTTER = true;
   public static final boolean DEFAULT_ENABLE_LEFT_CLICK = false;
   public static final boolean DEFAULT_ENABLE_RIGHT_CLICK = true;
   public static final boolean DEFAULT_ALWAYS_ON = true;
   public static final boolean DEFAULT_ALT_HOLD = true;
   public static final boolean DEFAULT_CTRL_HOLD = true;
   public static final boolean DEFAULT_DROP_ENABLE = true;
   public static final int DEFAULT_REPEAT_DELAY = 6;
   //~ if >= 1.21.9 '1' -> '0'
   public static final int DEFAULT_REPEAT_INTERVAL = 1;
   public static final boolean DEFAULT_DEBUG_LOGGING = false;
   public static final int DEFAULT_TOGGLE_HOLD_KEYCODE = -1;
   public static final int DEFAULT_REPEAT_LAST_KEYCODE = -1;
   static {
      CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("oneclickcrafting.json");
      INSTANCE = new OneClickCraftingConfig();
      GSON = new GsonBuilder()
              .setPrettyPrinting()
              .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
              .create();
   }

   private boolean enableStonecutter = DEFAULT_ENABLE_STONECUTTER;
   private boolean enableLeftClick = DEFAULT_ENABLE_LEFT_CLICK;
   private boolean enableRightClick = DEFAULT_ENABLE_RIGHT_CLICK;
   private boolean alwaysOn = DEFAULT_ALWAYS_ON;
   private boolean altHold = DEFAULT_ALT_HOLD;
   private boolean ctrlHold = DEFAULT_CTRL_HOLD;
   private boolean dropEnable = DEFAULT_DROP_ENABLE;
   private int repeatDelay = DEFAULT_REPEAT_DELAY;
   private int repeatInterval = DEFAULT_REPEAT_INTERVAL;
   private boolean debugLogging = DEFAULT_DEBUG_LOGGING;
   private int toggleHoldKeycode = DEFAULT_TOGGLE_HOLD_KEYCODE;
   private int repeatLastKeycode = DEFAULT_REPEAT_LAST_KEYCODE;

   public static OneClickCraftingConfig getInstance() {
      return INSTANCE;
   }

   public static void loadModConfig() {
      if (Files.exists(CONFIG_PATH)) {
         try {
            String s = Files.readString(CONFIG_PATH);
            OneClickCraftingConfig config = GSON.fromJson(s, OneClickCraftingConfig.class);
            OneClickCraftingConfig instance = getInstance();
            instance.enableStonecutter = config.enableStonecutter;
            instance.enableLeftClick = config.enableLeftClick;
            instance.enableRightClick = config.enableRightClick;
            instance.alwaysOn = config.alwaysOn;
            instance.altHold = config.altHold;
            instance.ctrlHold = config.ctrlHold;
            instance.dropEnable = config.dropEnable;
            instance.repeatDelay = config.repeatDelay;
            instance.repeatInterval = config.repeatInterval;
            instance.debugLogging = config.debugLogging;
            instance.toggleHoldKeycode = config.toggleHoldKeycode;
            instance.repeatLastKeycode = config.repeatLastKeycode;
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

   public boolean isEnableStonecutter() {
      return enableStonecutter;
   }

   public void setEnableStonecutter(boolean enableStonecutter) {
      this.enableStonecutter = enableStonecutter;
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

   public int getRepeatDelay() {
      return repeatDelay;
   }

   public void setRepeatDelay(int repeatDelay) {
      this.repeatDelay = repeatDelay;
   }

   public int getRepeatInterval() {
      return repeatInterval;
   }

   public void setRepeatInterval(int repeatInterval) {
      this.repeatInterval = repeatInterval;
   }

   public boolean isDebugLogging() {
      return debugLogging;
   }

   public void setDebugLogging(boolean debugLogging) {
      this.debugLogging = debugLogging;
   }

   public int getToggleHoldKeycode() {
      return toggleHoldKeycode;
   }

   public void setToggleHoldKeycode(int toggleHoldKeycode) {
      this.toggleHoldKeycode = toggleHoldKeycode;
   }

   public int getRepeatLastKeycode() {
      return repeatLastKeycode;
   }

   public void setRepeatLastKeycode(int repeatLastKeycode) {
      this.repeatLastKeycode = repeatLastKeycode;
   }
}
