package com.example.data

import com.example.model.ControlLayout
import com.example.model.CreatorProfile
import com.example.model.FingerStyle
import com.example.model.LayoutCategory
import com.example.model.LauncherType

data class TutorialStep(
    val title: String,
    val description: String,
    val iconEmoji: String,
    val pathExample: String
)

object ControlLayoutsRepository {

    private val allLaunchers = listOf(
        LauncherType.POJAV,
        LauncherType.MOJO,
        LauncherType.ZALITH,
        LauncherType.HOLY
    )

    val creatorProfiles: List<CreatorProfile> = listOf(
        CreatorProfile(
            name = "Not Altino",
            handle = "@altino_b4b",
            platform = "YouTube & Discord",
            discord = "Not_Altino",
            subscribers = "Verified Owner",
            description = "Mastermind of the v9 King Controls Engine! Creator of the 35-button G-Blow + Dual Obsidian + Swipe Crystal + 360° Finger Joystick setups.",
            isFeaturedOwner = true
        ),
        CreatorProfile(
            name = "PojavXPain",
            handle = "@pojavxpain",
            platform = "YouTube",
            discord = "PojavXPain_Official",
            subscribers = "520K+ Legends",
            description = "Legendary Pojav & Mojo CPvP god known for insane 38-button multi-role HUDs, instant G-blow totem swaps, and zero-delay crystal/anchor chains."
        ),
        CreatorProfile(
            name = "DeepakWasTaken",
            handle = "@deepakwastaken",
            platform = "YouTube",
            discord = "DeepakWasTaken#0001",
            subscribers = "480K+ Subs",
            description = "Creator of the iconic DeepakWasTaken 5-Finger & 6-Finger Super Crazy Claw layouts with dual anchor/glowstone stacks, pearl clutch, and 360° finger joystick."
        ),
        CreatorProfile(
            name = "Reker",
            handle = "@reker_pvp",
            platform = "YouTube",
            discord = "Reker#0001",
            subscribers = "450K+ Subs",
            description = "Famous Pojav & Mojo crystal PvP YouTuber known for tournament-winning claw setups and smooth camera tracking."
        ),
        CreatorProfile(
            name = "ItzRealME",
            handle = "@itzrealme_mc",
            platform = "YouTube",
            discord = "ItzRealME_MC",
            subscribers = "280K+ Subs",
            description = "Mobile Minecraft Java legend. Pioneer of fast anchor swapping and high-CPS mobile touch layouts on PojavLauncher."
        ),
        CreatorProfile(
            name = "KenHacks",
            handle = "@kenhacks_cpvp",
            platform = "YouTube",
            discord = "KenHacks_Official",
            subscribers = "190K+ Subs",
            description = "CPvP speedrun champion. Expert in swipeable quick crystal layouts and 0-delay multi-action buttons."
        ),
        CreatorProfile(
            name = "AsianGuy",
            handle = "@asianguy_mc",
            platform = "YouTube",
            discord = "AsianGuy_Pojav",
            subscribers = "320K+ Subs",
            description = "PojavLauncher & Zalith veteran. Created the famous 3-Finger + Finger Joystick Compact Claw for fast strafing."
        ),
        CreatorProfile(
            name = "McKit",
            handle = "@mckit_pvp",
            platform = "YouTube",
            discord = "McKit_Official",
            subscribers = "150K+ Subs",
            description = "1.21 Mace tech master. Innovator of Wind Charge bounce + Elytra Rocket instant Mace smash layouts."
        )
    )

