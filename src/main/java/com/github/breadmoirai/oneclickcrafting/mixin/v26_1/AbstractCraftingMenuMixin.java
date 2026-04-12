//? 26.1 {
package com.github.breadmoirai.oneclickcrafting.mixin.v26_1;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractCraftingMenu.class)
public abstract class AbstractCraftingMenuMixin extends RecipeBookMenu {

   public AbstractCraftingMenuMixin(MenuType<?> menuType, int containerId) {
      super(menuType, containerId);
   }

   @Override
   public void setItem(final int slot, final int stateId, final @NonNull ItemStack itemStack) {
      super.setItem(slot, stateId, itemStack);
      if ((Object) this instanceof CraftingMenu) {
         if (slot == CraftingMenu.RESULT_SLOT && !itemStack.isEmpty())
            OneClickEvents.RESULT_SLOT_UPDATE.invoker().onResultSlotUpdate(new OneClickItemStack(itemStack));
      } else if ((Object) this instanceof InventoryMenu) {
         if (slot == InventoryMenu.RESULT_SLOT && !itemStack.isEmpty())
            OneClickEvents.RESULT_SLOT_UPDATE.invoker().onResultSlotUpdate(new OneClickItemStack(itemStack));
      }
   }
}
//?}