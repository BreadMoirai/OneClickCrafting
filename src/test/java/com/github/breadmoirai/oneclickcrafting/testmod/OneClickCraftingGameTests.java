package com.github.breadmoirai.oneclickcrafting.testmod;

import com.github.breadmoirai.oneclickcrafting.testmod.suite.TestSuite;
import com.github.breadmoirai.oneclickcrafting.testmod.suite.*;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import org.lwjgl.glfw.GLFW;

/**
 * Entry point for all OneClickCrafting client gametests.
 * Registered via the "fabric-client-gametest" entrypoint in the testmod's fabric.mod.json.
 */
@SuppressWarnings("UnstableApiUsage")
public class OneClickCraftingGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
//         Suite 1: Config persistence (no world needed)
        ConfigSuite.testConfigDefaultsShownInUi(context);
        ConfigSuite.testConfigPersistenceViaUi(context);

        try (TestSingleplayerContext world = TestSuite.createTestWorld(context)) {

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
            repeatLastTests.repeatLastStack();
            repeatLastTests.repeatLastStacksFullInventory();
            repeatLastTests.repeatLastDropKeyDropsManyItems();

            // Suite 6 (alt): Repeat last (bound to space)
            RepeatLastTests.REPEAT_KEY_CODE = GLFW.GLFW_KEY_SPACE;
            repeatLastTests.repeatLastReCrafts();
            repeatLastTests.repeatLastStack();
            repeatLastTests.repeatLastStacksFullInventory();
            repeatLastTests.repeatLastDropKeyDropsManyItems();

            // Suites 7–8: Stack accumulation + click sequences (recipe-book and stonecutter)
            StackAndSequenceTests stackAndSequenceTests = new StackAndSequenceTests(context, world);
            stackAndSequenceTests.rightClickPartialStack();
            stackAndSequenceTests.rightClickManyTimes();
            stackAndSequenceTests.stackAccumulation();
            stackAndSequenceTests.stackAccumulated();
            stackAndSequenceTests.stackAccumulationPartial();
            stackAndSequenceTests.stackAccumulatedPartial();
            stackAndSequenceTests.leftThenRightSequence();
            stackAndSequenceTests.leftLeftLeftRightSequence();

            //? 26.1 {
            // Suite 9: Multi-option (cycling) recipe buttons and overlay submenu
            RecipeAlternativesTests recipeAlternativesTests = new RecipeAlternativesTests(context, world);
            recipeAlternativesTests.multiOptionLeftClickDisabledPlacesRecipe();
            recipeAlternativesTests.multiOptionLeftClickEnabledCrafts();
            recipeAlternativesTests.multiOptionOverlayRightClickOakPlanks();
            recipeAlternativesTests.multiOptionOverlayRightClickBirchPlanks();
            recipeAlternativesTests.multiOptionOverlayLeftClickEnabledCrafts();
            recipeAlternativesTests.multiOptionOverlayRightClickDisabledNoAction();
            //?}
        }
    }
}
