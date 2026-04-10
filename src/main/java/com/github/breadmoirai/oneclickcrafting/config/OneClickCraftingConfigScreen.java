package com.github.breadmoirai.oneclickcrafting.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class OneClickCraftingConfigScreen extends YACLScreen {

   public OneClickCraftingConfigScreen(Screen parent) {
      super(createConfig(), parent);
   }
   private static Component text(String key) {
      return Component.translatable(key);
   }

   private static Component literal(String text) {
      return Component.literal(text);
   }

   private static YetAnotherConfigLib createConfig() {
      return YetAnotherConfigLib.createBuilder()
         .title(text("config.oneclickcrafting.title"))
         .category(categoryForConfig(OneClickCraftingConfig.getInstance()))
         .save(OneClickCraftingConfig::saveModConfig)
         .build();
   }

   private static ConfigCategory categoryForConfig(OneClickCraftingConfig config) {
      return ConfigCategory.createBuilder()
         .name(text("config.oneclickcrafting.title"))
         .tooltip(text("modmenu.summaryTranslation.one-click-crafting"))
         .group(OptionGroup.createBuilder()
            .option(Option.<Boolean>createBuilder().name(text("config.oneclickcrafting.stonecutter"))
               .description(OptionDescription.of(text("config.oneclickcrafting.stonecutter.tooltip")))
               .binding(true, config::isEnableStonecutter, config::setEnableStonecutter)
               .controller(TickBoxControllerBuilder::create)
               .build()
            ).option(Option.<Boolean>createBuilder().name(text("config.oneclickcrafting.left_click"))
               .description(OptionDescription.of(text("config.oneclickcrafting.left_click.tooltip")))
               .binding(false, config::isEnableLeftClick, config::setEnableLeftClick)
               .controller(TickBoxControllerBuilder::create)
               .build()
            ).option(Option.<Boolean>createBuilder().name(text("config.oneclickcrafting.right_click"))
               .description(OptionDescription.of(text("config.oneclickcrafting.right_click.tooltip")))
               .binding(false, config::isEnableRightClick, config::setEnableRightClick)
               .controller(TickBoxControllerBuilder::create)
               .build()
            ).option(Option.<Boolean>createBuilder().name(text("config.oneclickcrafting.always_on"))
               .description(OptionDescription.of(text("config.oneclickcrafting.always_on.tooltip")))
               .binding(false, config::isAlwaysOn, config::setAlwaysOn)
               .controller(TickBoxControllerBuilder::create)
               .build()
            ).option(Option.<Boolean>createBuilder().name(text("config.oneclickcrafting.alt_hold"))
               .description(OptionDescription.of(text("config.oneclickcrafting.alt_hold.tooltip")))
               .binding(false, config::isAltHold, config::setAltHold)
               .controller(TickBoxControllerBuilder::create)
               .build()
            ).option(Option.<Boolean>createBuilder().name(text("config.oneclickcrafting.ctrl_hold"))
               .description(OptionDescription.of(text("config.oneclickcrafting.ctrl_hold.tooltip")))
               .binding(false, config::isCtrlHold, config::setCtrlHold)
               .controller(TickBoxControllerBuilder::create)
               .build()
            ).option(Option.<Boolean>createBuilder().name(text("config.oneclickcrafting.drop_enable"))
               .description(OptionDescription.of(text("config.oneclickcrafting.drop_enable.tooltip")))
               .binding(false, config::isDropEnable, config::setDropEnable)
               .controller(TickBoxControllerBuilder::create)
               .build()
            ).option(Option.<Integer>createBuilder().name(text("config.oneclickcrafting.repeat_delay"))
               .description(OptionDescription.of(text("config.oneclickcrafting.repeat_delay.tooltip")))
               .binding(6, config::getRepeatDelay, config::setRepeatDelay)
               .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                  .range(0, 40)
                  .step(1)
                  .formatValue(v -> literal(v == 0 ? "None" : v == 1 ? "1 tick" : v + " ticks")))
               .build()
            ).option(Option.<Integer>createBuilder().name(text("config.oneclickcrafting.repeat_interval"))
               .description(OptionDescription.of(text("config.oneclickcrafting.repeat_interval.tooltip")))
               .binding(0, config::getRepeatInterval, config::setRepeatInterval)
               .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                  .range(0, 40)
                  .step(1)
                  .formatValue(v -> literal(v == 0 ? "Instant" : v == 1 ? "1 tick" : v + " ticks")))
               .build()
            ).option(Option.<Boolean>createBuilder().name(text("config.oneclickcrafting.debug_logging"))
               .description(OptionDescription.of(text("config.oneclickcrafting.debug_logging.tooltip")))
               .binding(false, config::isDebugLogging, config::setDebugLogging)
               .controller(TickBoxControllerBuilder::create)
               .build()
            ).build()
         ).build();
   }
}
