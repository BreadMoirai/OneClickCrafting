package com.github.breadmoirai.oneclickcrafting.testmod;

import com.github.breadmoirai.oneclickcrafting.config.OneClickCraftingConfig;
import com.github.breadmoirai.oneclickcrafting.testmod.context.CraftContext;
import com.github.breadmoirai.oneclickcrafting.testmod.context.CraftingTableContext;
import com.github.breadmoirai.oneclickcrafting.testmod.context.InventoryContext;
import com.github.breadmoirai.oneclickcrafting.testmod.context.StonecutterContext;
import com.github.breadmoirai.oneclickcrafting.testmod.recipebookhelper.RecipeBookHelper;
import com.github.breadmoirai.oneclickcrafting.testmod.suite.TestSuite;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class OneClickTests extends TestSuite {

   protected RecipeBookHelper recipeBook;
   protected ConfigHelper config;
   protected final List<CraftContext> contexts;
   /** Crafting-only contexts (recipe-book inventory + crafting table), excluding stonecutter. */
   protected final List<CraftContext> craftingContexts;

   // Config label constants — exposed here so suite subclasses can reference them directly
   protected static final String LABEL_STONECUTTER = ConfigHelper.LABEL_STONECUTTER;
   protected static final String LABEL_LEFT_CLICK  = ConfigHelper.LABEL_LEFT_CLICK;
   protected static final String LABEL_RIGHT_CLICK = ConfigHelper.LABEL_RIGHT_CLICK;
   protected static final String LABEL_ALWAYS_ON   = ConfigHelper.LABEL_ALWAYS_ON;
   protected static final String LABEL_ALT_HOLD    = ConfigHelper.LABEL_ALT_HOLD;
   protected static final String LABEL_CTRL_HOLD   = ConfigHelper.LABEL_CTRL_HOLD;
   protected static final String LABEL_DROP_ENABLE = ConfigHelper.LABEL_DROP_ENABLE;

   protected OneClickTests(ClientGameTestContext context, TestSingleplayerContext world) {
      super(context, world);
      this.recipeBook = RecipeBookHelper.create(context);
      this.config = new ConfigHelper(context);
      context.runOnClient(mc -> OneClickCraftingConfig.getInstance().setDebugLogging(true));
      InventoryContext inv = new InventoryContext(context, world, "minecraft:oak_log", 1, "minecraft:oak_planks", 4);
      CraftingTableContext table = new CraftingTableContext(context, world, "minecraft:oak_log", 1, "minecraft:oak_planks", 4);
      StonecutterContext stone = StonecutterContext.create(context, world, "minecraft:cobblestone", 1, "minecraft:cobblestone_slab", 2);
      this.contexts = List.of(inv, table, stone);
      this.craftingContexts = List.of(inv, table);
   }

}
