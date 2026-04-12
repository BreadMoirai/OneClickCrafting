package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.operation.OneClickOperation;

import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;

public abstract class OneClickHandler {
   protected OneClickCraftingMod mod;
   protected boolean isRepeating;
   protected int repeatDelayInitial;
   protected int repeatDelayInterval;
   protected OneClickOperation op;

   public OneClickHandler(OneClickCraftingMod mod) {
      this.mod = mod;
      this.isRepeating = false;
      this.repeatDelayInitial = -1;
      this.repeatDelayInterval = -1;
      this.op = null;
   }

   public abstract void onInitialize();

   public final void tick() {
      if (!mod.input.repeatLast.isDown()) {
         isRepeating = false;
         repeatDelayInitial = -1;
         repeatDelayInterval = -1;
         return;
      }
      if (repeatDelayInitial > 0) {
         repeatDelayInitial--;
         if (repeatDelayInitial == 0) {
            fireRepeatCraft();
         }
      } else if (repeatDelayInterval > 0) {
         repeatDelayInterval--;
      } else if (repeatDelayInterval == 0) {
         repeatDelayInterval = -1;
         fireRepeatCraft();
      }
   }

   /**
    * Called immediately after a craft cycle completes. If the repeat key is held and the
    * initial delay has elapsed, fires the next craft without waiting for the next tick.
    * Otherwise, resets state so {@link #tick()} can fire it once the delay elapses.
    */
   protected void onCraftComplete() {
      debug("onCraftComplete: clearing op, isRepeating=" + isRepeating);
      debug("onCraftComplete: repeatDelayInitial = " + repeatDelayInitial);
      debug("onCraftComplete: repeatDelayInterval = " + repeatDelayInterval);
      clearOp();
      if (!isRepeating) return;
      if (repeatDelayInitial == -1) {
         repeatDelayInitial = mod.config.getRepeatDelay();
         debug("onCraftComplete: set repeatDelayInitial = " + repeatDelayInitial);
      }
      if (repeatDelayInterval == -1) {
         repeatDelayInterval = mod.config.getRepeatInterval();
         debug("onCraftComplete: set repeatDelayInterval = " + repeatDelayInterval);
      }
      if (repeatDelayInitial == 0 && repeatDelayInterval == 0) {
         repeatDelayInterval = -1;
         fireRepeatCraft();
      }
   }

   /**
    * Fires one repeat craft using the last-selected recipe. Implemented by each handler.
    */
   protected abstract void fireRepeatCraft();

   protected boolean hasOp() {
      return this.op != null;
   }

   protected OneClickOperation getOp() {
      return this.op;
   }

   protected void clearOp() {
      this.op = null;
   }

   protected void setOp(OneClickOperation op) {
      this.op = op;
   }
}
