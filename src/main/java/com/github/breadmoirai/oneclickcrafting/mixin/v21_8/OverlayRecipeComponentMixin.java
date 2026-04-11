//? <1.21.9 {
package com.github.breadmoirai.oneclickcrafting.mixin.v21_8;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OverlayRecipeComponent.class)
public abstract class OverlayRecipeComponentMixin {

   @Shadow
   private RecipeDisplayId lastRecipeClicked;

   @Unique
   private int oneclick$originalButton;

   @ModifyVariable(method = "mouseClicked(DDI)Z", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private int overrideLeftClickCondition(int button) {
      this.oneclick$originalButton = button;
      if (button == 1 && OneClickCraftingConfig.getInstance().isEnableRightClick())
         return 0;
      return button;
   }

   @Inject(method = "mouseClicked(DDI)Z", at = @At(value = "RETURN", ordinal = 1))
   private void mouseClickedLeft(double x, double y, int button, CallbackInfoReturnable<Boolean> cir) {
      OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(this.lastRecipeClicked.index(), this.oneclick$originalButton);
   }
}
//?}
