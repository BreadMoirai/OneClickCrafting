# OneClickCrafting Game Tests

Integration tests using `fabric-client-gametest-api-v1`. Every test drives the game exclusively through simulated user input — no calls into the mod's own API. The mod's behaviour is exercised only as a side-effect of UI interactions.

---

## Infrastructure

### TestKeyState (utility class)
A static `Map<Integer, Boolean>` consulted by `InputUtilTestMixin` before the real GLFW call. Tests call `TestKeyState.press(GLFW_KEY_*)` / `TestKeyState.release(GLFW_KEY_*)` / `TestKeyState.clear()` to control what `InputUtil.isKeyPressed` returns for any key at any moment.

### InputUtilTestMixin
`@Inject` at HEAD of `InputUtil.isKeyPressed`, cancellable. If `TestKeyState` has an override for the queried keycode, the mixin returns that value and cancels the real GLFW call. Lives only in the `testmod` source set.

### TestHelper (utility class)
Shared setup and navigation helpers. All helpers that interact with the game go through commands or widget-tree traversal — never through the mod's own classes.

| Method | Description |
|---|---|
| `runCommand(context, String)` | Executes a cheat command on the integrated server (e.g. `/give`, `/setblock`, `/recipe`) |
| `openInventoryScreen(context)` | Simulates pressing the inventory key; waits until `InventoryScreen` is active |
| `openStonecutterScreen(context)` | `/setblock ~ ~-1 ~ stonecutter` then right-click the block; waits until `StonecutterScreen` is active |
| `openRecipeBook(context)` | Finds and clicks the recipe-book toggle button in the current handled screen |
| `findRecipeButton(context, Item)` | Walks `Screen.children()` → `RecipeBookWidget` → `RecipeBookResults` → `AnimatedResultButton` until it finds the button whose display result matches the target item. Returns the button's bounding box centre. |
| `findStonecutterRecipeButton(context, Item)` | Walks `StonecutterScreen.children()` to find the recipe-list entry whose output matches the target item. Returns its bounding box centre. |
| `openConfigViaModMenu(context)` | Sets the current screen to `ModsScreen`; finds the One Click Crafting row; clicks its configure button; waits until the YACL screen is active |
| `findYaclToggle(context, String optionLabel)` | Walks the YACL screen's widget tree to find the option entry matching `optionLabel`; returns its toggle widget's bounding box centre |
| `closeScreen(context)` | Simulates pressing Escape; waits until the in-game HUD is active |
| `assertInventoryContains(context, Item, int minCount)` | Checks the player's inventory via `ClientPlayerEntity.getInventory()` |
| `assertItemOnGround(context, Item)` | Checks `ClientWorld.getEntitiesByClass(ItemEntity.class, ...)` near the player |

---

## Simulated Input Conventions

- **Mouse click:** `context.clickMouseButton(x, y, button)` where `button` is `0` (left) or `1` (right). Coordinates come from the widget's bounding box centre via `TestHelper.find*`.
- **Modifier keys:** `TestKeyState.press(GLFW_KEY_LEFT_SHIFT)` (etc.) before clicking; `TestKeyState.clear()` in teardown.
- **Key events:** `context.pressKey(keyCode)` for keys that must go through `ScreenKeyboardEvents` (e.g. the repeat-last keybind).
- **Tick advancement:** `context.waitTicks(n)` to let the integrated server process packets and the client re-render.

---

## Test Cases

### Suite 1 — Config Persistence

All config tests navigate through ModMenu → YACL screen. No config class methods are called directly.

#### `testConfigDefaultsShownInUi`
- **Setup:** Delete `config/oneclickcrafting.json` if present; restart the mod initialiser via screen open
- **Action:** `TestHelper.openConfigViaModMenu(context)`
- **Assert:** Walk the YACL screen's widget tree and verify every toggle's visual state matches the documented defaults:
  `Enable Stonecutter=ON`, `Enable Left Click=OFF`, `Enable Right Click=ON`,
  `Always On=ON`, `Alt Hold=ON`, `Ctrl Hold=ON`, `Drop Enable=ON`

#### `testConfigPersistenceViaUi`
- **Setup:** `TestHelper.openConfigViaModMenu(context)`
- **Action:**
  1. Find "Enable Left Click" toggle via `findYaclToggle`; click it (OFF → ON)
  2. Find "Always On" toggle; click it (ON → OFF)
  3. Simulate pressing Escape to close and save (YACL saves on screen close)
  4. `TestHelper.openConfigViaModMenu(context)` again
