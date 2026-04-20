package com.github.breadmoirai.oneclickcrafting.testmod.suite;

import com.github.breadmoirai.oneclickcrafting.testmod.ConfigHelper;
import com.github.breadmoirai.oneclickcrafting.testmod.inputhelper.VirtualKeyState;
import com.github.breadmoirai.oneclickcrafting.testmod.recipebookhelper.RecipeBookHelper;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import org.lwjgl.glfw.GLFW;

import com.github.breadmoirai.oneclickcrafting.mixin.KeyMappingAccessor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public abstract class TestSuite {
   protected ClientGameTestContext context;
   protected TestSingleplayerContext world;
   protected RecipeBookHelper recipeBook;
   protected ConfigHelper config;

   public TestSuite(ClientGameTestContext context, TestSingleplayerContext world) {
      this.context = context;
      this.world = world;
      this.recipeBook = RecipeBookHelper.create(context);
      this.config = new ConfigHelper(context);
   }

   public static TestSingleplayerContext createTestWorld(ClientGameTestContext context) {
      TestSingleplayerContext world = context.worldBuilder()
         .setUseConsistentSettings(true)
         .create();
      //~ if >=26.1 '.getClientWorld()' -> '.getClientLevel()'
      world.getClientWorld().waitForChunksDownload();
      // @a required — runCommand runs as the server console (@s = server, not player)
      world.getServer().runCommand("time set day");
      // Suppress hunger drain so survival mechanics don't interfere with tests
      world.getServer().runCommand("effect give @a minecraft:saturation 1000000 255 true");
      context.waitTick();
      return world;
   }

   /**
    * Closes the current screen by pressing Escape and waits until the game HUD
    * is active (screen == null, i.e. back in the world).
    */
   protected void closeScreen() {
      context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
      context.waitFor(mc -> mc.screen == null);
   }

   // -------------------------------------------------------------------------
   // Server command helpers
   // -------------------------------------------------------------------------

   protected void clearInventory() {
      world.getServer().runCommand("clear @a");
   }

   protected void clearGroundItems() {
      world.getServer().runCommand("kill @e[type=minecraft:item]");
   }

   protected void giveItem(String itemId, int count) {
      world.getServer().runCommand("give @a " + itemId + " " + count);
   }

   protected void openInventory() {
      context.getInput().pressKey(options -> options.keyInventory);
      context.waitForScreen(InventoryScreen.class);
   }

   protected void openBlock(String block, Class<? extends Screen> screen) {
      BlockPos playerPos = context.computeOnClient(mc -> {
         assert mc.player != null;
         return mc.player.blockPosition();
      });

      int sx = playerPos.getX() + 1;
      int sy = playerPos.getY();
      int sz = playerPos.getZ();
      world.getServer().runCommand(
         "execute unless block %d %d %d %s run setblock %d %d %d %s"
            .formatted(sx, sy, sz, block, sx, sy, sz, block));
      context.waitTick();

      BlockPos stonePos = new BlockPos(sx, sy, sz);
      context.runOnClient(mc -> {
         BlockHitResult hitResult = new BlockHitResult(
            Vec3.atCenterOf(stonePos), Direction.WEST, stonePos, false);
         assert mc.gameMode != null;
         mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hitResult);
      });
      context.waitForScreen(screen);
   }

   // -------------------------------------------------------------------------
   // Wait helpers
   // -------------------------------------------------------------------------

   protected void waitTick() {
      context.waitTick();
   }

   protected void wait(int ticks) {
      context.waitTicks(ticks);
   }

   // -------------------------------------------------------------------------
   // Inventory assertions
   // -------------------------------------------------------------------------

   protected void assertInventoryEmpty() {
      assertInventoryExact(Collections.emptyMap());
   }

   protected void assertInventoryExact(String itemId, int count) {
      assertInventoryExact(Map.of(itemId, count));
   }

   protected void assertInventoryExact(String itemId1, int count1, String itemId2, int count2) {
      HashMap<String, Integer> expected = new HashMap<>();
      expected.put(itemId1, count1);
      expected.put(itemId2, count2);
      assertInventoryExact(expected);
   }

   protected void assertInventoryExact(Map<String, Integer> items) {
      try {
         context.waitFor(mc -> {
            assert mc.player != null;
            Map<String, Integer> counter = new HashMap<>(items);
            var inv = mc.player.getInventory();
            for (int i = 0; i < inv.getContainerSize(); i++) {
               ItemStack stack = inv.getItem(i);
               String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
               counter.compute(id, (k, c) -> (c == null ? 0 : c) - stack.getCount());
            }
            return counter.values().stream().mapToInt(x -> x).allMatch(x -> x == 0);
         }, 20);
      } catch (AssertionError timeout) {
         Map<String, Integer> actual = snapshotInventory();
         throw new AssertionError(
            "Expected inventory " + formatItemMap(items)
               + " but found " + formatItemMap(actual));
      }
   }

   protected void assertInventoryCount(String itemId, int expectedCount) {
      int actual = context.computeOnClient(mc -> {
         if (mc.player == null) return 0;
         var inv = mc.player.getInventory();
         int total = 0;
         for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(itemId)) total += stack.getCount();
         }
         return total;
      });
      if (actual != expectedCount) {
         throw new AssertionError(
            "Expected exactly %d of %s in inventory, found %d"
               .formatted(expectedCount, itemId, actual));
      }
   }

   protected void assertInventoryAtLeast(String itemId, int minCount) {
      try {
         context.waitFor(mc -> {
            if (mc.player == null) return false;
            var inv = mc.player.getInventory();
            int total = 0;
            for (int i = 0; i < inv.getContainerSize(); i++) {
               ItemStack stack = inv.getItem(i);
               if (BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(itemId)) total += stack.getCount();
            }
            return total >= minCount;
         }, 20);
      } catch (AssertionError timeout) {
         int actual = context.computeOnClient(mc -> {
            if (mc.player == null) return 0;
            var inv = mc.player.getInventory();
            int total = 0;
            for (int i = 0; i < inv.getContainerSize(); i++) {
               ItemStack stack = inv.getItem(i);
               if (BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(itemId)) total += stack.getCount();
            }
            return total;
         });
         throw new AssertionError(
            "Expected >= %d of %s in inventory, found %d"
               .formatted(minCount, itemId, actual));
      }
   }

   // -------------------------------------------------------------------------
   // Ground item assertions
   // -------------------------------------------------------------------------

   protected void assertItemOnGround(String itemId) {
      try {
         context.waitFor(mc -> {
            if (mc.player == null) return false;
            if (mc.level == null) return false;
            AABB box = mc.player.getBoundingBox().inflate(16.0);
            return !mc.level.getEntitiesOfClass(ItemEntity.class, box,
               e -> BuiltInRegistries.ITEM.getKey(e.getItem().getItem()).toString().equals(itemId))
               .isEmpty();
         }, 20);
      } catch (AssertionError timeout) {
         throw new AssertionError(
            "Expected ItemEntity with " + itemId + " near player, found none");
      }
   }

   protected void assertNoItemOnGround(String itemId) {
      boolean found = context.computeOnClient(mc -> {
         if (mc.player == null || mc.level == null) return false;
         AABB box = mc.player.getBoundingBox().inflate(16.0);
         return !mc.level.getEntitiesOfClass(ItemEntity.class, box,
            e -> BuiltInRegistries.ITEM.getKey(e.getItem().getItem()).toString().equals(itemId))
            .isEmpty();
      });
      if (found) {
         throw new AssertionError(
            "Expected no ItemEntity with " + itemId + " near player, but found one");
      }
   }

   // -------------------------------------------------------------------------
   // Misc helpers
   // -------------------------------------------------------------------------

   protected int stacks(int stacks) {
      return stacks * 64;
   }

   // -------------------------------------------------------------------------
   // Private helpers
   // -------------------------------------------------------------------------

   private Map<String, Integer> snapshotInventory() {
      return context.computeOnClient(mc -> {
         Map<String, Integer> counts = new LinkedHashMap<>();
         if (mc.player == null) return counts;
         var inv = mc.player.getInventory();
         for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) counts.merge(
               BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(),
               stack.getCount(), Integer::sum);
         }
         return counts;
      });
   }

   private static String formatItemMap(Map<String, Integer> map) {
      if (map.isEmpty()) return "{}";
      StringBuilder sb = new StringBuilder("{");
      map.forEach((id, count) -> sb.append(id).append("=").append(count).append(", "));
      sb.setLength(sb.length() - 2);
      return sb.append("}").toString();
   }
}
