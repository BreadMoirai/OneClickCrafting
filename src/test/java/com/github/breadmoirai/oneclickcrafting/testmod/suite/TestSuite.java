package com.github.breadmoirai.oneclickcrafting.testmod.suite;

import com.github.breadmoirai.oneclickcrafting.testmod.ConfigHelper;
import com.github.breadmoirai.oneclickcrafting.testmod.RecipeBookHelper;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

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
      this.recipeBook = new RecipeBookHelper(context);
      this.config = new ConfigHelper(context);
   }

   public static TestSingleplayerContext createTestWorld(ClientGameTestContext context) {
      TestSingleplayerContext world = context.worldBuilder()
         .setUseConsistentSettings(true)
         .create();
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
      context.waitFor(mc -> mc.currentScreen == null);
   }

   // -------------------------------------------------------------------------
   // Server command helpers
   // -------------------------------------------------------------------------

   /** Clears the player's entire inventory. */
   protected void clearInventory() {
      world.getServer().runCommand("clear @a");
   }

   /**
    * Kills all item entities in the world.
    * Call before tests that assert items on (or not on) the ground.
    */
   protected void clearGroundItems() {
      world.getServer().runCommand("kill @e[type=minecraft:item]");
   }

   /**
    * Gives the specified item to the player.
    *
    * @param itemId namespaced item id, e.g. {@code "minecraft:oak_log"}
    * @param count  stack count
    */
   protected void giveItem(String itemId, int count) {
      world.getServer().runCommand("give @a " + itemId + " " + count);
   }

   /**
    * Opens the player's inventory by pressing the inventory key binding and waits
    * until {@link InventoryScreen} is active.
    */
   protected void openInventory() {
      context.getInput().pressKey(options -> options.inventoryKey);
      context.waitForScreen(InventoryScreen.class);
   }

   protected void openBlock(String block, Class<? extends Screen> screen) {
      BlockPos playerPos = context.computeOnClient(mc ->
      {
         assert mc.player != null;
         return mc.player.getBlockPos();
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
            Vec3d.ofCenter(stonePos), Direction.WEST, stonePos, false);
         assert mc.interactionManager != null;
         mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
      });
      context.waitForScreen(screen);
   }


   // -------------------------------------------------------------------------
   // Wait helpers
   // -------------------------------------------------------------------------

   /** Waits exactly one game tick. */
   protected void waitTick() {
      context.waitTick();
   }

   /** Waits {@code ticks} game ticks. */
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

   /**
    * Asserts that the player's inventory contains exactly the items in {@code items}
    * and nothing else. Waits up to 20 ticks for the state to match, then throws
    * {@link AssertionError} with a descriptive message if it does not.
    *
    * @param items map of namespaced item ID → expected count
    */
   protected void assertInventoryExact(Map<String, Integer> items) {
      try {
         context.waitFor(mc -> {
            assert mc.player != null;
            Map<String, Integer> counter = new HashMap<>(items);
            var inv = mc.player.getInventory();
            for (int i = 0; i < inv.size(); i++) {
               ItemStack stack = inv.getStack(i);
               String id = Registries.ITEM.getId(stack.getItem()).toString();
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

   /**
    * Asserts the player's inventory contains exactly {@code expectedCount} of
    * {@code itemId}, ignoring all other items.
    *
    * @param itemId namespaced item ID, e.g. {@code "minecraft:oak_planks"}
    */
   protected void assertInventoryCount(String itemId, int expectedCount) {
      int actual = context.computeOnClient(mc -> {
         if (mc.player == null) return 0;
         var inv = mc.player.getInventory();
         int total = 0;
         for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (Registries.ITEM.getId(stack.getItem()).toString().equals(itemId)) total += stack.getCount();
         }
         return total;
      });
      if (actual != expectedCount) {
         throw new AssertionError(
            "Expected exactly %d of %s in inventory, found %d"
               .formatted(expectedCount, itemId, actual));
      }
   }

   /**
    * Asserts the player's inventory contains at least {@code minCount} of
    * {@code itemId}, ignoring all other items. Retries for up to 20 ticks to
    * allow for server round-trip latency.
    *
    * @param itemId namespaced item ID, e.g. {@code "minecraft:oak_planks"}
    */
   protected void assertInventoryAtLeast(String itemId, int minCount) {
      try {
         context.waitFor(mc -> {
            if (mc.player == null) return false;
            var inv = mc.player.getInventory();
            int total = 0;
            for (int i = 0; i < inv.size(); i++) {
               ItemStack stack = inv.getStack(i);
               if (Registries.ITEM.getId(stack.getItem()).toString().equals(itemId)) total += stack.getCount();
            }
            return total >= minCount;
         }, 20);
      } catch (AssertionError timeout) {
         int actual = context.computeOnClient(mc -> {
            if (mc.player == null) return 0;
            var inv = mc.player.getInventory();
            int total = 0;
            for (int i = 0; i < inv.size(); i++) {
               ItemStack stack = inv.getStack(i);
               if (Registries.ITEM.getId(stack.getItem()).toString().equals(itemId)) total += stack.getCount();
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

   /**
    * Asserts there is at least one {@link ItemEntity} carrying {@code itemId} near the player.
    *
    * @param itemId namespaced item ID, e.g. {@code "minecraft:oak_planks"}
    */
   protected void assertItemOnGround(String itemId) {
      try {
         context.waitFor(mc -> {
            if (mc.player == null || mc.world == null) return false;
            Box box = mc.player.getBoundingBox().expand(16.0);
            return !mc.world
               .getEntitiesByClass(ItemEntity.class, box,
                  e -> Registries.ITEM.getId(e.getStack().getItem()).toString().equals(itemId))
               .isEmpty();
         }, 20);
      } catch (AssertionError timeout) {
         throw new AssertionError(
            "Expected ItemEntity with " + itemId + " near player, found none");
      }
   }

   /**
    * Asserts there are NO {@link ItemEntity}s carrying {@code itemId} near the player.
    *
    * @param itemId namespaced item ID, e.g. {@code "minecraft:oak_planks"}
    */
   protected void assertNoItemOnGround(String itemId) {
      boolean found = context.computeOnClient(mc -> {
         if (mc.player == null || mc.world == null) return false;
         Box box = mc.player.getBoundingBox().expand(16.0);
         return !mc.world
            .getEntitiesByClass(ItemEntity.class, box,
               e -> Registries.ITEM.getId(e.getStack().getItem()).toString().equals(itemId))
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
         for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty()) counts.merge(
               Registries.ITEM.getId(stack.getItem()).toString(),
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
