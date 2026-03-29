package com.github.breadmoirai.oneclickcrafting.testmod;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * Shared helpers for gameplay (world) tests — world setup, server commands,
 * and inventory / world-state assertions used across all gameplay suites.
 */
@SuppressWarnings("UnstableApiUsage")
public final class GameplayTestHelper {

    private GameplayTestHelper() {}

    // -------------------------------------------------------------------------
    // World setup
    // -------------------------------------------------------------------------

    /**
     * Creates a flat singleplayer survival world for gameplay tests.
     *
     * <p>Survival mode is required: the mod hooks into {@code InventoryScreen} and
     * {@code CraftingScreen}, which are only opened in survival/adventure. Creative
     * mode opens {@code CreativeInventoryScreen} instead, bypassing the recipe book.
     *
     * <p>Callers are responsible for closing the context (try-with-resources).
     */
    public static TestSingleplayerContext createTestWorld(ClientGameTestContext context) {
        TestSingleplayerContext world = context.worldBuilder()
                .setUseConsistentSettings(true)
                .create();
        world.getClientWorld().waitForChunksDownload();
        // @a required — runCommand runs as the server console (@s = server, not player)
        world.getServer().runCommand("time set day");
        // Suppress hunger drain so survival mechanics don't interfere with tests
        world.getServer().runCommand("effect give @a minecraft:saturation 1000000 255 true");
        context.waitTick();
        return world;
    }
}
