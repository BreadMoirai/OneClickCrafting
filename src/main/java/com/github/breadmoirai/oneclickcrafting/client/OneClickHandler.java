package com.github.breadmoirai.oneclickcrafting.client;

import com.github.breadmoirai.oneclickcrafting.operation.OneClickOperation;
import static com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingMod.debug;

public abstract class OneClickHandler {
   protected OneClickCraftingMod mod;
   protected int repeatTicks;
   protected boolean isRepeating;
   protected OneClickOperation op;

   public OneClickHandler(OneClickCraftingMod mod) {
      this.mod = mod;
      this.repeatTicks = 0;
      this.isRepeating = false;
      op = null;
   }

   public abstract void onInitialize();

   public final void tick() {
      if (!mod.input.repeatLast.isDown()) {
         repeatTicks = 0;
         isRepeating = false;
         return;
      }
      if (op != null) return;
      if (isRepeating) return;
      repeatTicks += 1;
      if (repeatTicks < mod.config.getRepeatDelay()) return;
      debug("tick: repeat delay elapsed (ticks=" + repeatTicks + "), firing repeat craft");
      isRepeating = true;
      fireRepeatCraft();
   }

   /**
    * Called immediately after a craft cycle completes. If the repeat key is held and the
    * initial delay has elapsed, fires the next craft without waiting for the next tick.
    * Otherwise resets state so {@link #tick()} can fire it once the delay elapses.
    */
   protected void onCraftComplete() {
      debug("onCraftComplete: clearing op, isRepeating=" + isRepeating);
      clearOp();
      if (!isRepeating) return;
      fireRepeatCraft();
   }

   /** Fires one repeat craft using the last-selected recipe. Implemented by each handler. */
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
