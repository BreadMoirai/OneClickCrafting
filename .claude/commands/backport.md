Add support for Minecraft version $ARGUMENTS to this Stonecutter mod project. Follow these steps in order:

## Step 0 — Pre-flight: Look up dependency versions

Before touching any files, look up the correct library versions for the new MC version:

```bash
# Fabric loader version
curl -s "https://meta.fabricmc.net/v2/versions/loader/$ARGUMENTS" | python3 -c "import json,sys; data=json.load(sys.stdin); print(data[0]['loader']['version'])"

# Fabric API version (pick the latest matching the game version)
curl -s "https://api.modrinth.com/v2/project/fabric-api/version?game_versions=%5B%22$ARGUMENTS%22%5D&loaders=%5B%22fabric%22%5D" | python3 -c "import json,sys; data=json.load(sys.stdin); print(data[0]['version_number'])"

# YACL version
curl -s "https://api.modrinth.com/v2/project/yacl/version?game_versions=%5B%22$ARGUMENTS%22%5D&loaders=%5B%22fabric%22%5D" | python3 -c "import json,sys; data=json.load(sys.stdin); [print(v['version_number']) for v in data[:3]]"

# ModMenu version
curl -s "https://api.modrinth.com/v2/project/modmenu/version?game_versions=%5B%22$ARGUMENTS%22%5D&loaders=%5B%22fabric%22%5D" | python3 -c "import json,sys; data=json.load(sys.stdin); [print(v['version_number']) for v in data[:3]]"
```

## Step 1 — Register the new version

Read `settings.gradle.kts`. Add the new version to the `stonecutter { create(rootProject) { ... } }` block using `versions("$ARGUMENTS")`. Place it alongside the existing `versions(...)` calls in a logical order (newest first or grouped by build script). Do not change `vcsVersion`.

## Step 2 — Create the version's gradle.properties

Create `versions/$ARGUMENTS/gradle.properties` with:
```properties
# Fabric Properties
minecraft_version=$ARGUMENTS
minecraft_version_range=>=$ARGUMENTS <{NEXT_VERSION}
loader_version={LOADER_VERSION}

# Dependencies
fabric_version={FABRIC_API_VERSION}
yacl_version={YACL_VERSION}
modmenu_version={MODMENU_VERSION}
```

Copy the format from an existing version (e.g. `versions/1.21.10/gradle.properties`).

## Step 3 — Create version-specific resource files

The new version needs resource directories for both main and test sources. Copy from the closest existing version:

```bash
# Main resources (accesswidener + mixins.json)
mkdir -p versions/$ARGUMENTS/src/main/resources
cp versions/{CLOSEST_VERSION}/src/main/resources/oneclickcrafting.accesswidener versions/$ARGUMENTS/src/main/resources/
cp versions/{CLOSEST_VERSION}/src/main/resources/one-click-crafting.mixins.json versions/$ARGUMENTS/src/main/resources/

# Test resources (test accesswidener + test mixins.json)
mkdir -p versions/$ARGUMENTS/src/test/resources
cp versions/{CLOSEST_VERSION}/src/test/resources/oneclickcraftingtestmod.accesswidener versions/$ARGUMENTS/src/test/resources/
cp versions/{CLOSEST_VERSION}/src/test/resources/oneclickcraftingtestmod.mixins.json versions/$ARGUMENTS/src/test/resources/
```

## Step 4 — Register transformUnnamedVars for the new version

Read `stonecutter.gradle.kts` and add the new version to the `afterEvaluate` block:

```kotlin
afterEvaluate {
    tasks.findByName("stonecutterSwitchTo$ARGUMENTS")?.finalizedBy("transformUnnamedVars")
    // ... existing entries ...
}
```

The `transformUnnamedVars` task replaces `_` (JDK 22 unnamed vars) with `unusedN` for MC versions that don't support JDK 22 features. Use `restoreUnnamedVars` only for the `26.1` version.

## Step 5 — Set active project to the new version

Run the Gradle task to switch the active Stonecutter version:
```
./gradlew "Set active project to $ARGUMENTS"
```

If you get an I/O error, first run `./gradlew --stop` then retry.

## Step 6 — Extend version conditionals optimistically

Before attempting to compile, find all stonecutter version conditions whose range includes the nearest existing version but not the new one, and extend them to include the new version. For example, when backporting to 1.21.8:

```bash
grep -rn ">=1.21.9 <=1.21.11" src/
```

