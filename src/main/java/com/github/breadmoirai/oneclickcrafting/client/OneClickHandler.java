package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.util.InputHelper;
import net.minecraft.item.ItemStack;

public abstract class OneClickHandler {

   protected ItemStack lastCraft;
   protected boolean isDropping;
   protected boolean isShiftDropping;
   protected int lastButton;
   protected boolean isPending;

   /** Timestamp (ms) when the repeatLastKey was first pressed for the current hold sequence. -1 = not in a sequence. */
   protected long repeatInitialPressTime = -1;
   /** Number of repeat crafts that have fired during the current hold sequence (0 = none yet). */
   protected int repeatCraftCount = 0;

   public abstract void onInitialize();

   /**
    * Called every screen tick. Handles two responsibilities:
    * <ol>
    *   <li>Detects when {@code repeatLastKey} becomes held without a keyboard callback
    *       (e.g. {@code holdKey} in tests) and fires the first craft immediately.</li>
    *   <li>Fires the second craft once the initial hold delay has elapsed, as a fallback
    *       for when the first craft completed before the delay was reached.</li>
    * </ol>
    * Crafts #3 and beyond are fired immediately from {@link #onCraftComplete()} rather
    * than waiting for the next tick.
    */
   public final void tick() {
      OneClickCraftingClient occ = OneClickCraftingClient.getInstance();
      boolean isHeld = InputHelper.isKeybindingPressed(occ.repeatLastKey);
      if (!isHeld) {
         if (repeatInitialPressTime != -1) {
            repeatInitialPressTime = -1;
            repeatCraftCount = 0;
         }
         return;
      }
      if (isPending) return;
      if (repeatCraftCount > 0) return; // subsequent crafts are handled by onCraftComplete
      long now = System.currentTimeMillis();
      if (repeatInitialPressTime == -1) {
         // Key held but beforeKeyPress didn't fire (e.g. holdKey without keyboard callback).
         // Treat this tick as the initial press.
         repeatInitialPressTime = now;
         repeatCraftCount = 0;
         fireRepeatCraft();
         return;
      }
      // Wait for the initial hold delay before firing the second craft.
      long delayMs = occ.config.getRepeatDelay() * 50L;
      if ((now - repeatInitialPressTime) < delayMs) return;
      repeatCraftCount++;
      fireRepeatCraft();
   }

   /**
    * Called immediately after a craft cycle completes. If the repeat key is held and the
    * initial delay has elapsed, fires the next craft without waiting for the next tick.
    * Otherwise resets state so {@link #tick()} can fire it once the delay elapses.
    */
   protected void onCraftComplete() {
      reset();
      if (!InputHelper.isKeybindingPressed(OneClickCraftingClient.getInstance().repeatLastKey)) return;
      if (repeatCraftCount == 0) {
         // First craft just finished — fire immediately only if delay has already elapsed.
         long delayMs = OneClickCraftingClient.getInstance().config.getRepeatDelay() * 50L;
         if (repeatInitialPressTime == -1 || (System.currentTimeMillis() - repeatInitialPressTime) < delayMs) return;
      }
      repeatCraftCount++;
      fireRepeatCraft();
   }

   /** Fires one repeat craft using the last-selected recipe. Implemented by each handler. */
   protected abstract void fireRepeatCraft();

   public void reset() {
      isDropping = false;
      isShiftDropping = false;
      lastCraft = null;
      lastButton = -1;
      isPending = false;
   }

   public boolean isEnabled() {
      OneClickCraftingClient client = OneClickCraftingClient.getInstance();
      if (!InputHelper.isKeybindingPressed(client.repeatLastKey)) {
         if (lastButton == 0 && !client.config.isEnableLeftClick()) {
            return false;
         } else if (lastButton == 1 && !client.config.isEnableRightClick()) {
            return false;
         } else if (lastButton == -1) {
            return false;
         }
      }
      boolean alwaysOn = client.config.isAlwaysOn();
      if (client.config.isCtrlHold() && InputHelper.isControlDown()) return !alwaysOn;
      if (client.config.isAltHold() && InputHelper.isAltDown()) return !alwaysOn;
      if (!client.toggleHoldKey.isUnbound() && InputHelper.isKeybindingPressed(client.toggleHoldKey)) return !alwaysOn;
      return alwaysOn;
   }

   public abstract void onResultSlotUpdated(ItemStack slot);

   public void setLastButton(int i) {
      this.lastButton = i;
   }

   public void setLastCraft(ItemStack stack) {
      this.lastCraft = stack;
      this.isPending = true;
   }
}
