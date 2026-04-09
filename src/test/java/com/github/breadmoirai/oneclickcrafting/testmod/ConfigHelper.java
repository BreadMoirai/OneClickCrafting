package com.github.breadmoirai.oneclickcrafting.testmod;

import com.terraformersmc.modmenu.gui.ModsScreen;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Instance helper for opening and interacting with the mod's YACL config screen
 * via ModMenu. Instantiated once per test class and stored as {@code config}.
 */
@SuppressWarnings("UnstableApiUsage")
public class ConfigHelper {

   // YACL option labels (translated strings from en_us.json)
   public static final String LABEL_STONECUTTER = "Enable Stonecutter";
   public static final String LABEL_LEFT_CLICK = "Left Click";
   public static final String LABEL_RIGHT_CLICK = "Right Click";
   public static final String LABEL_ALWAYS_ON = "Always On";
   public static final String LABEL_ALT_HOLD = "Alt Toggle (Hold)";
   public static final String LABEL_CTRL_HOLD = "Ctrl Toggle (Hold)";
   public static final String LABEL_DROP_ENABLE = "Enable Drop Key on Craft";

   private final ClientGameTestContext context;

   public ConfigHelper(ClientGameTestContext context) {
      this.context = context;
   }

   // -------------------------------------------------------------------------
   // Navigation
   // -------------------------------------------------------------------------

   public void openConfigViaModMenu() {
      context.setScreen(() -> new ModsScreen(null));
      context.waitForScreen(ModsScreen.class);

      int[] entryCenter = context.computeOnClient(mc -> {
         ModsScreen screen = (ModsScreen) mc.screen;
         try {
            Field modListField = ModsScreen.class.getDeclaredField("modList");
            modListField.setAccessible(true);
            Object modList = modListField.get(screen);
            @SuppressWarnings("unchecked")
            List<Object> children = (List<Object>) modList.getClass()
               .getMethod("children").invoke(modList);

            for (int i = 0; i < children.size(); i++) {
               Object entry = children.get(i);
               Object mod = entry.getClass().getMethod("getMod").invoke(entry);
               String id = (String) mod.getClass().getMethod("getId").invoke(mod);
               if ("one-click-crafting".equals(id)) {
                  int rowTop = (int) modList.getClass()
                     .getMethod("getRowTop", int.class).invoke(modList, i);
                  int rowHeight = (int) entry.getClass()
                     .getMethod("getHeight").invoke(entry);
                  int listX = (int) modList.getClass()
                     .getMethod("getX").invoke(modList);
                  int listWidth = (int) modList.getClass()
                     .getMethod("getWidth").invoke(modList);
                  double scale = mc.getWindow().getGuiScale();
                  int guiX = listX + listWidth / 2;
                  int guiY = rowTop + rowHeight / 2;
                  return new int[]{(int) (guiX * scale), (int) (guiY * scale)};
               }
            }
            throw new AssertionError("Mod 'one-click-crafting' not found in ModsScreen list");
         } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
         }
      });

      context.getInput().setCursorPos(entryCenter[0], entryCenter[1]);
      context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
      context.waitTick();

      context.runOnClient(mc -> {
         ModsScreen screen = (ModsScreen) mc.screen;
         try {
            Field configField = ModsScreen.class.getDeclaredField("configureButton");
            configField.setAccessible(true);
            AbstractWidget btn = (AbstractWidget) configField.get(screen);
            if (btn == null || !btn.active) {
               throw new AssertionError(
                  "Configure button is null or inactive for one-click-crafting");
            }
            double cx = btn.getX() + btn.getWidth() / 2.0;
            double cy = btn.getY() + btn.getHeight() / 2.0;
            btn.mouseClicked(new MouseButtonEvent(cx, cy, new MouseButtonInfo(GLFW.GLFW_MOUSE_BUTTON_LEFT, 0)), false);
         } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
         }
      });
      context.waitForScreen(YACLScreen.class);
   }

   /**
    * Saves any pending YACL changes and closes the screen.
    *
    * <p>In YACL 3.8.1+, "Save Changes" persists options but keeps the screen open;
    * "Done" closes it. Both clicks are always issued.
    */
   public void saveAndCloseYacl() {
      context.tryClickScreenButton("yacl.gui.save");
      context.clickScreenButton("gui.done");
      context.waitFor(mc -> !YACLScreen.class.isInstance(mc.screen));
   }

   /**
    * Closes ModsScreen by pressing Escape.
    */
   public void closeModsScreen() {
      context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
      context.waitFor(mc -> mc.screen == null || mc.screen instanceof TitleScreen);
   }

   // -------------------------------------------------------------------------
   // YACL toggle helpers  (must be called while YACLScreen is open)
   // -------------------------------------------------------------------------

   public boolean getYaclToggleState(String label) {
      return context.computeOnClient(mc -> {
         YACLScreen screen = (YACLScreen) mc.screen;
         OptionListWidget list = findOptionListWidget(screen);
         OptionListWidget.OptionEntry entry = findOptionEntry(list, label);
         @SuppressWarnings("unchecked")
         Option<Boolean> opt = (Option<Boolean>) entry.option;
         return opt.pendingValue();
      });
   }

   public void clickYaclToggle(String label) {
      context.runOnClient(mc -> {
         YACLScreen screen = (YACLScreen) mc.screen;
         OptionListWidget list = findOptionListWidget(screen);
         OptionListWidget.OptionEntry entry = findOptionEntry(list, label);
         var dim = entry.widget.getDimension();
         entry.widget.mouseClicked(new MouseButtonEvent(dim.centerX(), dim.centerY(), new MouseButtonInfo(GLFW.GLFW_MOUSE_BUTTON_LEFT, 0)), false);
      });
      context.waitTick();
   }

   public void setYaclToggle(String label, boolean desired) {
      if (getYaclToggleState(label) != desired) {
         clickYaclToggle(label);
      }
   }

   // -------------------------------------------------------------------------
   // Private widget-tree helpers
   // -------------------------------------------------------------------------

   private static OptionListWidget findOptionListWidget(YACLScreen screen) {
      for (GuiEventListener child : screen.children()) {
         if (child instanceof OptionListWidget optList) {
            return optList;
         }
      }
      throw new AssertionError("OptionListWidget not found among YACLScreen children");
   }

   private static OptionListWidget.OptionEntry findOptionEntry(
      OptionListWidget optList, String label) {
      for (OptionListWidget.Entry entry : optList.children()) {
         if (entry instanceof OptionListWidget.OptionEntry optEntry) {
            if (optEntry.option.name().getString().equals(label)) {
               return optEntry;
            }
         }
      }
      throw new AssertionError("YACL option not found for label: '" + label + "'");
   }
}
