package com.github.breadmoirai.oneclickcrafting.testmod.suite;

import com.github.breadmoirai.oneclickcrafting.testmod.ConfigHelper;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

/**
 * Suite 1 — Config tests.
 *
 * All interactions go through the ModMenu → YACL config screen UI using simulated
 * mouse clicks. No mod API calls are made.
 */
@SuppressWarnings("UnstableApiUsage")
public final class ConfigSuite {

    // Documented defaults from OneClickCraftingConfig field initializers
    private static final boolean DEFAULT_STONECUTTER = true;
    private static final boolean DEFAULT_LEFT_CLICK  = false;
    private static final boolean DEFAULT_RIGHT_CLICK = true;
    private static final boolean DEFAULT_ALWAYS_ON   = true;
    private static final boolean DEFAULT_ALT_HOLD    = true;
    private static final boolean DEFAULT_CTRL_HOLD   = true;
    private static final boolean DEFAULT_DROP_ENABLE = true;

    private ConfigSuite() {}

    /**
     * Verifies that the YACL config screen correctly displays the documented default values.
     *
     * <p>Flow:
     * <ol>
     *   <li>Open config via ModMenu; set every toggle to its documented default value.
     *   <li>Save and close.
     *   <li>Reopen; assert every toggle reflects the expected default.
     * </ol>
     */
    public static void testConfigDefaultsShownInUi(ClientGameTestContext context) {
        ConfigHelper cfg = new ConfigHelper(context);

        // --- Setup: drive all options to their documented defaults ---
        cfg.openConfigViaModMenu();
        cfg.setYaclToggle(ConfigHelper.LABEL_STONECUTTER, DEFAULT_STONECUTTER);
        cfg.setYaclToggle(ConfigHelper.LABEL_LEFT_CLICK,  DEFAULT_LEFT_CLICK);
        cfg.setYaclToggle(ConfigHelper.LABEL_RIGHT_CLICK, DEFAULT_RIGHT_CLICK);
        cfg.setYaclToggle(ConfigHelper.LABEL_ALWAYS_ON,   DEFAULT_ALWAYS_ON);
        cfg.setYaclToggle(ConfigHelper.LABEL_ALT_HOLD,    DEFAULT_ALT_HOLD);
        cfg.setYaclToggle(ConfigHelper.LABEL_CTRL_HOLD,   DEFAULT_CTRL_HOLD);
        cfg.setYaclToggle(ConfigHelper.LABEL_DROP_ENABLE, DEFAULT_DROP_ENABLE);
        cfg.saveAndCloseYacl();

        // --- Verification: reopen and assert each displayed value ---
        cfg.openConfigViaModMenu();
        assertToggle(cfg, ConfigHelper.LABEL_STONECUTTER, DEFAULT_STONECUTTER);
        assertToggle(cfg, ConfigHelper.LABEL_LEFT_CLICK,  DEFAULT_LEFT_CLICK);
        assertToggle(cfg, ConfigHelper.LABEL_RIGHT_CLICK, DEFAULT_RIGHT_CLICK);
        assertToggle(cfg, ConfigHelper.LABEL_ALWAYS_ON,   DEFAULT_ALWAYS_ON);
        assertToggle(cfg, ConfigHelper.LABEL_ALT_HOLD,    DEFAULT_ALT_HOLD);
        assertToggle(cfg, ConfigHelper.LABEL_CTRL_HOLD,   DEFAULT_CTRL_HOLD);
        assertToggle(cfg, ConfigHelper.LABEL_DROP_ENABLE, DEFAULT_DROP_ENABLE);
        cfg.saveAndCloseYacl();

        cfg.closeModsScreen();
    }

    /**
     * Verifies that changes made through the YACL config UI persist across close/reopen cycles.
     *
     * <p>Flow:
     * <ol>
     *   <li>Open config; ensure a known baseline (Left Click = OFF, Always On = ON).
     *   <li>Toggle both options; save.
     *   <li>Reopen; assert both changes survived.
     *   <li>Restore defaults to avoid polluting subsequent tests.
     * </ol>
     */
    public static void testConfigPersistenceViaUi(ClientGameTestContext context) {
        ConfigHelper cfg = new ConfigHelper(context);

        // --- Baseline: ensure known starting state ---
        cfg.openConfigViaModMenu();
        cfg.setYaclToggle(ConfigHelper.LABEL_LEFT_CLICK, false);
        cfg.setYaclToggle(ConfigHelper.LABEL_ALWAYS_ON,  true);
        cfg.saveAndCloseYacl();

        // --- Act: flip both options and save ---
        cfg.openConfigViaModMenu();
        cfg.clickYaclToggle(ConfigHelper.LABEL_LEFT_CLICK);  // OFF → ON
        cfg.clickYaclToggle(ConfigHelper.LABEL_ALWAYS_ON);   // ON  → OFF
        cfg.saveAndCloseYacl();

        // --- Assert: changes must survive a close/reopen ---
        cfg.openConfigViaModMenu();
        assertToggle(cfg, ConfigHelper.LABEL_LEFT_CLICK, true);
        assertToggle(cfg, ConfigHelper.LABEL_ALWAYS_ON,  false);
        cfg.saveAndCloseYacl();

        // --- Teardown: restore defaults so later tests start clean ---
        cfg.openConfigViaModMenu();
        cfg.setYaclToggle(ConfigHelper.LABEL_LEFT_CLICK, DEFAULT_LEFT_CLICK);
        cfg.setYaclToggle(ConfigHelper.LABEL_ALWAYS_ON,  DEFAULT_ALWAYS_ON);
        cfg.saveAndCloseYacl();

        cfg.closeModsScreen();
    }

    // -------------------------------------------------------------------------
    // Private assertion helper
    // -------------------------------------------------------------------------

    private static void assertToggle(ConfigHelper cfg, String label, boolean expected) {
        boolean actual = cfg.getYaclToggleState(label);
        if (actual != expected) {
            throw new AssertionError(
                    "Config toggle '%s': expected %b but was %b"
                            .formatted(label, expected, actual));
        }
    }
}