For each match, widen the lower bound to include the new version:
- `//? >=1.21.9 <=1.21.11 {` → `//? >=$ARGUMENTS <=1.21.11 {`
- Inline conditions like `*///?} >=1.21.9 <=1.21.11 {` → `*///?} >=$ARGUMENTS <=1.21.11 {`
- Do this for files in versioned **main** package dirs only

**IMPORTANT — do NOT extend test code conditions below 1.21.4.** Test code in `src/test/` requires the Fabric client gametest API which only exists for MC >= 1.21.4. If the new version is below 1.21.4, skip `src/test/` entirely when extending conditions — the test srcDirs are already cleared in `build.gradle.kts` for those versions.

Also search for **exact-version conditions** targeting the nearest existing version — these also need widening:
```bash
grep -rn "//? {NEAREST_VERSION} {" src/
```
For example `//? 1.21.8 {` must become `//? >=$ARGUMENTS <=1.21.8 {` when backporting to $ARGUMENTS.

After changes, resync Stonecutter:
```
./gradlew "Set active project to $ARGUMENTS"
```

## Step 7 — Attempt a build

```
./gradlew :$ARGUMENTS:compileJava
```

For versions >= 1.21.4 that have the client gametest API, also run:
```
./gradlew :$ARGUMENTS:compileTestJava
```

For versions < 1.21.4, `compileTestJava` will show `NO-SOURCE` — this is correct and expected, skip it.

Collect all compiler errors. Do not fix anything yet — read and understand all errors first.

## Step 8 — Fix errors iteratively

Work through compile errors one category at a time:

### Package rename after extending lower bound

**Naming convention:** Versioned packages are named after the **minimum** MC version they run on — e.g. `v21_9` for code guarded with `>=1.21.9`. If you extended a package's lower bound in Step 6 (e.g. `v21_9` now runs on `>=$ARGUMENTS <=1.21.11`), rename the package to match the new minimum after the backport compiles. This rename touches:

1. Create new versioned dirs and copy with `sed 's/v21_11/v21_9/g'` to fix package declarations:
   ```bash
   mkdir -p src/main/java/.../subsystem/v21_9
   sed 's/v21_11/v21_9/g' src/main/java/.../subsystem/v21_11/Foo.java \
       > src/main/java/.../subsystem/v21_9/Foo.java
   ```
2. Factory import lines in shared files (e.g. `OneClickInventory.java`, `OneClickStonecutter.java`) — these use Stonecutter inline conditions controlled by the active version
3. `stonecutter-swaps.gradle.kts` — replace all `v21_11` keys with `v21_9` (use `replace_all`)
4. `versions/*/src/main/resources/one-click-crafting.mixins.json` — update the mixin class ref
5. `versions/*/src/test/resources/oneclickcraftingtestmod.mixins.json` — update the package field
6. Commented-out `v21_11` package refs inside `v26_1/` files (they show the "else branch" class)
7. Delete the old `v21_11/` directories

After rename, resync (`./gradlew "Set active project to $ARGUMENTS"`) and compile all 1.21.x versions.

### Simple renames (applies to all call sites across all versions)

Add swaps to `stonecutter-swaps.gradle.kts` under the appropriate version key. Example format:
```kotlin
"$ARGUMENTS" to mapOf(
    "OldClassName" to "NewClassName",
    "old.package.name" to "new.package.name",
),
```
After adding swaps, resync: `./gradlew "Set active project to $ARGUMENTS"` then rebuild.

### Complex behavioral/mixin changes (new versioned class needed)

When the API surface, method signatures, or behavior changed incompatibly:
1. Determine the minimum version this code applies to (the new version being backported, or an existing version if shared).
2. Create a new package `oneclickcrafting.<subsystem>.v<version_underscored>` (e.g. `v21_9` for 1.21.9).
3. Write a new implementation class in that package. Wrap the entire class body in a stonecutter condition:
   ```java
   //? if <version_condition> {
   package com.github.breadmoirai.oneclickcrafting.<subsystem>.v<version>;
   // ... full class content ...
   //? }
   ```
4. Add the package swap to `stonecutter-swaps.gradle.kts` so the correct versioned package is selected per version.

### Mixin versioning rule (CRITICAL)

**Every mixin that has version-specific method signatures or behavior must be a separate class file in a versioned package.** Do NOT add inline stonecutter conditions inside a mixin class in a versioned package — there must be no nested conditions within versioned package files.