    val tutorialSteps: List<TutorialStep> = listOf(
        TutorialStep(
            title = "Step 1: Copy JSON Code from App",
            description = "Tap 'Copy JSON' on any King Control layout in this app. This copies the real playable v9 control code (with mControlDataList & mJoystickDataList) to your clipboard.",
            iconEmoji = "📋",
            pathExample = "Copied v9 JSON code (scaledAt: 100.0, version: 9)"
        ),
        TutorialStep(
            title = "Step 2: Create a .json File in File Manager",
            description = "Open any File Manager app on your phone (such as ZArchiver, MT Manager, or Files). Create a new empty file and name it ending with .json!",
            iconEmoji = "📄",
            pathExample = "Example: king_control.json or pojavxpain_god.json"
        ),
        TutorialStep(
            title = "Step 3: Paste Copied Text into .json File",
            description = "Open the newly created .json file in your file manager's text editor, PASTE the copied JSON text inside it, and tap Save!",
            iconEmoji = "✍️",
            pathExample = "Paste full {\"mControlDataList\": [...], \"version\": 9} text & Save"
        ),
        TutorialStep(
            title = "Step 4: Move .json File to Launcher Controls Folder",
            description = "Cut or Move your saved .json file into the 'controlmap' / controls folder of your launcher (PojavLauncher, Mojo Launcher, or Zalith Launcher):",
            iconEmoji = "📁",
            pathExample = "Pojav: /Android/data/net.kdt.pojavlaunch/files/controlmap/\nMojo: /games/MojoLauncher/controlmap/\nZalith: /Android/data/com.movtery.zalithlauncher/files/controlmap/"
        ),
        TutorialStep(
            title = "Step 5: Load Control & Dominate with Finger Joystick!",
            description = "Open Pojav, Mojo, or Zalith Launcher -> Custom Controls -> Load your moved .json file -> Select as Default and play with 360° Finger Joystick + 35+ King Macro Buttons!",
            iconEmoji = "👑",
            pathExample = "Every button (PRI, SEC, crystal, anchor, glow, totem, g blow, 🧿, joystick) works 100%!"
        )
    )

    val layouts: List<ControlLayout> by lazy {
        generateAllLayouts()
    }

