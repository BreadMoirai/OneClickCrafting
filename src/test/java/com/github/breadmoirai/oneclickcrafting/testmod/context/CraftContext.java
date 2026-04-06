package com.github.breadmoirai.oneclickcrafting.testmod.context;

import com.github.breadmoirai.oneclickcrafting.testmod.suite.TestSuite;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

/**
 * Abstracts a crafting interaction for either the recipe-book (inventory/crafting-table
 * screen) or the stonecutter, allowing generic test methods to exercise both surfaces
 * without code duplication.
 *
 * <p>Callers always call {@link #prepare(int)} (or the no-arg convenience overload)
 * before calling {@link #click} or reading {@link #output()}.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class CraftContext extends TestSuite {

    public final String inputItem;
    public final int inputCount;
    public final String outputItem;
    public final int outputCount;

    public CraftContext(ClientGameTestContext context, TestSingleplayerContext world, String inputItem, int inputCount, String outputItem, int outputCount) {
       super(context, world);
       this.inputItem = inputItem;
        this.inputCount = inputCount;
        this.outputItem = outputItem;
        this.outputCount = outputCount;
    }

    /**
     * Open the appropriate screen and give enough ingredients to produce
     * {@code wantedResults} output items.
     *
     * <p>Also calls {@code clearInventory()} internally, so callers do not need to
     * clear before preparing.
     */
    public abstract void prepare(int operations);

    /** Trigger one craft action using the given mouse button (0 = left, 1 = right). */
    public abstract void click(int mouseButton);

    /**
     * Trigger a left-click that requests a full-stack fill ({@code craftAll=true}).
     * The default implementation delegates to {@code click(0)}.
     */
    public void clickAll() {
        click(0);
    }

    /**
     * The config-toggle label whose value enables or disables this crafting feature.
     * Used by tests that verify the "disabled" behaviour.
     */
    public abstract String featureToggleLabel();

    /**
     * Open the appropriate screen without clearing the inventory or giving items.
     * Useful for tests that pre-populate the inventory before opening.
     */
    public abstract void open();

    /** Close the current screen. */
    public abstract void close();

    /** Convenience overload: {@code prepare(outputPerCraft())}. */
    public void prepare() {
        prepare(1);
    }


}
