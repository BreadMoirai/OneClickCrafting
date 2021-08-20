package com.github.breadmoirai.oneclickcrafting.config.modmenu;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OneClickCraftingModMenuEntry implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return OneClickCraftingConfigScreen::new;
    }
}
