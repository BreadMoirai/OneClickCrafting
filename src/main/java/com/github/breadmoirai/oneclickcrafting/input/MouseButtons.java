package com.github.breadmoirai.oneclickcrafting.input;

import com.mojang.blaze3d.platform.InputConstants;

/**
 * GUI mouse-button codes, and the mapping to this mod's internal button codes.
 *
 * <p>MC 26.3 replaced GLFW with SDL, which numbers mouse buttons differently
 * (GLFW: left 0, right 1 - SDL: left 1, right 3). Always compare a
 * {@code MouseButtonEvent}/{@code MouseButtonInfo} button against {@link #LEFT}/{@link #RIGHT}
 * rather than a literal. The mod's own codes ({@code 0} left, {@code 1} right, {@code -1} drop -
 * see {@code OneClickOperation}) are unrelated to the platform numbering and stay as they are.
 */
public final class MouseButtons {

   public static final int LEFT = InputConstants.MOUSE_BUTTON_LEFT;
   public static final int RIGHT = InputConstants.MOUSE_BUTTON_RIGHT;

   private MouseButtons() {
   }

   /** Maps a platform GUI mouse-button code to the mod's internal code (0 = left, 1 = right). */
   public static int toInternal(int guiButton) {
      return guiButton == RIGHT ? 1 : 0;
   }
}
