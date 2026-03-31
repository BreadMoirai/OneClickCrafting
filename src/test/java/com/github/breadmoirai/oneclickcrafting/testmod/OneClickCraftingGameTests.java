package com.github.breadmoirai.oneclickcrafting.testmod;

import com.github.breadmoirai.oneclickcrafting.testmod.suite.*;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

/**
 * Entry point for all OneClickCrafting client gametests.
 * Registered via the "fabric-client-gametest" entrypoint in the testmod's fabric.mod.json.
 */
@SuppressWarnings("UnstableApiUsage")
public class OneClickCraftingGameTests implements FabricClientGameTest {

    @SuppressWarnings("ExtractMethodRecommender")
    @Override
    public void runTest(ClientGameTestContext context) {
        // Suite 1: Config persistence (no world needed)
        ConfigSuite.testConfigDefaultsShownInUi(context);
        ConfigSuite.testConfigPersistenceViaUi(context);

        try (TestSingleplayerContext world = GameplayTestHelper.createTestWorld(context)) {

            // Suite 2–4: Basic crafting + drop-key (recipe-book and stonecutter)
            BasicCraftTests basicCraft = new BasicCraftTests(context, world);
            basicCraft.leftClickDisabledNoAction();
            basicCraft.leftClickMovesToInventory();
            basicCraft.rightClickMovesToInventory();
            basicCraft.rightClickDisabledNoAction();
            basicCraft.dropKeyDropsItem();
            basicCraft.dropKeyDisabledMovesToInventory();
            basicCraft.shiftDropDropsItem();

            // Suite 5: Activation modes (recipe-book and stonecutter)
            ActivationModeTests activationModeTests = new ActivationModeTests(context, world);
            activationModeTests.alwaysOffNoModifierNoAction();
            activationModeTests.alwaysOffCtrlHoldEnables();
            activationModeTests.alwaysOnCtrlHoldDisables();
            activationModeTests.alwaysOnAltHoldDisables();

            // Suite 6: Repeat last (recipe-book and stonecutter)
            RepeatLastTests repeatLastTests = new RepeatLastTests(context, world);
            repeatLastTests.repeatLastReCrafts();
            repeatLastTests.repeatLastSingleTwoStacks();
            repeatLastTests.repeatLastStacksFullInventory();
            repeatLastTests.repeatLastDropKeyDropsManyItems();

            // Suites 7–8: Stack accumulation + click sequences (recipe-book and stonecutter)
            StackAndSequenceTests stackAndSequenceTests = new StackAndSequenceTests(context, world);
            stackAndSequenceTests.rightClickPartialStack();
            stackAndSequenceTests.rightClickManyTimes();
            stackAndSequenceTests.stackAccumulationShiftLeftRight();
            stackAndSequenceTests.shiftLeftClickCraftsOnce();
            stackAndSequenceTests.shiftLeftClickMovesToInventory();
            stackAndSequenceTests.leftThenRightSequence();
            stackAndSequenceTests.leftLeftLeftRightSequence();
            stackAndSequenceTests.shiftLeftThenRightSequenceRecipeBook();
            stackAndSequenceTests.shiftLeftThenRightSequenceStonecutter();
            stackAndSequenceTests.leftClicksAccumulateThenRight();
            stackAndSequenceTests.shiftLeftStagedIngredientsThenRight();
            stackAndSequenceTests.shiftLeftStages64LogsThenRight();
            stackAndSequenceTests.leftClicksAccumulateThenShiftRight();
        }
    }
}
