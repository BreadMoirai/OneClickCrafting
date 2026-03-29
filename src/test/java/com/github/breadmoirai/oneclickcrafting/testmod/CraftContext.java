package com.github.breadmoirai.oneclickcrafting.testmod;

import net.minecraft.item.Item;

/**
 * Abstracts a crafting interaction for either the recipe-book (inventory/crafting-table
 * screen) or the stonecutter, allowing generic test methods to exercise both surfaces
 * without code duplication.
 *
 * <p>Callers always call {@link #prepare(int)} (or the no-arg convenience overload)
 * before calling {@link #click} or reading {@link #result()}.
 */
@SuppressWarnings("UnstableApiUsage")
public interface CraftContext {

    /**
     * Open the appropriate screen and give enough ingredients to produce
     * {@code wantedResults} output items.
     *
     * <p>Also calls {@code clearInventory()} internally, so callers do not need to
     * clear before preparing.
     */
    void prepare(int wantedResults);

    /** Trigger one craft action using the given mouse button (0 = left, 1 = right). */
    void click(int mouseButton);

    /**
     * The item this context produces.
     * For the stonecutter, this is captured dynamically during {@link #prepare}.
     */
    Item result();

    /** Number of output items produced by one craft action. */
    int outputPerCraft();

    /**
     * The config-toggle label whose value enables or disables this crafting feature.
     * Used by tests that verify the "disabled" behaviour.
     */
    String featureToggleLabel();

    /**
     * Open the appropriate screen without clearing the inventory or giving items.
     * Useful for tests that pre-populate the inventory before opening.
     */
    void open();

    /** Close the current screen. */
    void close();

    /** Convenience overload: {@code prepare(outputPerCraft())}. */
    default void prepare() {
        prepare(outputPerCraft());
    }
}
