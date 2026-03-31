package com.github.breadmoirai.oneclickcrafting.testmod;

import com.github.breadmoirai.oneclickcrafting.client.OneClickCraftingClient;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.client.input.MouseInput;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class OneClickTests {

   protected ClientGameTestContext context;
   protected TestSingleplayerContext world;
   protected RecipeBookHelper recipeBook;
   protected ConfigHelper config;

   // Config label constants — exposed here so suite subclasses can reference them directly
   protected static final String LABEL_STONECUTTER = ConfigHelper.LABEL_STONECUTTER;
   protected static final String LABEL_LEFT_CLICK  = ConfigHelper.LABEL_LEFT_CLICK;
   protected static final String LABEL_RIGHT_CLICK = ConfigHelper.LABEL_RIGHT_CLICK;
   protected static final String LABEL_ALWAYS_ON   = ConfigHelper.LABEL_ALWAYS_ON;
   protected static final String LABEL_ALT_HOLD    = ConfigHelper.LABEL_ALT_HOLD;
   protected static final String LABEL_CTRL_HOLD   = ConfigHelper.LABEL_CTRL_HOLD;
   protected static final String LABEL_DROP_ENABLE = ConfigHelper.LABEL_DROP_ENABLE;

   protected OneClickTests(ClientGameTestContext context, TestSingleplayerContext world) {
      this.context = context;
      this.world = world;
      this.recipeBook = new RecipeBookHelper(context);
      this.config = new ConfigHelper(context);
   }

   // -------------------------------------------------------------------------
   // CraftContext factory methods
   // -------------------------------------------------------------------------

   /**
    * Returns a {@link CraftContext} that uses the recipe-book (inventory screen)
    * to craft {@code recipeId} (e.g. {@code "oak_planks"}).
    */
   protected CraftContext recipeBookContext(String recipeId, Item resultItem, int outputCount) {
      return new CraftContext() {
         @Override
         public void prepare(int wantedResults) {
            clearInventory();
            recipe(recipeId).give(wantedResults);
            open();
         }
         @Override
         public void open() {
            openInventory();
            recipeBook.open();
         }
         @Override
         public void click(int mouseButton) {
            recipeBook.clickRecipeButton(resultItem, mouseButton);
         }
         @Override
         public Item result() { return resultItem; }
         @Override
         public int outputPerCraft() { return outputCount; }
         @Override
         public String featureToggleLabel() { return LABEL_RIGHT_CLICK; }
         @Override
         public void close() { closeScreen(); }
      };
   }

   /**
    * Returns a {@link CraftContext} that uses the recipe-book (crafting table screen)
    * to craft {@code recipeId}.
    */
   protected CraftContext craftingTableContext(String recipeId, Item resultItem, int outputCount) {
      return new CraftContext() {
         @Override
         public void prepare(int wantedResults) {
            clearInventory();
            recipe(recipeId).give(wantedResults);
            open();
         }
         @Override
         public void open() {
            openCraftingTable();
            recipeBook.open();
         }
         @Override
         public void click(int mouseButton) {
            recipeBook.clickRecipeButton(resultItem, mouseButton);
         }
         @Override
         public Item result() { return resultItem; }
         @Override
         public int outputPerCraft() { return outputCount; }
         @Override
         public String featureToggleLabel() { return LABEL_RIGHT_CLICK; }
         @Override
         public void close() { closeScreen(); }
      };
   }

   /**
    * Returns a {@link CraftContext} that uses the stonecutter to craft using
    * {@code inputItem} (e.g. {@code Items.COBBLESTONE}).
    *
    * <p>The result item is captured dynamically during {@link CraftContext#prepare}.
    */
   protected CraftContext stonecutterContext(String inputItemId, Item inputItem, int outputCount) {
      return new CraftContext() {
         private Item capturedResult;

         @Override
         public void prepare(int wantedResults) {
            clearInventory();
            giveItem(inputItemId, wantedResults / outputCount);
            OneClickTests.this.wait(2);
            open();
            putOneItemInInputSlot(inputItem);
            capturedResult = getRecipeResult(0);
         }
         @Override
         public void open() {
            openStonecutter();
         }
         @Override
         public void click(int mouseButton) {
            clickRecipeButton(mouseButton, 0);
         }
         @Override
         public Item result() { return capturedResult; }
         @Override
         public int outputPerCraft() { return 2; }
         @Override
         public String featureToggleLabel() { return LABEL_STONECUTTER; }
         @Override
         public void close() { closeScreen(); }
      };
   }

   // -------------------------------------------------------------------------
   // Screen navigation
   // -------------------------------------------------------------------------

   /**
    * Opens the player's inventory by pressing the inventory key binding and waits
    * until {@link InventoryScreen} is active.
    */
   protected void openInventory() {
      context.getInput().pressKey(options -> options.inventoryKey);
      context.waitForScreen(InventoryScreen.class);
   }

   protected void openBlock(Block block, Class<? extends Screen> screen) {
      BlockPos playerPos = context.computeOnClient(mc ->
      {
         assert mc.player != null;
         return mc.player.getBlockPos();
      });

      int sx = playerPos.getX() + 1;
      int sy = playerPos.getY();
      int sz = playerPos.getZ();
      String blockId = block.asItem().toString();
      world.getServer().runCommand(
         "execute unless block %d %d %d %s run setblock %d %d %d %s"
            .formatted(sx, sy, sz, blockId, sx, sy, sz, blockId));
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

   /**
    * Places a stonecutter block 1 block east of the player and opens it.
    * Waits until {@link StonecutterScreen} is active.
    */
   protected void openStonecutter() {
      openBlock(Blocks.STONECUTTER, StonecutterScreen.class);
   }

   /**
    * Places a crafting table block 1 block east of the player and opens it.
    * Waits until {@link CraftingScreen} is active.
    */
   protected void openCraftingTable() {
      openBlock(Blocks.CRAFTING_TABLE, CraftingScreen.class);
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
   // Stonecutter helpers
   // -------------------------------------------------------------------------

   /**
    * Moves one stack of {@code item} from the player's inventory into the
    * stonecutter input slot via a shift-click. Waits a few ticks for the
    * server to send updated available recipes.
    */
   protected void putItemInInputSlot(Item item) {
      // Wait until the item has arrived in the screen handler's inventory
      try {
         context.waitFor(mc -> {
            if (!(mc.currentScreen instanceof StonecutterScreen screen)) return false;
            StonecutterScreenHandler handler = screen.getScreenHandler();
            return handler.slots.stream().anyMatch(slot -> slot.getStack().isOf(item));
         }, 20);
      } catch (AssertionError timeout) {
         throw new AssertionError("putItemInInputSlot: item not found in inventory: " + item);
      }
      context.runOnClient(mc -> {
         if (!(mc.currentScreen instanceof StonecutterScreen screen)) {
            throw new AssertionError("putItemInInputSlot: not in a StonecutterScreen");
         }
         StonecutterScreenHandler handler = screen.getScreenHandler();
         for (var slot : handler.slots) {
            if (slot.getStack().isOf(item)) {
               screen.onMouseClick(slot, slot.getIndex(), 0, SlotActionType.QUICK_MOVE);
               return;
            }
         }
         throw new AssertionError("putItemInInputSlot: item not found in inventory: " + item);
      });
      wait(2);
   }

   /**
    * Moves exactly one item of {@code item} from the player's inventory into the
    * stonecutter input slot (slot 0), leaving any remaining items of the same type
    * in the player's inventory so that the handler's refill mechanism can use them.
    */
   protected void putOneItemInInputSlot(Item item) {
      context.runOnClient(mc -> {
         if (!(mc.currentScreen instanceof StonecutterScreen screen)) {
            throw new AssertionError("putOneItemInInputSlot: not in a StonecutterScreen");
         }
         StonecutterScreenHandler handler = screen.getScreenHandler();
         Slot sourceSlot = null;
         for (var slot : handler.slots) {
            if (!(slot.inventory instanceof PlayerInventory)) continue;
            if (slot.getStack().isOf(item)) {
               sourceSlot = slot;
               break;
            }
         }
         if (sourceSlot == null) {
            throw new AssertionError(
               "putOneItemInInputSlot: item not found in player inventory: " + item);
         }
         screen.onMouseClick(sourceSlot, sourceSlot.getIndex(), 0, SlotActionType.PICKUP);
         screen.onMouseClick(handler.getSlot(0), 0, 1, SlotActionType.PICKUP);
         screen.onMouseClick(sourceSlot, sourceSlot.getIndex(), 0, SlotActionType.PICKUP);
      });
      wait(2);
   }

   /**
    * Triggers the mod's stonecutter handler for recipe {@code recipeIndex} and sends
    * the corresponding button-click packet so the server fills the output slot.
    *
    * @param mouseButton 0 = left-click, 1 = right-click
    * @param recipeIndex index into the available recipe list
    */
   protected void clickRecipeButton(int mouseButton, int recipeIndex) {
      context.runOnClient(mc -> {
         if (!(mc.currentScreen instanceof StonecutterScreen screen)) {
            throw new AssertionError("clickRecipeButton: not in a StonecutterScreen");
         }
         StonecutterScreenHandler handler = screen.getScreenHandler();
         CuttingRecipeDisplay.Grouping<?> recipes = handler.getAvailableRecipes();
         if (recipes.isEmpty()) {
            throw new AssertionError(
               "clickRecipeButton: no available recipes (input slot empty?)");
         }
         if (recipeIndex >= recipes.entries().size()) {
            throw new AssertionError(
               "clickRecipeButton: recipeIndex " + recipeIndex
                  + " out of range, available=" + recipes.entries().size());
         }
         OneClickCraftingClient.getInstance().stonecuttingHandler.recipeClicked(
            screen,
            new Click(0, 0, new MouseInput(mouseButton, 0)),
            recipeIndex);
         mc.interactionManager.clickButton(handler.syncId, recipeIndex);
      });
   }

   /**
    * Returns the result {@link Item} that recipe {@code recipeIndex} in the
    * currently open stonecutter would produce.
    */
   protected Item getRecipeResult(int recipeIndex) {
      return context.computeOnClient(mc -> {
         if (!(mc.currentScreen instanceof StonecutterScreen screen)) {
            throw new AssertionError("getRecipeResult: not in a StonecutterScreen");
         }
         StonecutterScreenHandler handler = screen.getScreenHandler();
         CuttingRecipeDisplay.Grouping<?> recipes = handler.getAvailableRecipes();
         if (recipes.isEmpty()) {
            throw new AssertionError("getRecipeResult: no available recipes");
         }
         var entry = recipes.entries().get(recipeIndex);
         ItemStack stack = ((SlotDisplay.StackSlotDisplay) entry.recipe().optionDisplay()).stack();
         return stack.getItem();
      });
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

   protected void assertInventoryExact(Item item, int count) {
      HashMap<Item, Integer> expected = new HashMap<>();
      expected.put(item, count);
      assertInventoryExact(expected);
   }

   protected void assertInventoryExact(Item item1, int count1, Item item2, int count2) {
      HashMap<Item, Integer> expected = new HashMap<>();
      expected.put(item1, count1);
      expected.put(item2, count2);
      assertInventoryExact(expected);
   }

   /**
    * Asserts that the player's inventory contains exactly the items in {@code items}
    * and nothing else. Waits up to 20 ticks for the state to match, then throws
    * {@link AssertionError} with a descriptive message if it does not.
    */
   protected void assertInventoryExact(Map<Item, Integer> items) {
      try {
         context.waitFor(mc -> {
            assert mc.player != null;
            Map<Item, Integer> counter = new HashMap<>(items);
            var inv = mc.player.getInventory();
            for (int i = 0; i < inv.size(); i++) {
               ItemStack stack = inv.getStack(i);
               counter.compute(stack.getItem(),
                  (k, c) -> (c == null ? 0 : c) - stack.getCount());
            }
            return counter.values().stream().mapToInt(x -> x).allMatch(x -> x == 0);
         }, 20);
      } catch (AssertionError timeout) {
         Map<Item, Integer> actual = snapshotInventory();
         throw new AssertionError(
            "Expected inventory " + formatItemMap(items)
               + " but found " + formatItemMap(actual));
      }
   }

   /**
    * Asserts the player's inventory contains exactly {@code expectedCount} of
    * {@code item}, ignoring all other items.
    */
   protected void assertInventoryCount(Item item, int expectedCount) {
      int actual = context.computeOnClient(mc -> {
         if (mc.player == null) return 0;
         var inv = mc.player.getInventory();
         int total = 0;
         for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isOf(item)) total += stack.getCount();
         }
         return total;
      });
      if (actual != expectedCount) {
         throw new AssertionError(
            "Expected exactly %d of %s in inventory, found %d"
               .formatted(expectedCount, Registries.ITEM.getId(item), actual));
      }
   }

   /**
    * Asserts the player's inventory contains at least {@code minCount} of
    * {@code item}, ignoring all other items.
    */
   protected void assertInventoryAtLeast(Item item, int minCount) {
      int actual = context.computeOnClient(mc -> {
         if (mc.player == null) return 0;
         var inv = mc.player.getInventory();
         int total = 0;
         for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isOf(item)) total += stack.getCount();
         }
         return total;
      });
      if (actual < minCount) {
         throw new AssertionError(
            "Expected >= %d of %s in inventory, found %d"
               .formatted(minCount, Registries.ITEM.getId(item), actual));
      }
   }

   // -------------------------------------------------------------------------
   // Ground item assertions
   // -------------------------------------------------------------------------

   /** Asserts there is at least one {@link ItemEntity} carrying {@code item} near the player. */
   protected void assertItemOnGround(Item item) {
      try {
         context.waitFor(mc -> {
            if (mc.player == null || mc.world == null) return false;
            Box box = mc.player.getBoundingBox().expand(16.0);
            return !mc.world
               .getEntitiesByClass(ItemEntity.class, box, e -> e.getStack().isOf(item))
               .isEmpty();
         }, 20);
      } catch (AssertionError timeout) {
         throw new AssertionError(
            "Expected ItemEntity with " + Registries.ITEM.getId(item) + " near player, found none");
      }
   }

   /** Asserts there are NO {@link ItemEntity}s carrying {@code item} near the player. */
   protected void assertNoItemOnGround(Item item) {
      boolean found = context.computeOnClient(mc -> {
         if (mc.player == null || mc.world == null) return false;
         Box box = mc.player.getBoundingBox().expand(16.0);
         return !mc.world
            .getEntitiesByClass(ItemEntity.class, box, e -> e.getStack().isOf(item))
            .isEmpty();
      });
      if (found) {
         throw new AssertionError(
            "Expected no ItemEntity with " + Registries.ITEM.getId(item) + " near player, but found one");
      }
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

   // -------------------------------------------------------------------------
   // Misc helpers
   // -------------------------------------------------------------------------

   protected int stacks(int stacks) {
      return stacks * 64;
   }

   protected IngredientGiver recipe(Item item) {
      return recipe(item.toString());
   }

   /**
    * Looks up the recipe with id {@code "minecraft:<recipeId>"}, grants it to the
    * player (clearing all other known recipes first), and returns an
    * {@link IngredientGiver} that gives the exact number of ingredients needed to
    * produce {@code targetResultCount} results and asserts they are in the
    * player's inventory.
    *
    * @param recipeId the path portion of the recipe id, e.g. {@code "oak_planks"}
    */
   protected IngredientGiver recipe(String recipeId) {
      CraftingRecipe recipe = world.getServer().computeOnServer(server -> {
         RecipeEntry<?> recipeEntry = server.getRecipeManager()
            .get(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(recipeId))).orElseThrow();
         Recipe<?> value = recipeEntry.value();
         assert value instanceof CraftingRecipe;
         return ((CraftingRecipe) value);
      });
      world.getServer().runCommand("recipe take @a *");
      world.getServer().runCommand("recipe give @a minecraft:" + recipeId);
      int outputCount;
      List<Ingredient> ingredients;
      if (recipe instanceof ShapelessRecipe shapelessRecipe) {
         ItemStack result = shapelessRecipe.result;
         outputCount = result.getCount();
         ingredients = shapelessRecipe.ingredients;
      } else if (recipe instanceof ShapedRecipe shapedRecipe) {
         ItemStack result = shapedRecipe.result;
         outputCount = result.getCount();
         ingredients = shapedRecipe.raw.getIngredients().stream().filter(Optional::isPresent).map(Optional::get).toList();
      } else {
         throw new AssertionError(
            "Expected recipe minecraft:%s to be of type ShapelessRecipe or ShapedRecipe, found %s"
               .formatted(recipeId, recipe.getClass().getName()));
      }

      return x -> {
         int inputCount = x / outputCount;
         Map<Item, Integer> expected = new HashMap<>();
         for (Ingredient ingredient : ingredients) {
            Item input = ingredient.entries.stream().findFirst().orElseThrow().value();
            world.getServer().runCommand("give @a " + input + " " + inputCount);
            expected.compute(input, (i, count) -> (count == null ? 0 : count) + inputCount);
         }
         assertInventoryExact(expected);
      };
   }

   // -------------------------------------------------------------------------
   // Private helpers
   // -------------------------------------------------------------------------

   private Map<Item, Integer> snapshotInventory() {
      return context.computeOnClient(mc -> {
         Map<Item, Integer> counts = new LinkedHashMap<>();
         if (mc.player == null) return counts;
         var inv = mc.player.getInventory();
         for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty()) counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
         }
         return counts;
      });
   }

   private static String formatItemMap(Map<Item, Integer> map) {
      if (map.isEmpty()) return "{}";
      StringBuilder sb = new StringBuilder("{");
      map.forEach((item, count) ->
         sb.append(Registries.ITEM.getId(item)).append("=").append(count).append(", "));
      sb.setLength(sb.length() - 2);
      return sb.append("}").toString();
   }
}
