package com.github.breadmoirai.oneclickcrafting.config;

import me.lambdaurora.spruceui.Position;
import me.lambdaurora.spruceui.SpruceTexts;
import me.lambdaurora.spruceui.background.SimpleColorBackground;
import me.lambdaurora.spruceui.option.SpruceBooleanOption;
import me.lambdaurora.spruceui.screen.SpruceScreen;
import me.lambdaurora.spruceui.widget.SpruceButtonWidget;
import me.lambdaurora.spruceui.widget.SpruceSeparatorWidget;
import me.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import me.lambdaurora.spruceui.wrapper.VanillaButtonWrapper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class OneClickCraftingConfigScreen extends SpruceScreen {
    private final Screen parent;

    public OneClickCraftingConfigScreen(Screen parent) {
        super(new TranslatableText("config.oneclickcrafting.title"));
        this.parent = parent;
    };

    @Override
    protected void init() {
        super.init();
        SpruceOptionListWidget list = new SpruceOptionListWidget(Position.of(0, 32), this.width, this.height - 36 - 32);

        OneClickCraftingConfig config = OneClickCraftingConfig.getInstance();
        SpruceBooleanOption alwaysOn = new SpruceBooleanOption(
                "config.oneclickcrafting.always_on",
                config::isAlwaysOn,
                config::setAlwaysOn,
                null
        );
        SpruceBooleanOption altHold = new SpruceBooleanOption(
                "config.oneclickcrafting.alt_hold",
                config::isAltHold,
                config::setAltHold,
                new TranslatableText("config.oneclickcrafting.alt_hold.tooltip")
        );
        SpruceBooleanOption ctrlHold = new SpruceBooleanOption(
                "config.oneclickcrafting.ctrl_hold",
                config::isCtrlHold,
                config::setCtrlHold,
                new TranslatableText("config.oneclickcrafting.ctrl_hold.tooltip")
        );
        SpruceBooleanOption singleClickEnable = new SpruceBooleanOption(
                "config.oneclickcrafting.single_click_enable",
                config::isSingleClickEnable,
                config::setSingleClickEnable,
                new TranslatableText("config.oneclickcrafting.single_click_enable.tooltip")
        );
        SpruceBooleanOption dropEnable = new SpruceBooleanOption(
                "config.oneclickcrafting.drop_enable",
                config::isDropEnable,
                config::setDropEnable,
                new TranslatableText("config.oneclickcrafting.drop_enable.tooltip")
        );

        list.addSingleOptionEntry(alwaysOn);
        list.addSingleOptionEntry(ctrlHold);
        list.addSingleOptionEntry(altHold);
        list.addSingleOptionEntry(singleClickEnable);
        list.addSingleOptionEntry(dropEnable);
        list.setRenderTransition(false);
        list.setBackground(new SimpleColorBackground(0, 0, 0, 0));

        VanillaButtonWrapper done = new SpruceButtonWidget(Position.of(this, this.width / 2  - 75, this.height - 28), 150, 20, SpruceTexts.GUI_DONE,
                btn -> {
                    if (this.client != null) {
                        this.client.method_29970(this.parent);
                    }
                }).asVanilla();

        this.children.add(list);
        this.addButton(done);
    }

    @Override
    public void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 16, 16777215);
    }

    @Override
    public void onClose() {
        if (this.client != null) {
            this.client.method_29970(this.parent); // setScreen
        }
        OneClickCraftingConfig.saveModConfig();
    }
}
