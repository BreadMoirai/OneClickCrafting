package com.github.breadmoirai.oneclickcrafting.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class OneClickCraftingClient implements ClientModInitializer {

    public static ItemStack lastCraft;

    @Override
    public void onInitializeClient() {

    }
}
