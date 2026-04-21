//? <1.21.1 {
package com.github.breadmoirai.oneclickcrafting.mixin.v20_1;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.recipebook.v20_1.OneClickRecipeBookImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.world.item.crafting.Recipe;
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
   private Recipe<?> lastRecipeClicked;

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
      if (this.lastRecipeClicked != null && Minecraft.getInstance().level != null) {
         OneClickRecipeBookImpl.lastRecipeResult = this.lastRecipeClicked.getResultItem(Minecraft.getInstance().level.registryAccess());
      }
      OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(0, this.oneclick$originalButton);
   }
}
//? }
