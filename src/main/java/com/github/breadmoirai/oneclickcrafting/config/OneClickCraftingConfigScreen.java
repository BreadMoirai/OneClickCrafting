package com.github.breadmoirai.oneclickcrafting.config;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SpruceTexts;
import dev.lambdaurora.spruceui.option.SpruceBooleanOption;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.tooltip.TooltipData;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class OneClickCraftingConfigScreen extends SpruceScreen {
   private final Screen parent;

   public OneClickCraftingConfigScreen(Screen parent) {
      super(Text.translatable("config.oneclickcrafting.title"));
      this.parent = parent;
   }

   @Override
   protected void init() {
      super.init();
      SpruceOptionListWidget list = new SpruceOptionListWidget(Position.of(0, 22), this.width, this.height - 36 - 22);

      OneClickCraftingConfig config = OneClickCraftingConfig.getInstance();
      SpruceBooleanOption leftClick = new SpruceBooleanOption(
              "config.oneclickcrafting.left_click",
              config::isEnableLeftClick,
              config::setEnableLeftClick,
              TooltipData.builder().text(Text.translatable("config.oneclickcrafting.left_click.tooltip")).build()
      );
      SpruceBooleanOption rightClick = new SpruceBooleanOption(
              "config.oneclickcrafting.right_click",
              config::isEnableRightClick,
              config::setEnableRightClick,
              TooltipData.builder().text(Text.translatable("config.oneclickcrafting.right_click.tooltip")).build()
      );
      SpruceBooleanOption alwaysOn = new SpruceBooleanOption(
              "config.oneclickcrafting.always_on",
              config::isAlwaysOn,
              config::setAlwaysOn,
              TooltipData.builder().text(Text.translatable("config.oneclickcrafting.always_on.tooltip")).build()
      );
      SpruceBooleanOption altHold = new SpruceBooleanOption(
              "config.oneclickcrafting.alt_hold",
              config::isAltHold,
              config::setAltHold,
              TooltipData.builder().text(Text.translatable("config.oneclickcrafting.alt_hold.tooltip")).build()
      );
      SpruceBooleanOption ctrlHold = new SpruceBooleanOption(
              "config.oneclickcrafting.ctrl_hold",
              config::isCtrlHold,
              config::setCtrlHold,
              TooltipData.builder().text(Text.translatable("config.oneclickcrafting.ctrl_hold.tooltip")).build()
      );
      SpruceBooleanOption dropEnable = new SpruceBooleanOption(
              "config.oneclickcrafting.drop_enable",
              config::isDropEnable,
              config::setDropEnable,
              TooltipData.builder().text(Text.translatable("config.oneclickcrafting.drop_enable.tooltip")).build()
      );

      list.addOptionEntry(leftClick, rightClick);
      list.addSingleOptionEntry(alwaysOn);
      list.addOptionEntry(ctrlHold, altHold);
      list.addSingleOptionEntry(dropEnable);

      SpruceButtonWidget done = new SpruceButtonWidget(Position.of(this, this.width / 2 + 4, this.height - 28), 150, 20, SpruceTexts.GUI_DONE,
              btn -> close());

      addDrawableChild(list);
      addDrawableChild(done);
   }


   @Override
   public void close() {
      OneClickCraftingConfig.saveModConfig();
      if (this.client != null) {
         this.client.setScreen(parent);
      }
   }
}