- If a mixin needs different behavior across versions, create separate `v21_8/FooMixin.java`, `v21_9/FooMixin.java`, etc.
- The outer condition at the top of the file (e.g. `//? <=1.21.8 {`) is the only condition allowed in a versioned package file.
- Register each versioned mixin separately in the appropriate `versions/*/src/main/resources/one-click-crafting.mixins.json`.
- Mixin entries in `mixins.json` **must be alphabetically sorted**.

**`@ModifyVariable at HEAD` loses the original value — preserve it if needed.** When `@ModifyVariable` modifies a parameter (e.g., converting `button=1` to `button=0` so vanilla treats a right-click as left-click), subsequent `@Inject` callbacks that capture that parameter from the LVT see the **modified** value. If the original value must be passed downstream (e.g., to `onRecipeClick` so it knows the click was a right-click), stash it in a `@Unique` instance field before modifying:

```java
@Unique private int oneclick$originalButton;

@ModifyVariable(method = "mouseClicked(DDI)Z", at = @At("HEAD"), argsOnly = true, ordinal = 0)
private int overrideLeftClickCondition(int button) {
    this.oneclick$originalButton = button;
    if (button == 1 && ...) return 0;
    return button;
}

@Inject(method = "mouseClicked(DDI)Z", at = @At(value = "RETURN", ordinal = 1))
private void onClickSuccess(double x, double y, int button, CallbackInfoReturnable<Boolean> cir) {
    OneClickEvents.RECIPE_CLICK.invoker().onRecipeClick(..., this.oneclick$originalButton);
}
```

Failure to do this causes `onRecipeClick` to always receive `button=0`, which is evaluated against `isEnableLeftClick()` — left-click is disabled by default, so the operation is silently rejected and nothing is crafted.

### Stonecutter swap conflict: use chained swaps, never inline conditions

Stonecutter rejects "Ambiguous replacement" when two version entries map the same source string to different targets (e.g., `"1.21.8" to mapOf("v21_9" to "v21_8")` conflicts with `"26.1" to mapOf("v21_9" to "v26_1")`).

**Never resolve this with inline conditions in factory files.** Instead, use chained swaps:

- Each version entry maps FROM the previous version's name TO its own name.
- `"1.21.9"` maps `v21_8` → `v21_9`; `"26.1"` maps `v21_9` → `v26_1`. No conflict because the source strings differ.
- Swaps must use full package-path specificity (e.g., `"oneclickcrafting.recipebook.v21_8"` → `"oneclickcrafting.recipebook.v21_9"`), never bare `v21_8` fragments.

```kotlin
"1.21.9" to mapOf(
    "oneclickcrafting.recipebook.v21_8" to "oneclickcrafting.recipebook.v21_9",
    "oneclickcrafting.testmod.context.v21_8" to "oneclickcrafting.testmod.context.v21_9",
    "oneclickcrafting.testmod.recipebookhelper.v21_8" to "oneclickcrafting.testmod.recipebookhelper.v21_9",
),
```

Factory files (e.g., `OneClickRecipeBook.java`, `StonecutterContext.java`) use the highest/latest version's import in the vcsVersion source (e.g., `v26_1` for MC 26.1). When Stonecutter switches to an older version, it applies the swap chain in reverse — unwinding each version's swaps in order to produce the correct import for that version.

### Accessor abstraction

Mixin accessor interfaces (e.g., `RecipeBookComponentAccessor`) that differ across versions should:
1. Be placed in versioned packages (`v21_8/`, `v21_9/`, `v26_1/`)
2. Only be used inside the corresponding versioned impl class (e.g., `OneClickRecipeBookImpl`)
3. Expose the functionality via a version-agnostic interface method (e.g., `craftRecipe(collection, id, shift)` on `OneClickRecipeBook`)
4. Test helpers must NOT import versioned accessors directly — they call the interface method instead

For versions where the accessor method signature differs (e.g., 2-param vs 3-param `tryPlaceRecipe`), handle the difference inside the versioned impl class and keep the interface method signature stable.

