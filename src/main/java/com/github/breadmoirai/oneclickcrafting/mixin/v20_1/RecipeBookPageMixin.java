//? <1.21.1 {
package com.github.breadmoirai.oneclickcrafting.mixin.v20_1;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.recipebook.v20_1.OneClickRecipeBookImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeBookPage.class)
public abstract class RecipeBookPageMixin {

   @Shadow @Final private List<RecipeButton> buttons;
   @Shadow @Nullable private Recipe<?> lastClickedRecipe;
   @Shadow @Nullable private RecipeCollection lastClickedRecipeCollection;

   @Inject(method = "mouseClicked(DDIIIII)Z", at = @At(value = "RETURN", ordinal = 3))
   private void onButtonClicked(double mouseX, double mouseY, int btn, int left, int top, int width, int height, CallbackInfoReturnable<Boolean> cir) {
      for (RecipeButton button : this.buttons) {
         if (!button.isMouseOver(mouseX, mouseY)) continue;
         Recipe<?> recipe = button.getRecipe();
         if (recipe == null) return;
         if (Minecraft.getInstance().level != null) {
            OneClickRecipeBookImpl.lastRecipeResult = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
         }
         if (btn == 1 && OneClickCraftingConfig.getInstance().isEnableRightClick() && button.isOnlyOption()) {
            this.lastClickedRecipe = recipe;
            this.lastClickedRecipeCollection = button.getCollection();
            OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(0, 1);
         } else if (btn == 0) {
            OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(0, 0);
         }
         return;
      }
   }
}
//? }