    private fun generateAllLayouts(): List<ControlLayout> {
        val list = mutableListOf<ControlLayout>()

        // 1. Crystal PvP King Layouts (crystal_1.json to crystal_75.json)
        for (i in 1..75) {
            val fileName = "crystal_$i.json"
            val isFirst = i == 1
            val buttons = ProHudArchetypeBuilder.buildKingLayoutButtons(LayoutCategory.CRYSTAL, i, isVip = false)
            val power = ProHudArchetypeBuilder.getSpecialPowerTitle(LayoutCategory.CRYSTAL, i, isVip = false)
            val featureNote = when (i) {
                1 -> "Official Master v9 King Control (Exact 35-Button + Finger Joystick Setup)"
                2 -> "PojavXPain x Altino Dual-Obsidian + G-Blow Crystal Claw"
                3 -> "DeepakWasTaken Hyper CPvP Double Totem & Swipe Crystal"
                4 -> "Left-Thumb Finger Joystick + Right 6-Cluster Instant Detonator"
                5 -> "Zero-Delay Butterfly Crystal [15,-4] + Totem Hit [10,-3] King"
                else -> "King Crystal v9 Pro Control #$i (${buttons.size} Active Elements)"
            }
            val finger = when (i % 4) {
                0 -> FingerStyle.FIVE_FINGER
                1 -> FingerStyle.FOUR_FINGER
                2 -> FingerStyle.THREE_FINGER
                else -> FingerStyle.TWO_FINGER
            }
            val macroDesc = "Real v9 Pojav/Mojo/Zalith Control: crystal [15,-4], obsidian [16,-4], totem hit [10,-3], totem safe [10,-4], g blow [10,-4], anchor [14,-4], glow [13,-4], pearl [12,-4], gap [9,-4], sword [8], hit box [133,30], F4 [133,134], plus 360° Finger Joystick!"
            val json = if (isFirst) {
                UserMasterControlJson.EXACT_USER_V9_JSON
            } else {
                LayoutJsonGenerator.generatePojavControlJson(
                    layoutName = fileName,
                    fileName = fileName,
                    category = "Crystal PvP",
                    buttons = buttons,
                    version = "1.21.x / 1.20+",
                    macroInfo = macroDesc
                )
            }

            list.add(
                ControlLayout(
                    id = "crystal_$i",
                    fileName = fileName,
                    name = fileName,
                    category = LayoutCategory.CRYSTAL,
                    targetLaunchers = allLaunchers,
                    gameVersions = listOf("1.21.x", "1.20+", "1.16.5"),
                    fingerStyle = finger,
                    description = "$featureNote. Power: $power. Built on real v9 controlmap schema with ${buttons.size} playable buttons + 360° Finger Joystick.",
                    macroGuide = macroDesc,
                    author = if (i <= 10) "Not Altino & PojavXPain" else "King CPvP Lab",
                    channelInfo = "YouTube: Not Altino (@altino_b4b) | PojavXPain | DeepakWasTaken",
                    rating = 4.9f + (i % 2) * 0.08f,
                    downloadsCount = 28000 - i * 130,
                    tags = listOf("v9 Playable", "1-Button 2-Roles", "Swipeable", "Finger Joystick", "${buttons.size} Btns"),
                    previewButtons = buttons,
                    jsonContent = json,
                    isFlagship = isFirst,
                    hasJoystick = true,
                    specialPower = power,
                    buttonCount = buttons.size
                )
            )
        }

        // 2. Mace 1.21+ King Layouts (mace_1.json to mace_65.json)
        for (i in 1..65) {
            val fileName = "mace_$i.json"
            val buttons = ProHudArchetypeBuilder.buildKingLayoutButtons(LayoutCategory.MACE, i, isVip = false)
            val power = ProHudArchetypeBuilder.getSpecialPowerTitle(LayoutCategory.MACE, i, isVip = false)
            val featureNote = when (i) {
                1 -> "King Sky-Mace v9: Wind Charge [11,-4,62] + Mace Smash [8,-3] + Finger Joystick"
                2 -> "PojavXPain Aerial Breach IV & Density V Instant Swap Claw"
                3 -> "DeepakWasTaken 5-Finger Elytra Rocket + Pearl + Heavy Mace Combo"
                else -> "Mace 1.21+ v9 King Control #$i (${buttons.size} Active Elements)"
            }
            val macroDesc = "Real v9 Mace Engine: Wind Charge Jump [11,-4,62] launches you sky-high while Mace Smash [8,-3] auto-equips Slot 1 and lands a lethal Left-Click crit! Includes full G-Blow, Totem, Anchor, Crystal, and 360° Finger Joystick."
            val json = LayoutJsonGenerator.generatePojavControlJson(
                layoutName = fileName,
                fileName = fileName,
                category = "Mace 1.21",
                buttons = buttons,
                version = "1.21.x",
                macroInfo = macroDesc
            )

            list.add(
                ControlLayout(
                    id = "mace_$i",
                    fileName = fileName,
                    name = fileName,
                    category = LayoutCategory.MACE,
                    targetLaunchers = allLaunchers,
                    gameVersions = listOf("1.21.x", "1.21.4"),
                    fingerStyle = if (i % 2 == 0) FingerStyle.FOUR_FINGER else FingerStyle.FIVE_FINGER,
                    description = "$featureNote. Power: $power. Full v9 playable controlmap with ${buttons.size} buttons + 360° Finger Joystick.",
                    macroGuide = macroDesc,
                    author = "Not Altino & McKit",
                    channelInfo = "YouTube: Not Altino (@altino_b4b) | McKit",
                    rating = 4.95f,
                    downloadsCount = 19500 - i * 110,
                    tags = listOf("Mace 1.21", "Wind Charge", "1-Button 2-Roles", "Finger Joystick", "${buttons.size} Btns"),
                    previewButtons = buttons,
                    jsonContent = json,
                    isFlagship = i == 1,
                    hasJoystick = true,
                    specialPower = power,
                    buttonCount = buttons.size
                )
            )
        }

        // 3. YouTubers Crazy Controls (youtuber_1.json to youtuber_80.json)
        val ytubers = listOf(
            "PojavXPain (@pojavxpain)",
            "DeepakWasTaken (@deepakwastaken)",
            "Not Altino (@altino_b4b)",
            "Reker (@reker_pvp)",
            "ItzRealME (@itzrealme_mc)",
            "KenHacks (@kenhacks_cpvp)",
            "AsianGuy (@asianguy_mc)",
            "McKit (@mckit_pvp)"
        )
        for (i in 1..80) {
            val fileName = "youtuber_$i.json"
            val yName = ytubers[(i - 1) % ytubers.size]
            val buttons = ProHudArchetypeBuilder.buildKingLayoutButtons(LayoutCategory.YOUTUBER, i, isVip = false)
            val power = ProHudArchetypeBuilder.getSpecialPowerTitle(LayoutCategory.YOUTUBER, i, isVip = false)
            val featureNote = when (i) {
                1 -> "PojavXPain Official 39-Button God Claw v9 [VERIFIED KING]"
                2 -> "DeepakWasTaken Official Super Crazy 5-Finger CPvP HUD [VERIFIED KING]"
                3 -> "Not Altino Official Master v9 Control [EXACT CREATOR JSON]"
                4 -> "Reker Tournament Crystal + Anchor + Finger Joystick Setup"
                5 -> "PojavXPain v2 Hyper Speed G-Blow + Dual Obsidian Matrix"
                6 -> "DeepakWasTaken v2 6-Finger Behemoth + Menu Offhand Swap"
                else -> "${yName.substringBefore(" (")} Signature Crazy Control v$i"
            }
            val macroDesc = "Signature YouTuber v9 Layout ($yName): Packed with ${buttons.size} pro buttons including PRI [-3], SEC [-4], crystal [15,-4], obsidian [16,-4], anchor [14,-4], glow [13,-4], g blow [10,-4], totem [10,-3], pearl [12,-4], gap [9,-4], off hand [34], plus 360° Finger Joystick!"
            val json = if (i == 3) {
                UserMasterControlJson.EXACT_USER_V9_JSON
            } else {
                LayoutJsonGenerator.generatePojavControlJson(
                    layoutName = fileName,
                    fileName = fileName,
                    category = "YouTubers",
                    buttons = buttons,
                    version = "1.21.x / 1.20+",
                    macroInfo = macroDesc
                )
            }

            list.add(
                ControlLayout(
                    id = "youtuber_$i",
                    fileName = fileName,
                    name = fileName,
                    category = LayoutCategory.YOUTUBER,
                    targetLaunchers = allLaunchers,
                    gameVersions = listOf("1.21.x", "1.20+", "1.16.5", "1.8.9"),
                    fingerStyle = if (i % 2 == 0) FingerStyle.FIVE_FINGER else FingerStyle.FOUR_FINGER,
                    description = "$featureNote. Power: $power. Authentic v9 launcher controlmap with ${buttons.size} buttons + Finger Joystick.",
                    macroGuide = macroDesc,
                    author = yName,
                    channelInfo = "Verified Creator v9 Control (${buttons.size} Buttons)",
                    rating = 4.98f,
                    downloadsCount = 34000 - i * 150,
                    tags = listOf("YouTuber King", "PojavXPain/Deepak", "1-Button 2-Roles", "Swipeable", "${buttons.size} Btns"),
                    previewButtons = buttons,
                    jsonContent = json,
                    isFlagship = i <= 3,
                    hasJoystick = true,
                    specialPower = power,
                    buttonCount = buttons.size
                )
            )
        }

        // 4. Anchor PvP Layouts (anchor_1.json to anchor_60.json)
        for (i in 1..60) {
            val fileName = "anchor_$i.json"
            val buttons = ProHudArchetypeBuilder.buildKingLayoutButtons(LayoutCategory.ANCHOR, i, isVip = false)
            val power = ProHudArchetypeBuilder.getSpecialPowerTitle(LayoutCategory.ANCHOR, i, isVip = false)
            val macroDesc = "Dual-Side Anchor v9 Engine: Left-hand 'anc' [14,-4] + double 'glow' [13,-4] paired with Right-hand 'anchor' [14,-4] + 'glow' [13,-4] + safe 'totem' detonate [10,-4] and 360° Finger Joystick!"
            val json = LayoutJsonGenerator.generatePojavControlJson(
                layoutName = fileName,
                fileName = fileName,
                category = "Anchor PvP",
                buttons = buttons,
                version = "1.21.x / 1.20+",
                macroInfo = macroDesc
            )
            list.add(
                ControlLayout(
                    id = "anchor_$i",
                    fileName = fileName,
                    name = fileName,
                    category = LayoutCategory.ANCHOR,
                    targetLaunchers = allLaunchers,
                    gameVersions = listOf("1.21.x", "1.20+", "1.16.5"),
                    fingerStyle = FingerStyle.FOUR_FINGER,
                    description = "King Anchor + Triple Glowstone v9 Control #$i. Power: $power. Features ${buttons.size} buttons + 360° Finger Joystick.",
                    macroGuide = macroDesc,
                    author = "DeepakWasTaken & Anchor Squad",
                    channelInfo = "YouTube: DeepakWasTaken | Not Altino",
                    rating = 4.92f,
                    downloadsCount = 15000 - i * 90,
                    tags = listOf("Anchor + Glow", "1-Button 2-Roles", "Swipeable", "${buttons.size} Btns"),
                    previewButtons = buttons,
                    jsonContent = json,
                    isFlagship = i == 1,
                    hasJoystick = true,
                    specialPower = power,
                    buttonCount = buttons.size
                )
            )
        }

        // 5. Cart PvP Layouts (cart_1.json to cart_55.json)
        for (i in 1..55) {
            val fileName = "cart_$i.json"
            val buttons = ProHudArchetypeBuilder.buildKingLayoutButtons(LayoutCategory.CART, i, isVip = false)
            val power = ProHudArchetypeBuilder.getSpecialPowerTitle(LayoutCategory.CART, i, isVip = false)
            val macroDesc = "Cart Trap v9 Engine: Instant Rail [14,-4] + TNT Cart Deploy & Hit [15,-4,-3] + Full CPvP buttons & 360° Finger Joystick."
            val json = LayoutJsonGenerator.generatePojavControlJson(
                layoutName = fileName,
                fileName = fileName,
                category = "Cart PvP",
                buttons = buttons,
                version = "1.21.x / 1.20+",
                macroInfo = macroDesc
            )
            list.add(
                ControlLayout(
                    id = "cart_$i",
                    fileName = fileName,
                    name = fileName,
                    category = LayoutCategory.CART,
                    targetLaunchers = allLaunchers,
                    gameVersions = listOf("1.21.x", "1.20+", "1.16.5"),
                    fingerStyle = FingerStyle.FOUR_FINGER,
                    description = "TNT Minecart + Rail Instant Trap v9 Control #$i. Power: $power.",
                    macroGuide = macroDesc,
                    author = "Not Altino & Cart Masters",
                    channelInfo = "YouTube: Not Altino (@altino_b4b)",
                    rating = 4.88f,
                    downloadsCount = 11200 - i * 75,
                    tags = listOf("Cart PvP", "1-Button 2-Roles", "Finger Joystick", "${buttons.size} Btns"),
                    previewButtons = buttons,
                    jsonContent = json,
                    isFlagship = i == 1,
                    hasJoystick = true,
                    specialPower = power,
                    buttonCount = buttons.size
                )
            )
        }

        // 6. Dual Macros & Swipeable Layouts (macro_1.json to macro_65.json)
        for (i in 1..65) {
            val fileName = "macro_$i.json"
            val buttons = ProHudArchetypeBuilder.buildKingLayoutButtons(LayoutCategory.MACRO_SWIPE, i, isVip = false)
            val power = ProHudArchetypeBuilder.getSpecialPowerTitle(LayoutCategory.MACRO_SWIPE, i, isVip = false)
            val macroDesc = "Multi-Keycode v9 Macro Board: Combines [15,-4] Crystal, [16,-4] Obsidian, [14,-4] Anchor, [13,-4] Glowstone, [10,-4] G-Blow, [12,-4] Pearl, [9,-4] Gap, [133,30] Hitbox, [133,134] F4, and 360° Finger Joystick!"
            val json = LayoutJsonGenerator.generatePojavControlJson(
                layoutName = fileName,
                fileName = fileName,
                category = "1-Button Macros",
                buttons = buttons,
                version = "All Versions",
                macroInfo = macroDesc
            )
            list.add(
                ControlLayout(
                    id = "macro_$i",
                    fileName = fileName,
                    name = fileName,
                    category = LayoutCategory.MACRO_SWIPE,
                    targetLaunchers = allLaunchers,
                    gameVersions = listOf("1.21.x", "1.20+", "1.16.5", "1.8.9"),
                    fingerStyle = FingerStyle.FIVE_FINGER,
                    description = "Super Crazy Multi-Button Macro v9 Control #$i. Power: $power.",
                    macroGuide = macroDesc,
                    author = "PojavXPain & Altino Tech Lab",
                    channelInfo = "YouTube: PojavXPain | Not Altino",
                    rating = 4.95f,
                    downloadsCount = 24000 - i * 120,
                    tags = listOf("1-Button 2-Roles", "Swipeable", "Finger Joystick", "${buttons.size} Btns"),
                    previewButtons = buttons,
                    jsonContent = json,
                    isFlagship = i == 1,
                    hasJoystick = true,
                    specialPower = power,
                    buttonCount = buttons.size
                )
            )
        }

        // 7. Sword & Pot PvP Layouts (sword_1.json to sword_55.json)
        for (i in 1..55) {
            val fileName = "sword_$i.json"
            val buttons = ProHudArchetypeBuilder.buildKingLayoutButtons(LayoutCategory.SWORD_AXE, i, isVip = false)
            val power = ProHudArchetypeBuilder.getSpecialPowerTitle(LayoutCategory.SWORD_AXE, i, isVip = false)
            val macroDesc = "Sword, Axe Stun & Splash Pot v9 Engine: Instant Sword [8], Gap [9,-4], Pot/Shield [11,-4], Pearl [12,-4], Offhand [34], Scroll Up/Down [-7/-8], and 360° Finger Joystick!"
            val json = LayoutJsonGenerator.generatePojavControlJson(
                layoutName = fileName,
                fileName = fileName,
                category = "Sword & Pot PvP",
                buttons = buttons,
                version = "1.21.x / 1.20+ / 1.16.5",
                macroInfo = macroDesc
            )
            list.add(
                ControlLayout(
                    id = "sword_$i",
                    fileName = fileName,
                    name = fileName,
                    category = LayoutCategory.SWORD_AXE,
                    targetLaunchers = allLaunchers,
                    gameVersions = listOf("1.21.x", "1.20+", "1.16.5"),
                    fingerStyle = FingerStyle.FOUR_FINGER,
                    description = "Tier-1 Sword, Axe & Pot PvP v9 Control #$i. Power: $power.",
                    macroGuide = macroDesc,
                    author = "DeepakWasTaken & Combat Guild",
                    channelInfo = "YouTube: DeepakWasTaken | Not Altino",
                    rating = 4.90f,
                    downloadsCount = 14200 - i * 85,
                    tags = listOf("Sword & Pot", "1-Button 2-Roles", "Finger Joystick", "${buttons.size} Btns"),
                    previewButtons = buttons,
                    jsonContent = json,
                    isFlagship = i == 1,
                    hasJoystick = true,
                    specialPower = power,
                    buttonCount = buttons.size
                )
            )
        }

        // 8. 1.8.9 Bedwars Legacy Layouts (legacy_1.json to legacy_55.json)
        for (i in 1..55) {
            val fileName = "legacy_$i.json"
            val buttons = ProHudArchetypeBuilder.buildKingLayoutButtons(LayoutCategory.LEGACY_189, i, isVip = false)
            val power = ProHudArchetypeBuilder.getSpecialPowerTitle(LayoutCategory.LEGACY_189, i, isVip = false)
            val macroDesc = "1.8.9 Bedwars & God-Bridge v9 Engine: High-CPS PRI [-3] & SEC [-4], Toggle Sneak ◇ [59], Sprint [113], Pearl [12,-4], Gap [9,-4], and 360° Finger Joystick!"
            val json = LayoutJsonGenerator.generatePojavControlJson(
                layoutName = fileName,
                fileName = fileName,
                category = "1.8.9 Legacy",
                buttons = buttons,
                version = "1.8.9",
                macroInfo = macroDesc
            )
            list.add(
                ControlLayout(
                    id = "legacy_$i",
                    fileName = fileName,
                    name = fileName,
                    category = LayoutCategory.LEGACY_189,
                    targetLaunchers = allLaunchers,
                    gameVersions = listOf("1.8.9", "1.21.x"),
                    fingerStyle = FingerStyle.FOUR_FINGER,
                    description = "1.8.9 Hypixel Bedwars & Blockhit v9 Control #$i. Power: $power.",
                    macroGuide = macroDesc,
                    author = "Legacy PvP Kings",
                    channelInfo = "YouTube: Not Altino (@altino_b4b)",
                    rating = 4.91f,
                    downloadsCount = 16500 - i * 95,
                    tags = listOf("1.8.9 Bedwars", "Blockhit", "Finger Joystick", "${buttons.size} Btns"),
                    previewButtons = buttons,
                    jsonContent = json,
                    isFlagship = i == 1,
                    hasJoystick = true,
                    specialPower = power,
                    buttonCount = buttons.size
                )
            )
        }

        // 9. Finger Joystick King Controls (joystick_1.json to joystick_60.json)
        for (i in 1..60) {
            val fileName = "joystick_$i.json"
            val buttons = ProHudArchetypeBuilder.buildKingLayoutButtons(LayoutCategory.JOYSTICK, i, isVip = false)
            val power = ProHudArchetypeBuilder.getSpecialPowerTitle(LayoutCategory.JOYSTICK, i, isVip = false)
            val macroDesc = "360° Finger Joystick Master v9 (mJoystickDataList with forwardLock: true, absolute: true) + ${buttons.size - 1} Pro Macro Buttons for Crystal, Anchor, Mace, Totem, and Offhand!"
            val json = if (i == 1) {
                UserMasterControlJson.EXACT_USER_V9_JSON
            } else {
                LayoutJsonGenerator.generatePojavControlJson(
                    layoutName = fileName,
                    fileName = fileName,
                    category = "Finger Joysticks",
                    buttons = buttons,
                    version = "All Versions",
                    macroInfo = macroDesc
                )
            }
            list.add(
                ControlLayout(
                    id = "joystick_$i",
                    fileName = fileName,
                    name = fileName,
                    category = LayoutCategory.JOYSTICK,
                    targetLaunchers = allLaunchers,
                    gameVersions = listOf("1.21.x", "1.20+", "1.16.5", "1.8.9"),
                    fingerStyle = FingerStyle.FOUR_FINGER,
                    description = "360° Finger Joystick + Full Macro HUD v9 Control #$i. Power: $power.",
                    macroGuide = macroDesc,
                    author = "Not Altino & PojavXPain",
                    channelInfo = "YouTube: Not Altino (@altino_b4b) | PojavXPain",
                    rating = 4.97f,
                    downloadsCount = 29000 - i * 140,
                    tags = listOf("Finger Joystick", "v9 Playable", "1-Button 2-Roles", "${buttons.size} Btns"),
                    previewButtons = buttons,
                    jsonContent = json,
                    isFlagship = i == 1,
                    hasJoystick = true,
                    specialPower = power,
                    buttonCount = buttons.size
                )
            )
        }

        // 10. VIP SUPER SUPER DUPER CRAZY GOD CONTROLS (vip_1.json to vip_55.json)
        for (i in 1..55) {
            val fileName = "vip_$i.json"
            val buttons = ProHudArchetypeBuilder.buildKingLayoutButtons(LayoutCategory.VIP, i, isVip = true)
            val power = ProHudArchetypeBuilder.getSpecialPowerTitle(LayoutCategory.VIP, i, isVip = true)
            val featureNote = when (i) {
                1 -> "VIP #1 KING OF KINGS: PojavXPain x DeepakWasTaken x Altino 41-Button Thunder God Matrix"
                2 -> "VIP #2 0ms Quad-Action Crystal + Obsidian + G-Blow + 360° Finger Joystick"
                3 -> "VIP #3 Super Duper Crazy 6-Finger Behemoth + Sky Mace + Instant Anchor Pop"
                4 -> "VIP #4 Immortal Double-Totem Auto-Swap + Hitbox + Pearl Clutch Beast"
                5 -> "VIP #5 Tournament Undefeated 45 CPS Butterfly Ring + Full v9 Engine"
                else -> "VIP God Tier Super Crazy v9 Control #$i (${buttons.size} Elements)"
            }
            val macroDesc = "VIP Exclusive Super Duper Crazy v9 Engine: ${buttons.size} hyper-tuned buttons including ⚡0ms POP [15,-4,-3,0], 👑GOD ANC [14,-4,13,-4], 🔨SKY MACE [11,-4,8,-3], 🛡️AUTO TOT [10,34,-4,0], 💎HYPER OB [16,-4,15,-4], 🌪️BLITZ [12,-4,62,113], G-Blow, Menu & In-Game Offhand, plus 360° Finger Joystick!"
            val json = LayoutJsonGenerator.generatePojavControlJson(
                layoutName = fileName,
                fileName = fileName,
                category = "VIP God Controls",
                buttons = buttons,
                version = "1.21.x / All Versions",
                macroInfo = macroDesc
            )

            list.add(
                ControlLayout(
                    id = "vip_$i",
                    fileName = fileName,
                    name = fileName,
                    category = LayoutCategory.VIP,
                    targetLaunchers = allLaunchers,
                    gameVersions = listOf("1.21.x", "1.20+", "1.16.5", "1.8.9"),
                    fingerStyle = if (i % 2 == 0) FingerStyle.FIVE_FINGER else FingerStyle.TABLET,
                    description = "$featureNote. Power: $power. Engineered with ${buttons.size} crazy multi-role buttons + 360° Finger Joystick in authentic v9 format.",
                    macroGuide = macroDesc,
                    author = "Not Altino x PojavXPain x DeepakWasTaken VIP Lab",
                    channelInfo = "VIP God Room Exclusive (${buttons.size} Buttons + Finger Joystick)",
                    rating = 5.0f,
                    downloadsCount = 55000 - i * 150,
                    tags = listOf("VIP GOD TIER", "⚡ 0ms Latency", "1-Button 2-Roles", "Swipeable", "${buttons.size} Btns"),
                    previewButtons = buttons,
                    jsonContent = json,
                    isFlagship = i <= 5,
                    hasJoystick = true,
                    isVip = true,
                    hasLightningEffect = true,
                    specialPower = power,
                    buttonCount = buttons.size
                )
            )
        }

        return list
    }
}
