package com.github.breadmoirai.oneclickcrafting.testmod.inputhelper;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simulated key-down state for game tests.
 *
 * <p>The test framework's {@code holdKey} fires GLFW key <em>events</em> but does not
 * update the hardware key state that {@code glfwGetKey} polls.  Mixins on
 * {@code InputConstants.isKeyDown} (v26.1) and {@code InputUtil.isKeyPressed} (v21.11)
 * consult this set before delegating to GLFW, so any mod code that polls key state
 * (e.g. {@code DropBinding.isDown()}) will see values set here.
 */
public final class VirtualKeyState {

    private static final Set<Integer> HELD = ConcurrentHashMap.newKeySet();

    private VirtualKeyState() {}

    public static void hold(int keycode) {
        HELD.add(keycode);
    }

    public static void release(int keycode) {
        HELD.remove(keycode);
    }

    public static boolean isHeld(int keycode) {
        return HELD.contains(keycode);
    }

    /** Releases all simulated keys — call at test teardown if needed. */
    public static void clear() {
        HELD.clear();
    }
}
