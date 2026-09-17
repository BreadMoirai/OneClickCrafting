extra["swaps"] = mapOf(
    // MC 26.3 swapped GLFW for SDL: org.lwjgl.glfw.GLFW is gone from the client classpath and
    // InputConstants.Type.KEYSYM/SCANCODE became a single KEYBOARD constant. Key/mouse codes and
    // InputConstants.UNKNOWN are unchanged, so only the enum constant needs a swap.
    // The client-gametest API jumped to 6.x for 26.3 and moved the chunk-load/level accessors off
    // TestSingleplayerContext onto a new TestServerConnection (test code only):
    //   TestSingleplayerContext.getClientLevel() -> getConnection()
    // Entries are ordered NEWEST FIRST so switching down unwinds a twice-renamed symbol
    // (getClientWorld -> getClientLevel -> getConnection) newest-rename-first.
    "26.3" to mapOf(
        "InputConstants.Type.KEYSYM" to "InputConstants.Type.KEYBOARD",
        "getClientLevel" to "getConnection",
    ),
    "26.2" to mapOf(
        "Minecraft.getInstance().screen" to "Minecraft.getInstance().gui.screen()",
        "minecraft.screen" to "minecraft.gui.screen()",
        "client.screen instanceof" to "client.gui.screen() instanceof",
        "mc.screen" to "mc.gui.screen()",
    ),
    "26.1" to mapOf(
        "net.minecraft.client.resources.sounds" to "net.minecraft.client.sounds",
        "net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper" to "net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper",
        "KeyBindingHelper.registerKeyBinding" to "KeyMappingHelper.registerKeyMapping",
        "getClientWorld" to "getClientLevel",
        "ClickType" to "ContainerInput",
        "getHoverName()" to "getItemName()",

        "oneclickcrafting.input.v21_9" to "oneclickcrafting.input.v26_1",
        "oneclickcrafting.item.v21_9" to "oneclickcrafting.item.v26_1",
        "oneclickcrafting.recipebook.v21_9" to "oneclickcrafting.recipebook.v26_1",
        "oneclickcrafting.stonecutter.v21_2" to "oneclickcrafting.stonecutter.v26_1",

        "oneclickcrafting.inventory.v21_2" to "oneclickcrafting.inventory.v26_1",
        "oneclickcrafting.operation.v21_2" to "oneclickcrafting.operation.v26_1",

        "oneclickcrafting.testmod.context.v21_9" to "oneclickcrafting.testmod.context.v26_1",
        "oneclickcrafting.testmod.recipebookhelper.v21_9" to "oneclickcrafting.testmod.recipebookhelper.v26_1",
        "oneclickcrafting.testmod.inputhelper.v21_11" to "oneclickcrafting.testmod.inputhelper.v26_1",
    ),
    "1.21.11" to mapOf(
        "ResourceLocation" to "Identifier",

        "oneclickcrafting.testmod.inputhelper.v21_9" to "oneclickcrafting.testmod.inputhelper.v21_11",
    ),
    "1.21.9" to mapOf(
        "oneclickcrafting.recipebook.v21_6" to "oneclickcrafting.recipebook.v21_9",

        "oneclickcrafting.testmod.context.v21_6" to "oneclickcrafting.testmod.context.v21_9",
        "oneclickcrafting.testmod.recipebookhelper.v21_6" to "oneclickcrafting.testmod.recipebookhelper.v21_9",
        "oneclickcrafting.testmod.inputhelper.v21_4" to "oneclickcrafting.testmod.inputhelper.v21_9",
    ),
    "1.21.2" to mapOf(
        "oneclickcrafting.stonecutter.v21_1" to "oneclickcrafting.stonecutter.v21_2",
        "oneclickcrafting.operation.v21_1" to "oneclickcrafting.operation.v21_2",
        "oneclickcrafting.recipebook.v21_1" to "oneclickcrafting.recipebook.v21_6",
    ),
    "1.21.1" to mapOf(
        "oneclickcrafting.recipebook.v20_1" to "oneclickcrafting.recipebook.v21_1",
        "oneclickcrafting.mixin.v20_1" to "oneclickcrafting.mixin.v21_1",
        "oneclickcrafting.stonecutter.v20_1" to "oneclickcrafting.stonecutter.v21_1",
        "oneclickcrafting.operation.v20_1" to "oneclickcrafting.operation.v21_1",
        "oneclickcrafting.inventory.v20_1" to "oneclickcrafting.inventory.v21_2",
    ),
)