- **Assert:**
  - "Enable Left Click" toggle is now ON
  - "Always On" toggle is now OFF
  - All other toggles unchanged from defaults

---

### Suite 2 — Basic Crafting (left-click)

#### `testLeftClickDisabledNoAction`
- **Setup:**
  1. `TestHelper.openConfigViaModMenu` → find "Enable Left Click" (already OFF by default) → close
  2. `runCommand("give @s oak_log 8")`, `runCommand("recipe give @s *")`
  3. `TestHelper.openInventoryScreen`, `TestHelper.openRecipeBook`
- **Action:** `context.clickMouseButton(findRecipeButton(context, OAK_PLANKS), 0)`; `waitTicks(10)`
- **Assert:** `assertInventoryContains(OAK_PLANKS, 0)` — mod did not move the result

#### `testLeftClickEnabledMovesToInventory`
- **Setup:**
  1. `openConfigViaModMenu` → click "Enable Left Click" toggle (OFF → ON) → close
  2. `runCommand("give @s oak_log 8")`, `runCommand("recipe give @s *")`
  3. `openInventoryScreen`, `openRecipeBook`
- **Action:** `clickMouseButton(findRecipeButton(OAK_PLANKS), 0)`; `waitTicks(10)`
- **Assert:** `assertInventoryContains(OAK_PLANKS, 4)` (one craft's worth)

---

### Suite 3 — Basic Crafting (right-click)

#### `testRightClickEnabledMovesToInventory`
- **Setup:** Default config (`enableRightClick=true`); give 4 oak logs; open crafting screen + recipe book
- **Action:** `clickMouseButton(findRecipeButton(OAK_PLANKS), 1)`; `waitTicks(10)`
- **Assert:** `assertInventoryContains(OAK_PLANKS, 4)`

#### `testRightClickDisabledNoAction`
- **Setup:**
  1. `openConfigViaModMenu` → click "Enable Right Click" toggle (ON → OFF) → close
  2. Give 4 oak logs; open crafting + recipe book
- **Action:** `clickMouseButton(findRecipeButton(OAK_PLANKS), 1)`; `waitTicks(10)`
- **Assert:** `assertInventoryContains(OAK_PLANKS, 0)`

---

### Suite 4 — Shift-Click (full stack)

#### `testShiftClickCraftsFullStack`
- **Setup:** Enable left click via config UI; give 64 oak logs; open crafting + recipe book
- **Action:**
  1. `TestKeyState.press(GLFW_KEY_LEFT_SHIFT)`
  2. `clickMouseButton(findRecipeButton(OAK_PLANKS), 0)`; `waitTicks(20)`
- **Teardown:** `TestKeyState.clear()`
- **Assert:** `assertInventoryContains(OAK_PLANKS, 64)` (64 logs × 4 planks, capped at 64 per slot)

#### `testShiftClickPartialIngredients`
- **Setup:** Enable left click via config UI; give exactly 3 oak logs
- **Action:** Shift + left-click oak planks recipe; `waitTicks(10)`
- **Assert:** `assertInventoryContains(OAK_PLANKS, 12)`; no crash/hang; inventory log count = 0

---

### Suite 5 — Drop Key

#### `testDropKeyDropsResult`
- **Setup:** Default config; give 4 oak logs; open crafting + recipe book
- **Action:**
  1. `TestKeyState.press(GLFW_KEY_Q)` (default drop key)
  2. `clickMouseButton(findRecipeButton(OAK_PLANKS), 1)`; `waitTicks(10)`
- **Teardown:** `TestKeyState.clear()`
- **Assert:** `assertItemOnGround(OAK_PLANKS)`; `assertInventoryContains(OAK_PLANKS, 0)`

#### `testDropKeyDisabledMovesToInventory`
- **Setup:**
  1. `openConfigViaModMenu` → click "Drop Enable" (ON → OFF) → close
  2. Give 4 oak logs; open crafting + recipe book
- **Action:** `TestKeyState.press(GLFW_KEY_Q)`, right-click recipe; `waitTicks(10)`
- **Teardown:** `TestKeyState.clear()`
- **Assert:** `assertInventoryContains(OAK_PLANKS, 4)`; no items on ground

#### `testShiftDropDropsFullStack`
- **Setup:** Default config; give 64 oak logs; open crafting + recipe book
- **Action:**
  1. `TestKeyState.press(GLFW_KEY_LEFT_SHIFT)`, `TestKeyState.press(GLFW_KEY_Q)`
  2. `clickMouseButton(findRecipeButton(OAK_PLANKS), 1)`; `waitTicks(30)`
- **Teardown:** `TestKeyState.clear()`
- **Assert:** Sum of dropped `ItemEntity` stacks near player ≥ 64 oak planks; `assertInventoryContains(OAK_PLANKS, 0)`

---

### Suite 6 — Activation Modes

Each test opens the config screen first to put the mod in the required mode, then exercises the activation behaviour through a recipe click.

#### `testAlwaysOnActiveWithNoModifiers`
- **Config state:** defaults (`alwaysOn=true`)
- **Action:** Right-click recipe; `waitTicks(10)`
- **Assert:** `assertInventoryContains(target item, >0)`

#### `testCtrlHoldInvertsWhenAlwaysOn`
- **Config state:** defaults (`alwaysOn=true`, `ctrlHold=true`)
- **Action:** `TestKeyState.press(GLFW_KEY_LEFT_CONTROL)`; right-click recipe; `waitTicks(10)`
- **Teardown:** `TestKeyState.clear()`
- **Assert:** `assertInventoryContains(target item, 0)` — mod suppressed

#### `testAltHoldInvertsWhenAlwaysOn`
- **Config state:** defaults (`alwaysOn=true`, `altHold=true`)
- **Action:** `TestKeyState.press(GLFW_KEY_LEFT_ALT)`; right-click recipe; `waitTicks(10)`
- **Teardown:** `TestKeyState.clear()`
- **Assert:** `assertInventoryContains(target item, 0)`

#### `testCtrlHoldActivatesWhenAlwaysOff`
- **Config state:** `openConfigViaModMenu` → click "Always On" (ON → OFF) → close
- **Action:** `TestKeyState.press(GLFW_KEY_LEFT_CONTROL)`; right-click recipe; `waitTicks(10)`
- **Teardown:** `TestKeyState.clear()`
- **Assert:** `assertInventoryContains(target item, >0)` — ctrl re-enabled the mod

#### `testCtrlHoldIgnoredWhenCtrlHoldDisabled`
- **Config state:** `openConfigViaModMenu` → click "Ctrl Hold" (ON → OFF) → close (`alwaysOn` remains true)
- **Action:** `TestKeyState.press(GLFW_KEY_LEFT_CONTROL)`; right-click recipe; `waitTicks(10)`
- **Teardown:** `TestKeyState.clear()`
- **Assert:** `assertInventoryContains(target item, >0)` — ctrl ignored, mod still active

---

### Suite 7 — Repeat Last

#### `testRepeatLastReCrafts`
- **Setup:** Give 16 oak logs; open crafting + recipe book; right-click oak planks recipe once; `waitTicks(10)`
- **Action:** `context.pressKey(repeatLastKey keycode)`; `waitTicks(10)`
- **Assert:** `assertInventoryContains(OAK_PLANKS, ≥8)` — second batch crafted

#### `testRepeatLastWaitsForPendingOperation`
- **Covers:** Issue #24
- **Setup:** Give 8 oak logs; open crafting + recipe book
- **Action:**
  1. Right-click recipe (triggers craft; server packet in flight)
  2. Immediately `context.pressKey(repeatLastKey)` on the same tick before packet arrives
  3. `waitTicks(1)` — packet arrives, first craft resolves
  4. `waitTicks(10)` — repeat fires after pending clears
- **Assert:** `assertInventoryContains(OAK_PLANKS, ≥8)` — both crafts completed in sequence, not doubled or skipped

---

### Suite 8 — Stonecutter Basic

#### `testStonecutterDisabledNoAction`
- **Setup:**
  1. `openConfigViaModMenu` → click "Enable Stonecutter" (ON → OFF) → close
  2. `runCommand("give @s stone 8")`; `openStonecutterScreen`
- **Action:** `clickMouseButton(findStonecutterRecipeButton(STONE_SLAB), 0)`; `waitTicks(10)`
- **Assert:** `assertInventoryContains(STONE_SLAB, 0)` — output not moved automatically

#### `testStonecutterBasicRecipeMovesToInventory`
- **Setup:** Default config; `runCommand("give @s stone 8")`; `openStonecutterScreen`
- **Action:** `clickMouseButton(findStonecutterRecipeButton(STONE_SLAB), 0)`; `waitTicks(10)`
- **Assert:** `assertInventoryContains(STONE_SLAB, ≥2)`

#### `testStonecutterRefillsInputFromInventory`
- **Setup:** Default config; give 16 stone; open stonecutter; click stone slab recipe; `waitTicks(20)`
- **Assert:** Stonecutter input slot is non-empty (refilled from inventory); total stone slabs in inventory accounts for multiple crafts

#### `testStonecutterRightClickSingleOutput`
- **Setup:** Default config; give 8 stone; open stonecutter
- **Action:** `clickMouseButton(findStonecutterRecipeButton(STONE_SLAB), 1)`; `waitTicks(10)`
- **Assert:** `assertInventoryContains(STONE_SLAB, 2)` — exactly one craft's worth (not a full stack run)

---

### Suite 9 — Stonecutter Advanced

#### `testStonecutterShiftClickFullStack`
- **Setup:** Default config; give 64 stone; open stonecutter
- **Action:**
  1. `TestKeyState.press(GLFW_KEY_LEFT_SHIFT)`
  2. `clickMouseButton(findStonecutterRecipeButton(STONE_SLAB), 0)`; `waitTicks(60)`
- **Teardown:** `TestKeyState.clear()`
- **Assert:** `assertInventoryContains(STONE_SLAB, 128)`; stonecutter input empty; player has no stone left

#### `testStonecutterShiftDropPattern`
- **Covers:** Latest commit — shift+left-click triggers shift+q+right-click output pattern
- **Setup:** Default config; give 8 stone; open stonecutter
- **Action:**
  1. `TestKeyState.press(GLFW_KEY_LEFT_SHIFT)`, `TestKeyState.press(GLFW_KEY_Q)`
  2. `clickMouseButton(findStonecutterRecipeButton(STONE_SLAB), 0)`; `waitTicks(20)`
- **Teardown:** `TestKeyState.clear()`
- **Assert:** `assertItemOnGround(STONE_SLAB)`; `assertInventoryContains(STONE_SLAB, 0)`

#### `testStonecutterInputExhaustedStops`
- **Setup:** Default config; give exactly 1 stone; open stonecutter; click stone slab recipe; `waitTicks(20)`
- **Assert:** Stonecutter input slot empty; no crash; no infinite packet loop (verify by checking no more than 1 result entity in world)

---

### Suite 10 — Edge Cases

#### `testIssue25NoServerPacketTriggers`
- **Covers:** Issue #25 — result slot pre-filled before `select()` fires
- **Setup:**
  1. Give player ingredients and open crafting screen
  2. `runCommand("recipe give @s *")`
  3. Click the recipe in the recipe book once and wait for the grid to fill — but do NOT wait for the result to be moved (close and reopen the screen to reset handler state while leaving the grid filled)
  4. Reopen the crafting screen (grid is already in the correct state)
- **Action:** Right-click the same recipe again; `waitTicks(10)`
- **Assert:** `assertInventoryContains(target item, >0)` — the fallback path detected the pre-filled result slot and acted without a server packet

#### `testScreenCloseResetsState`
- **Setup:** Give items; open crafting + recipe book; right-click recipe; `waitTicks(2)` (craft in flight)
- **Action:** Simulate pressing Escape (`closeScreen`); `waitTicks(2)`; reopen inventory
- **Assert:** A subsequent recipe click with no modifier keys held produces a normal result — verifies no stale `isDropping`, `isPending`, or `lastButton` state leaked across screens

#### `testNoActionOnEmptyResultSlot`
- **Setup:** Open crafting screen with no items in inventory; open recipe book
- **Action:** Right-click a recipe whose ingredients are absent; `waitTicks(10)`
- **Assert:** `assertInventoryContains(any crafted item, 0)`; no exceptions

#### `testAlternativeRecipeSelectionRightClick`
- **Setup:** Give player ingredients for a recipe with multiple alternatives (e.g. stone bricks from stone); open crafting + recipe book
- **Action:** Right-click the recipe button that shows alternatives to open the alternatives widget; right-click one of the alternatives; `waitTicks(10)`
- **Assert:** `assertInventoryContains(target item, >0)` — verifies the `RecipeAlternativesWidgetMixin` right-click path ends in the same automated move

---

## File Layout

```
src/test/
├── resources/
│   └── fabric.mod.json
└── java/com/github/breadmoirai/oneclickcrafting/testmod/
    ├── OneClickCraftingGameTests.java      -- entry point, registers all suites
    ├── TestHelper.java                     -- navigation and assertion helpers
    └── suite/
        └── ConfigSuite.java               -- Suite 1: config persistence tests
```

## Running Tests

```
./gradlew runTestClient
```

Tests run headlessly on startup and exit with code 0 (pass) or 1 (fail).
