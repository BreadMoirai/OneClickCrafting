//? 26.1 {
/*package com.github.breadmoirai.oneclickcrafting.mixin.v26_1;

import com.github.breadmoirai.oneclickcrafting.event.OneClickEvents;
import com.github.breadmoirai.oneclickcrafting.item.OneClickItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
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
      Screen screen = Minecraft.getInstance().screen;
      if (screen instanceof CraftingScreen || screen instanceof InventoryScreen) {
         if (slot == 0 && !itemStack.isEmpty())
            OneClickEvents.RESULT_SLOT_UPDATE.invoker().onResultSlotUpdate(new OneClickItemStack(itemStack));
      } else if (screen instanceof StonecutterScreen) {
         if (slot == 1 && !itemStack.isEmpty())
            OneClickEvents.RESULT_SLOT_UPDATE.invoker().onResultSlotUpdate(new OneClickItemStack(itemStack));
      }
   }
}
*///?}