The version condition syntax (see https://stonecutter.kikugie.dev/wiki/config/params#condition-syntax):
- `>=1.21.9` — at least this version
- `<1.21.11` — below this version
- `>=1.21.9 <1.21.11` — range

### Single call-site differences (local swap or condition)

When only one location differs, use an inline stonecutter comment rather than a new file:

Local swap (replaces one expression):
```java
//~ if >=1.21.9 'expressionForLessThan1.21.9()' /*? -> */ 'expressionForGreaterThanEqualTo1.21.9()'
```

Local condition block (replaces a block of code):
```java
//? if >=1.21.9 {
newBlock();
//? } else {
/*
oldBlock();
*/
//? }
```

## Step 9 — Resync and rebuild after each fix

After each category of fixes, resync stonecutter then rebuild:
```
./gradlew "Set active project to $ARGUMENTS"
./gradlew :$ARGUMENTS:compileJava
```

For versions >= 1.21.4 also verify test compile:
```
./gradlew :$ARGUMENTS:compileTestJava
```

Repeat until compile succeeds. Note: `./gradlew :$ARGUMENTS:build` will FAIL because game tests use `runTestClient` not JUnit — this is expected and pre-existing across all versions.

**Gradle timeout guidance:** resync and compile tasks each take 1–2 minutes. Use 120000ms timeouts (2 minutes) for these commands. Use `GRADLE_USER_HOME=/tmp/gradle-home --no-daemon --project-cache-dir /tmp/gradle-proj-cache` flags if WSL2 I/O errors occur.

## Step 10 — Verify existing versions still compile

Before running tests, confirm you haven't broken any other version:
```bash
./gradlew compileJava
```

This is sufficient — `compileTestJava` for versions < 1.21.4 is `NO-SOURCE` (harmless), and Stonecutter already applies the correct swaps per version.

## Step 10.5 — Rename versioned packages to match their new lower bound

After a successful backport, any versioned package whose lower bound was extended must be renamed so the package name reflects the new minimum. For example, if `v21_8` now runs on `>=1.21.6 <=1.21.11`, it must be renamed to `v21_6`.

**Which packages need renaming:** every package directory whose version condition lower bound was changed in Step 6.

**Rename procedure** (do all in one pass, then resync once):

1. **Create new dirs and copy files** — use `sed` to rewrite every occurrence of the old package fragment:
   ```bash
   mkdir -p src/main/java/.../subsystem/v21_6
   sed 's/oneclickcrafting\.subsystem\.v21_8/oneclickcrafting.subsystem.v21_6/g' \
       src/main/java/.../subsystem/v21_8/Foo.java \
       > src/main/java/.../subsystem/v21_6/Foo.java
   ```
   Repeat for every file/directory that moved.

2. **Update factory file imports** — the shared factory files (`OneClickInventory.java`, `OneClickStonecutter.java`, etc.) reference the old package name in the *currently active* view. Change them directly to the new name (e.g. `v21_8` → `v21_6`). The swap chain carries the rename forward to higher versions at build time.

3. **Update `stonecutter-swaps.gradle.kts`** — replace old package fragment with new one in every swap entry. Also update any intermediate-version files (e.g. `v21_9/OneClickRecipeBookImpl.java`) whose commented-out "else branch" package declaration (line 2) still names the old package — update it to the new name so the swap chain resolves correctly.

4. **Update `versions/*/src/main/resources/one-click-crafting.mixins.json`** — for every version that uses the renamed mixin package, replace `v21_8.MixinName` → `v21_6.MixinName`. Likewise for `versions/*/src/test/resources/oneclickcraftingtestmod.mixins.json` **only for versions >= 1.21.4** (earlier versions have empty test srcDirs and their test mixins.json client array should be empty).

5. **Update `v26_1/` commented-out "else branch" refs** — the `v26_1/` files contain a comment showing the older package name on line 2. Update those to the new name.

6. **Delete old directories.**

7. **Resync and compile all versions:**
   ```bash
   ./gradlew "Set active project to $ARGUMENTS"
   ./gradlew compileJava
   ```

**Test package naming exception:** Test versioned packages (`testmod/inputhelper/vXX`, `testmod/mixin/vXX`) are named after the minimum version that has the client gametest API, not the minimum MC version the code runs on. If you extended a main code package from `>=1.21.4` to `>=1.21.2`, the main package is renamed `v21_2` but the test package **stays** `v21_4` — tests don't run on 1.21.2 or 1.21.3 regardless.

## Step 11 — Run tests

### Check for client gametest API availability

The test infrastructure depends on `net.fabricmc.fabric.api.client.gametest.v1` from `fabric-client-gametest-api-v1`. This module was **added in Fabric API 0.119.x (MC 1.21.4)** and does **not** exist in earlier Fabric API versions (e.g. 0.114.1+1.21.3).

Note: the older `fabric-gametest-api-v1` (server-side game tests) goes back to MC 1.17 and is present in all versions — but that is a different module from the client gametest API used here.

To check whether the new version's Fabric API includes the client gametest module:
```bash
curl -s "https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/{FABRIC_VERSION}/fabric-api-{FABRIC_VERSION}.pom" | grep "client.*gametest\|gametest.*client"
```

**If the module is absent**, clear the test sourceSets for that version in `build.gradle.kts` so `compileTestJava` passes trivially:

```kotlin
val hasClientGameTestApi = property("minecraft_version").toString() >= "1.21.4"

sourceSets {
    named("test") {
        if (!hasClientGameTestApi) {
            java.setSrcDirs(emptyList<File>())
            resources.setSrcDirs(emptyList<File>())
        } else {
            compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.test.get().compileClasspath
            runtimeClasspath += sourceSets.main.get().runtimeClasspath + sourceSets.test.get().runtimeClasspath
        }
    }
}
```

Update the lower-bound version string as needed. Skip the rest of Step 11 for versions without the client gametest API.

Once the build succeeds, run the test client **for the new version**:
```
./gradlew "Set active project to $ARGUMENTS"
./gradlew :$ARGUMENTS:runTestClient
```

Look for `BUILD SUCCESSFUL` at the end. If tests fail, read the log file for the specific assertion failure — **do not grep the Gradle output**:
```bash
grep -E "(ERROR|AssertionError|Expected)" versions/$ARGUMENTS/run/logs/latest.log
```
Then fix and re-run.

### Test timing for versions below 1.21.8

In MC versions before 1.21.8, `tryPlaceRecipe` may send more intermediate `ClientboundContainerSetSlotPacket` updates for the result slot before the final output stabilises, making each repeat craft cycle slower. This manifests as `RepeatLastTests` failures of the form:

```
AssertionError: Expected inventory {minecraft:oak_planks=256} but found {minecraft:oak_log=41, minecraft:oak_planks=92}
```

**Verify the root cause first** — check the log for the "output mismatch" pattern between crafts:
```bash
grep "output mismatch\|output matched\|Operation created" versions/$ARGUMENTS/run/logs/latest.log | head -30
```
If you see 4–5 consecutive "output mismatch (got=air)" lines before each "output matched", the server is sending extra intermediate updates and the fix below applies. If you see 0–1 mismatch lines per craft, the issue is something else.

**Fix**: add version-conditional waits around the `holdRepeatKey` blocks in `RepeatLastTests`. For the no-shift variants, multiply the existing wait by ~3:

```java
input.holdRepeatKey();
//? if <1.21.8 {
/*wait(200);*/
//? } else {
wait(65);
//? }
input.releaseRepeatKey();
```

The shift-hold variant (`repeatLastStacksFullInventory`) runs faster because the server bulk-processes with shift, so it needs a smaller multiplier — bump `wait(24)` to approximately `wait(35)` rather than 3×.

After adjusting, resync and re-run the tests to confirm.

## Step 12 — Add publish step to modrinth.yml

Add a new publish step to `.github/workflows/modrinth.yml` for the new version, placed in ascending version order before the next higher version's publish step. Copy the structure of the adjacent step, substituting the new version. Use `java: 21` for versions up to 1.21.x and `java: 25` for MC 26.1.

```yaml
      - name: Publish $ARGUMENTS
        uses: Kir-Antipov/mc-publish@v3.3
        with:
          modrinth-token: ${{ secrets.M_TOKEN }}
          files: build/libs/${{ env.MOD_VERSION }}/one-click-crafting-${{ env.MOD_VERSION }}+$ARGUMENTS*.jar
          name: "${{ env.MOD_VERSION }}+$ARGUMENTS"
          version: "${{ env.MOD_VERSION }}+$ARGUMENTS"
          version-type: release
          loaders: fabric
          game-versions: "$ARGUMENTS"
          java: 21
          dependencies: |
            fabric-api
            modmenu
            yacl
```

## Step 13 — Report

Summarize what changed:
- Dependency versions used (Fabric loader, Fabric API, YACL, ModMenu)
- Which versioned package conditions were extended (e.g. `>=1.21.10` → `>=1.21.9`)
- Which swaps were added to `stonecutter-swaps.gradle.kts`
- Which new versioned classes were created and why
- Which local conditions/swaps were used and where
- Test results for the new version
- Note any pre-existing test failures in other versions that are unrelated to the backport
