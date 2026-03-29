package com.github.breadmoirai.oneclickcrafting.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import java.util.Optional;

/**
 * Copied a bunch of this from
 * <a href="https://github.com/sakura-ryoko/itemscroller/blob/1.21.10/src/main/java/fi/dy/masa/itemscroller/util/InventoryUtils.java">ItemScroller</a>
 */
public class InventoryUtils {
   public static void clickSlot(HandledScreen<? extends ScreenHandler> gui,
                                int slotNum,
                                int mouseButton,
                                SlotActionType type)
   {
      if (slotNum >= 0 && slotNum < gui.getScreenHandler().slots.size())
      {
         Slot slot = gui.getScreenHandler().getSlot(slotNum);
         clickSlot(gui, slot, slotNum, mouseButton, type);
      }
      else
      {
         MinecraftClient mc = MinecraftClient.getInstance();
         ClientPlayerInteractionManager interactionManager = mc.interactionManager;
         if (interactionManager != null) {
            interactionManager.clickSlot(gui.getScreenHandler().syncId, slotNum, mouseButton, type, mc.player);
         }
      }
   }

   public static void clickSlot(HandledScreen<? extends ScreenHandler> gui,
                                Slot slot,
                                int slotNum,
                                int mouseButton,
                                SlotActionType type)
   {
      gui.onMouseClick(slot, slotNum, mouseButton, type);
   }

   public static void leftClickSlot(HandledScreen<? extends ScreenHandler> gui, Slot slot)
   {
      clickSlot(gui, slot, slot.getIndex(), 0, SlotActionType.PICKUP);
   }

   public static void leftClickSlot(HandledScreen<? extends ScreenHandler> gui, int slotNum)
   {
      clickSlot(gui, slotNum, 0, SlotActionType.PICKUP);
   }

   public static void moveMatchingIntoSlot(HandledScreen<? extends ScreenHandler> gui, int slotNum)
   {
      clickSlot(gui, slotNum, 0, SlotActionType.PICKUP);
      clickSlot(gui, slotNum, 0, SlotActionType.PICKUP_ALL);
      clickSlot(gui, slotNum, 0, SlotActionType.PICKUP);
   }

   public static void rightClickSlot(HandledScreen<? extends ScreenHandler> gui, Slot slot)
   {
      clickSlot(gui, slot, slot.getIndex(), 1, SlotActionType.PICKUP);
   }

   public static void rightClickSlot(HandledScreen<? extends ScreenHandler> gui, int slotNum)
   {
      clickSlot(gui, slotNum, 1, SlotActionType.PICKUP);
   }

   public static void shiftClickSlot(HandledScreen<? extends ScreenHandler> gui, Slot slot)
   {
      clickSlot(gui, slot, slot.getIndex(), 0, SlotActionType.QUICK_MOVE);
   }

   public static void shiftClickSlot(HandledScreen<? extends ScreenHandler> gui, int slotNum)
   {
      clickSlot(gui, slotNum, 0, SlotActionType.QUICK_MOVE);
   }

   public static void dropItemsFromCursor(HandledScreen<? extends ScreenHandler> gui)
   {
      clickSlot(gui, -999, 0, SlotActionType.PICKUP);
   }

   public static void dropItem(HandledScreen<? extends ScreenHandler> gui, int slotNum)
   {
      clickSlot(gui, slotNum, 0, SlotActionType.THROW);
   }

   public static void dropStack(HandledScreen<? extends ScreenHandler> gui, int slotNum)
   {
      clickSlot(gui, slotNum, 1, SlotActionType.THROW);
   }

   public static Slot getSlot(HandledScreen<? extends ScreenHandler> gui, int slotNum)
   {
      return gui.getScreenHandler().getSlot(slotNum);
   }

   public static Optional<Slot> findMatchingSlot(HandledScreen<? extends ScreenHandler> gui, Ingredient ingredient)
   {
      DefaultedList<Slot> slots = gui.getScreenHandler().slots;
      for (Slot slot : slots) {
         if (!(slot.inventory instanceof PlayerInventory)) continue;
         if (!Ingredient.matches(Optional.of(ingredient), slot.getStack())) continue;
         return Optional.of(slot);
      }
      return Optional.empty();
   }

   public static Optional<Slot> findEmptySlot(HandledScreen<? extends ScreenHandler> gui)
   {
      DefaultedList<Slot> slots = gui.getScreenHandler().slots;
      for (Slot slot : slots) {
         if (!(slot.inventory instanceof PlayerInventory)) continue;
         if (!slot.getStack().isOf(Items.AIR)) continue;
         return Optional.of(slot);
      }
      return Optional.empty();
   }

}
