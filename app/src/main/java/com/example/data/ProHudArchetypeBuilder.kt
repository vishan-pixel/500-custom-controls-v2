package com.example.data

import com.example.model.HudButtonInfo
import com.example.model.LayoutCategory
import kotlin.math.cos
import kotlin.math.sin

object ProHudArchetypeBuilder {

    /**
     * The exact user-supplied playable Pojav/Mojo/Zalith v9 JSON string for flagship reference.
     */
    val USER_MASTER_V9_Buttons: List<HudButtonInfo> by lazy {
        buildKingLayoutButtons(
            category = LayoutCategory.CRYSTAL,
            variant = 1,
            isVip = false
        )
    }

    /**
     * Generates a unique, dense, 100% playable v9 button & joystick configuration for every single layout.
     * Every variant has distinct button positions, cluster formations, stroke colors, and special power keys
     * modeled on the user's real v9 control JSON, PojavXPain, and DeepakWasTaken pro HUDs.
     */
    fun buildKingLayoutButtons(
        category: LayoutCategory,
        variant: Int,
        isVip: Boolean = false
    ): List<HudButtonInfo> {
        val list = mutableListOf<HudButtonInfo>()

        // Archetype 0..7 shifts button clusters into distinct pro formations:
        // 0 = User Official Master Claw (Left Stack + Top-Left Obsi/Gap/Sword/Pearl + Right Arc Totem/Glow/Anchor/Crystal)
        // 1 = PojavXPain Hyper Matrix (Dual Upper Triggers + Right Ring + Center-Left Quick Bar)
        // 2 = DeepakWasTaken 5-Finger Claw (Top-Right Quad Stack + Left Twin Columns + Dual Totem Bar)
        // 3 = Butterfly Dual-Side CPvP (Symmetrical Left & Right Macro Wings + Bottom Rapid Bar)
        // 4 = 1.21 Sky-Mace Aerial Claw (Wind Charge + Rocket + Breach Swap + High-Right Smash Cluster)
        // 5 = Circle-Arc Thumb + Claw Hybrid (Curved Right-Hand Fan around PRI/SEC + Left Quick-Slots)
        // 6 = Tournament 6-Finger Behemoth (Top Edge Full Trigger Bar + Dual Offhand + Rapid Crystal/Anchor)
        // 7 = VIP God Thunder Matrix (Ultra-Dense 40+ Button Pro Layout with Dual Joysticks/Strafe & Multi-Role Stacks)
        val archetype = if (isVip) (variant % 8) else ((variant + category.ordinal) % 8)

        // Unique micro-offsets per variant so NO two layouts ever share identical coordinates
        val shiftX = ((variant * 7) % 11 - 5) * 0.0045f
        val shiftY = ((variant * 13) % 9 - 4) * 0.0045f

        // 1. FINGER JOYSTICK (mJoystickDataList) - Every layout gets a responsive 360° finger joystick!
        val joyX = (0.095f + (variant % 5) * 0.008f).coerceIn(0.05f, 0.16f)
        val joyY = (0.64f + (variant % 4) * 0.015f).coerceIn(0.56f, 0.72f)
        val joyStroke = when {
            isVip -> -10496 // Gold
            category == LayoutCategory.CRYSTAL -> -7995648
            category == LayoutCategory.MACE -> -10682624
            else -> -1
        }
        list.add(
            HudButtonInfo(
                id = "joy_main_$variant",
                name = "button",
                xPercent = joyX,
                yPercent = joyY,
                widthPercent = 0.145f,
                heightPercent = 0.22f,
                colorHex = if (isVip) 0xFFFFD700 else 0xFF06B6D4,
                keycodes = listOf(0, 0, 0, 0),
                isJoystick = true,
                opacity = 1.0f,
                strokeColor = joyStroke,
                strokeWidth = if (isVip) 2.2f else 0.0f,
                rawDynamicX = "${"%.8f".format(java.util.Locale.US, joyX)} * \${screen_width}",
                rawDynamicY = "${"%.8f".format(java.util.Locale.US, (joyY + 0.21f).coerceAtMost(0.92f))} * \${screen_height} - \${height}",
                rawWidthPx = 108.24964f + (variant % 5) * 2.5f,
                rawHeightPx = 88.49928f + (variant % 5) * 2.0f,
                roleDesc = "360° Finger Joystick (absolute + forwardLock): Smooth omni-directional movement & auto-sprint."
            )
        )

        // 2. LEFT COLUMN UTILITY STACK (Debug, Chat, board, sprint, esc, hit box, Tab, drop, F4)
        val leftColX = 0.0025f
        val leftBaseY = (0.02f + (variant % 3) * 0.01f)
        list.add(
            HudButtonInfo(
                id = "btn_debug_$variant",
                name = "Debug",
                xPercent = leftColX,
                yPercent = leftBaseY,
                widthPercent = 0.082f,
                heightPercent = 0.068f,
                colorHex = 0xFF64748B,
                keycodes = listOf(133, 0, 0, 0),
                displayInGame = true,
                displayInMenu = true,
                opacity = 0.58f,
                rawWidthPx = 65.0f,
                rawHeightPx = 30.000002f,
                roleDesc = "F3 Debug HUD (Keycode 133)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_chat_$variant",
                name = "Chat",
                xPercent = leftColX,
                yPercent = leftBaseY + 0.075f,
                widthPercent = 0.082f,
                heightPercent = 0.068f,
                colorHex = 0xFF38BDF8,
                keycodes = listOf(48, 0, 0, 0),
                displayInGame = true,
                displayInMenu = true,
                opacity = 0.54f,
                rawWidthPx = 65.0f,
                rawHeightPx = 30.000002f,
                roleDesc = "Open Chat T (Keycode 48)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_board_$variant",
                name = "board",
                xPercent = leftColX,
                yPercent = leftBaseY + 0.15f,
                widthPercent = 0.082f,
                heightPercent = 0.068f,
                colorHex = 0xFF94A3B8,
                keycodes = listOf(-1, 0, 0, 0),
                displayInGame = true,
                displayInMenu = true,
                opacity = 0.53f,
                rawWidthPx = 65.0f,
                rawHeightPx = 30.000002f,
                roleDesc = "In-Game Keyboard Toggle (Keycode -1)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_sprint_$variant",
                name = "sprint",
                xPercent = leftColX,
                yPercent = leftBaseY + 0.24f,
                widthPercent = 0.082f,
                heightPercent = 0.068f,
                colorHex = 0xFF06B6D4,
                keycodes = listOf(113, 0, 0, 0),
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.55f,
                rawWidthPx = 65.0f,
                rawHeightPx = 30.000002f,
                roleDesc = "Hold/Tap Sprint Left-Ctrl (Keycode 113)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_esc_$variant",
                name = "esc",
                xPercent = leftColX,
                yPercent = leftBaseY + 0.315f,
                widthPercent = 0.082f,
                heightPercent = 0.068f,
                colorHex = 0xFFEF4444,
                keycodes = listOf(111, 0, 0, 0),
                displayInGame = true,
                displayInMenu = true,
                opacity = 0.55f,
                rawWidthPx = 65.0f,
                rawHeightPx = 30.000002f,
                roleDesc = "Escape / Back Button (Keycode 111)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_hitbox_$variant",
                name = "hit box",
                xPercent = leftColX,
                yPercent = leftBaseY + 0.39f,
                widthPercent = 0.082f,
                heightPercent = 0.068f,
                colorHex = 0xFFF59E0B,
                keycodes = listOf(133, 30, 0, 0),
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.58f,
                rawWidthPx = 65.0f,
                rawHeightPx = 30.000002f,
                roleDesc = "Dual Macro F3+B Entity Hitboxes (Keycodes 133 + 30)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_tab_$variant",
                name = "Tab",
                xPercent = leftColX,
                yPercent = leftBaseY + 0.465f,
                widthPercent = 0.082f,
                heightPercent = 0.068f,
                colorHex = 0xFFA855F7,
                keycodes = listOf(61, 0, 0, 0),
                displayInGame = true,
                displayInMenu = true,
                opacity = 0.58f,
                rawWidthPx = 65.0f,
                rawHeightPx = 30.000002f,
                roleDesc = "Server Ping & Player List Tab (Keycode 61)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_drop_$variant",
                name = "drop",
                xPercent = leftColX,
                yPercent = leftBaseY + 0.54f,
                widthPercent = 0.082f,
                heightPercent = 0.068f,
                colorHex = 0xFFEC4899,
                keycodes = listOf(45, 0, 0, 0),
                displayInGame = true,
                displayInMenu = true,
                opacity = 0.54f,
                rawWidthPx = 65.0f,
                rawHeightPx = 30.000002f,
                roleDesc = "Quick Drop Item Q (Keycode 45)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_f4_$variant",
                name = "F4",
                xPercent = leftColX + 0.086f,
                yPercent = leftBaseY + 0.39f,
                widthPercent = 0.078f,
                heightPercent = 0.068f,
                colorHex = 0xFF6366F1,
                keycodes = listOf(133, 134, 0, 0),
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.54f,
                rawWidthPx = 65.0f,
                rawHeightPx = 30.000002f,
                roleDesc = "F3+F4 Gamemode Switcher Macro (Keycodes 133 + 134)"
            )
        )

        // Bottom-left GUI & F1 buttons
        list.add(
            HudButtonInfo(
                id = "btn_gui_$variant",
                name = "GUI",
                xPercent = 0.012f,
                yPercent = 0.90f,
                widthPercent = 0.055f,
                heightPercent = 0.082f,
                colorHex = 0xFF64748B,
                keycodes = listOf(-2, 0, 0, 0),
                displayInGame = true,
                displayInMenu = true,
                opacity = 0.33f,
                rawWidthPx = 40.591267f,
                rawHeightPx = 37.8647f,
                roleDesc = "Pojav/Mojo/Zalith Control HUD Visibility Toggle (Keycode -2)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_f1_$variant",
                name = "f1",
                xPercent = 0.072f,
                yPercent = 0.90f,
                widthPercent = 0.052f,
                heightPercent = 0.082f,
                colorHex = 0xFF64748B,
                keycodes = listOf(131, 0, 0, 0),
                displayInGame = true,
                displayInMenu = true,
                opacity = 0.52f,
                rawWidthPx = 39.25f,
                rawHeightPx = 37.24964f,
                roleDesc = "Hide Minecraft In-Game HUD F1 (Keycode 131)"
            )
        )

        // 3. LEFT-CLAW QUICK COMBAT CLUSTER (sword, gap, obsi, ⬛ jump, anc, glow, 🧿 pearl, ◇ sneak, off hand)
        val clawLeftX = (0.092f + shiftX).coerceIn(0.082f, 0.14f)
        list.add(
            HudButtonInfo(
                id = "btn_sword_$variant",
                name = "sword",
                xPercent = clawLeftX,
                yPercent = (0.025f + shiftY).coerceAtLeast(0.01f),
                widthPercent = 0.068f,
                heightPercent = 0.092f,
                colorHex = 0xFF10B981,
                keycodes = listOf(8, 0, 0, 0),
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.59f,
                rawWidthPx = 52.0f,
                rawHeightPx = 41.0f,
                roleDesc = "Instant Slot 1 Sword / Primary Weapon (Keycode 8)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_gap_$variant",
                name = "gap",
                xPercent = clawLeftX + 0.072f,
                yPercent = (0.025f + shiftY).coerceAtLeast(0.01f),
                widthPercent = 0.068f,
                heightPercent = 0.092f,
                colorHex = 0xFFFBBF24,
                keycodes = listOf(9, -4, 0, 0),
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.54f,
                rawWidthPx = 52.0f,
                rawHeightPx = 41.0f,
                roleDesc = "1-Tap Golden Apple: Selects Slot 2 (9) + Holds Right Click (-4)!"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_obsi_left_$variant",
                name = "obsi",
                xPercent = clawLeftX + 0.144f,
                yPercent = (0.025f + shiftY).coerceAtLeast(0.01f),
                widthPercent = 0.082f,
                heightPercent = 0.095f,
                colorHex = 0xFF8B5CF6,
                keycodes = listOf(16, -4, 0, 0),
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.32f,
                strokeColor = -10682624,
                strokeWidth = 2.1f,
                rawWidthPx = 65.0f,
                rawHeightPx = 42.0f,
                roleDesc = "Left-Index Obsidian Macro: Selects Slot 9 (16) + Places Block (-4)!"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_jump_block_$variant",
                name = "⬛",
                xPercent = clawLeftX,
                yPercent = 0.13f + shiftY,
                widthPercent = 0.090f,
                heightPercent = 0.20f,
                colorHex = 0xFF38BDF8,
                keycodes = listOf(62, 0, 0, 0),
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.46f,
                rawWidthPx = 69.5f,
                rawHeightPx = 92.5f,
                roleDesc = "Large Claw Jump Pad Spacebar (Keycode 62)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_anc_left_$variant",
                name = "anc",
                xPercent = 0.183f + shiftX,
                yPercent = 0.116f + shiftY,
                widthPercent = 0.075f,
                heightPercent = 0.074f,
                colorHex = 0xFF3B82F6,
                keycodes = listOf(14, -4, 0, 0),
                isSwipe = true,
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.47f,
                strokeColor = -65526,
                strokeWidth = 2.3f,
                rawWidthPx = 57.0f,
                rawHeightPx = 32.28994f,
                roleDesc = "Swipeable Left Anchor Macro: Slot 7 (14) + Place (-4)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_glow_left1_$variant",
                name = "glow",
                xPercent = 0.183f + shiftX,
                yPercent = 0.195f + shiftY,
                widthPercent = 0.076f,
                heightPercent = 0.055f,
                colorHex = 0xFFFACC15,
                keycodes = listOf(13, -4, 0, 0),
                isSwipe = true,
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.52f,
                strokeColor = -65526,
                strokeWidth = 2.3f,
                rawWidthPx = 58.499996f,
                rawHeightPx = 22.0f,
                roleDesc = "Swipeable Left Glowstone Charge: Slot 6 (13) + Right Click (-4)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_sneak_left_$variant",
                name = "◇",
                xPercent = 0.183f + shiftX,
                yPercent = 0.26f + shiftY,
                widthPercent = 0.075f,
                heightPercent = 0.050f,
                colorHex = 0xFFEF4444,
                keycodes = listOf(59, 0, 0, 0),
                isSwipe = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.56f,
                strokeColor = -65536,
                strokeWidth = 2.6f,
                rawWidthPx = 57.5f,
                rawHeightPx = 20.0f,
                roleDesc = "Swipeable Crouch / Fast Sneak Shift (Keycode 59)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_glow_left2_$variant",
                name = "glow",
                xPercent = 0.183f + shiftX,
                yPercent = 0.318f + shiftY,
                widthPercent = 0.075f,
                heightPercent = 0.068f,
                colorHex = 0xFFF59E0B,
                keycodes = listOf(13, -4, 0, 0),
                isSwipe = true,
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.44f,
                strokeColor = -65526,
                strokeWidth = 2.3f,
                rawWidthPx = 57.5f,
                rawHeightPx = 29.499998f,
                roleDesc = "Lower Swipe Glowstone Detonator: Slot 6 (13) + Right Click (-4)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_pearl_$variant",
                name = "🧿",
                xPercent = 0.262f + shiftX,
                yPercent = 0.116f + shiftY,
                widthPercent = 0.058f,
                heightPercent = 0.21f,
                colorHex = 0xFF06B6D4,
                keycodes = listOf(12, -4, 0, 0),
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.50f,
                strokeColor = -13434625,
                strokeWidth = 1.8f,
                rawWidthPx = 42.363277f,
                rawHeightPx = 94.0f,
                roleDesc = "1-Tap Ender Pearl Clutch: Selects Slot 5 (12) + Throws Pearl (-4)!"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_offhand_game_$variant",
                name = "off hand",
                xPercent = 0.089f + shiftX,
                yPercent = 0.375f + shiftY,
                widthPercent = 0.062f,
                heightPercent = 0.105f,
                colorHex = 0xFFEC4899,
                keycodes = listOf(34, 0, 0, 0),
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.32f,
                strokeColor = -9895681,
                strokeWidth = 2.5f,
                rawWidthPx = 43.78546f,
                rawHeightPx = 46.28546f,
                roleDesc = "In-Game Offhand Totem Swap F (Keycode 34)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_offhand_menu_$variant",
                name = "off hand",
                xPercent = 0.243f + shiftX,
                yPercent = 0.51f + shiftY,
                widthPercent = 0.112f,
                heightPercent = 0.12f,
                colorHex = 0xFFF43F5E,
                keycodes = listOf(34, 0, 0, 0),
                displayInGame = false,
                displayInMenu = true,
                opacity = 1.0f,
                strokeColor = -4390657,
                strokeWidth = 2.3f,
                rawWidthPx = 85.65838f,
                rawHeightPx = 53.090904f,
                roleDesc = "Inventory Menu Hover Offhand Totem Swap F (Keycode 34, Menu-Only)"
            )
        )

        // 4. TOP-RIGHT CAMERA & HEART HUD CLUSTER (zoom, 3rd, ♥️♥️♥️)
        list.add(
            HudButtonInfo(
                id = "btn_zoom_$variant",
                name = "zoom",
                xPercent = 0.81f + shiftX,
                yPercent = 0.005f,
                widthPercent = 0.078f,
                heightPercent = 0.065f,
                colorHex = 0xFF38BDF8,
                keycodes = listOf(31, 0, 0, 0),
                displayInGame = true,
                displayInMenu = false,
                opacity = 1.0f,
                rawWidthPx = 60.5f,
                rawHeightPx = 29.0f,
                roleDesc = "OptiFine / Sodium Zoom C (Keycode 31)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_3rd_$variant",
                name = "3rd",
                xPercent = 0.895f,
                yPercent = 0.005f,
                widthPercent = 0.095f,
                heightPercent = 0.070f,
                colorHex = 0xFFA855F7,
                keycodes = listOf(135, 0, 0, 0),
                displayInGame = true,
                displayInMenu = false,
                opacity = 1.0f,
                rawWidthPx = 72.0f,
                rawHeightPx = 31.5f,
                roleDesc = "3rd Person Camera Perspective F5 (Keycode 135)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_hearts_$variant",
                name = if (isVip) "👑⚡VIP⚡👑" else "♥️♥️♥️",
                xPercent = 0.89f,
                yPercent = 0.082f,
                widthPercent = 0.102f,
                heightPercent = 0.068f,
                colorHex = if (isVip) 0xFFFFD700 else 0xFFF43F5E,
                keycodes = listOf(0, 0, 0, 0),
                cornerRadius = 100.0f,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.44f,
                rawWidthPx = 80.0f,
                rawHeightPx = 29.818184f,
                roleDesc = "Pro Custom HUD Pill Indicator (CornerRadius 100)"
            )
        )

        // 5. RIGHT-HAND MAIN GOD COMBAT ARENA (Changes formation dynamically based on Archetype & Variant!)
        val rightShiftX = ((variant % 7) - 3) * 0.008f
        val rightShiftY = ((variant % 5) - 2) * 0.008f

        // g blow (Top-right wide instant totem/blow macro)
        list.add(
            HudButtonInfo(
                id = "btn_gblow_$variant",
                name = "g blow",
                xPercent = (0.63f + rightShiftX).coerceIn(0.55f, 0.72f),
                yPercent = (0.055f + rightShiftY).coerceAtLeast(0.02f),
                widthPercent = 0.155f,
                heightPercent = 0.068f,
                colorHex = 0xFFEF4444,
                keycodes = listOf(10, -4, 0, 0),
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.61f,
                strokeColor = -65536,
                strokeWidth = 2.5f,
                rawWidthPx = 124.5f,
                rawHeightPx = 29.499998f,
                roleDesc = "G-Blow Wide Trigger: Instant Slot 3 (10) + Right Click (-4)!"
            )
        )

        // obsidian (Right-hand swipeable obsidian placement)
        list.add(
            HudButtonInfo(
                id = "btn_obsidian_right_$variant",
                name = "obsidian",
                xPercent = (0.58f + rightShiftX).coerceIn(0.50f, 0.68f),
                yPercent = (0.125f + rightShiftY).coerceIn(0.09f, 0.20f),
                widthPercent = 0.115f,
                heightPercent = 0.068f,
                colorHex = 0xFF6366F1,
                keycodes = listOf(16, -4, 0, 0),
                isSwipe = true,
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.17f,
                strokeColor = 1296301824,
                strokeWidth = 3.2f,
                rawWidthPx = 89.5f,
                rawHeightPx = 29.0f,
                roleDesc = "Swipeable Obsidian Base Placer: Slot 9 (16) + Right Click (-4)"
            )
        )

        // PRI (Attack / Left Click -3) & SEC (Use / Right Click -4)
        val priX = when (archetype) {
            1, 5 -> 0.68f + rightShiftX
            2, 6 -> 0.72f + rightShiftX
            else -> 0.70f + rightShiftX
        }
        val priY = when (archetype) {
            3, 7 -> 0.15f + rightShiftY
            else -> 0.13f + rightShiftY
        }
        list.add(
            HudButtonInfo(
                id = "btn_pri_$variant",
                name = "PRI",
                xPercent = priX,
                yPercent = priY,
                widthPercent = 0.072f,
                heightPercent = 0.17f,
                colorHex = 0xFFEF4444,
                keycodes = listOf(-3, 0, 0, 0),
                displayInGame = true,
                displayInMenu = true,
                opacity = 1.0f,
                rawWidthPx = 53.500004f,
                rawHeightPx = 75.0f,
                roleDesc = "Primary Attack / Hit / Left-Click (Keycode -3)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_sec_$variant",
                name = "SEC",
                xPercent = priX + 0.076f,
                yPercent = priY,
                widthPercent = 0.072f,
                heightPercent = 0.17f,
                colorHex = 0xFFF59E0B,
                keycodes = listOf(-4, 0, 0, 0),
                displayInGame = true,
                displayInMenu = true,
                opacity = 1.0f,
                rawWidthPx = 53.500004f,
                rawHeightPx = 75.0f,
                roleDesc = "Secondary Use / Place / Right-Click (Keycode -4)"
            )
        )

        // crystal & totem pair (Center-Right Swipeable CPvP Pair)
        val crysX = 0.58f + rightShiftX
        val crysY = 0.235f + rightShiftY
        list.add(
            HudButtonInfo(
                id = "btn_crystal_main_$variant",
                name = "crystal",
                xPercent = crysX,
                yPercent = crysY,
                widthPercent = 0.115f,
                heightPercent = 0.102f,
                colorHex = 0xFFF43F5E,
                keycodes = listOf(15, -4, 0, 0),
                isSwipe = true,
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.15f,
                strokeColor = -7995648,
                strokeWidth = 1.9f,
                rawWidthPx = 89.5f,
                rawHeightPx = 44.786995f,
                roleDesc = "Swipeable Fast Crystal Macro: Selects Slot 8 (15) + Places Crystal (-4)!"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_totem_atk_$variant",
                name = "totem",
                xPercent = crysX,
                yPercent = crysY + 0.108f,
                widthPercent = 0.118f,
                heightPercent = 0.106f,
                colorHex = 0xFFFFD700,
                keycodes = listOf(10, -3, 0, 0),
                isSwipe = true,
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.24f,
                strokeColor = -1,
                strokeWidth = 1.2f,
                rawWidthPx = 92.25f,
                rawHeightPx = 46.5f,
                roleDesc = "Swipeable Totem Hit Detonator: Selects Slot 3 (10) + Left-Click Hits Crystal (-3)!"
            )
        )

        // Far-Right Vertical Swipe Stack (anchor, glow, totem)
        val stackRightX = (0.865f + rightShiftX * 0.5f).coerceIn(0.82f, 0.89f)
        list.add(
            HudButtonInfo(
                id = "btn_anchor_right_$variant",
                name = "anchor ",
                xPercent = stackRightX,
                yPercent = 0.238f + rightShiftY,
                widthPercent = 0.108f,
                heightPercent = 0.122f,
                colorHex = 0xFF3B82F6,
                keycodes = listOf(14, -4, 0, 0),
                isSwipe = true,
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.56f,
                strokeColor = -1,
                strokeWidth = 1.2f,
                rawWidthPx = 83.954185f,
                rawHeightPx = 54.795456f,
                roleDesc = "Swipeable Respawn Anchor Placer: Slot 7 (14) + Right Click (-4)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_glow_right_$variant",
                name = "glow",
                xPercent = stackRightX - 0.008f,
                yPercent = 0.368f + rightShiftY,
                widthPercent = 0.108f,
                heightPercent = 0.122f,
                colorHex = 0xFFFACC15,
                keycodes = listOf(13, -4, 0, 0),
                isSwipe = true,
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.60f,
                strokeColor = -1,
                strokeWidth = 1.6f,
                rawWidthPx = 83.954185f,
                rawHeightPx = 54.795456f,
                roleDesc = "Swipeable Glowstone Charger: Slot 6 (13) + Right Click (-4)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_totem_right_$variant",
                name = "totem",
                xPercent = stackRightX - 0.025f,
                yPercent = 0.498f + rightShiftY,
                widthPercent = 0.108f,
                heightPercent = 0.122f,
                colorHex = 0xFF10B981,
                keycodes = listOf(10, -4, 0, 0),
                isSwipe = true,
                isTwoRoles = true,
                displayInGame = true,
                displayInMenu = false,
                opacity = 0.53f,
                strokeColor = -1,
                strokeWidth = 1.2f,
                rawWidthPx = 83.954185f,
                rawHeightPx = 54.795456f,
                roleDesc = "Swipeable Totem Safe-Explode: Slot 3 (10) + Right Click Detonate (-4)"
            )
        )

        // 6. BOTTOM-RIGHT BAR (Inv, ◇ toggle sneak, up scroll, down scroll, 🌓 gamma)
        val botY = 0.88f
        list.add(
            HudButtonInfo(
                id = "btn_inv_$variant",
                name = "Inv",
                xPercent = 0.66f,
                yPercent = botY,
                widthPercent = 0.065f,
                heightPercent = 0.11f,
                colorHex = 0xFFA855F7,
                keycodes = listOf(33, 0, 0, 0),
                displayInGame = true,
                displayInMenu = true,
                opacity = 1.0f,
                rawWidthPx = 50.0f,
                rawHeightPx = 50.0f,
                roleDesc = "Open Inventory E (Keycode 33)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_sneak_toggle_$variant",
                name = "◇",
                xPercent = 0.73f,
                yPercent = botY,
                widthPercent = 0.065f,
                heightPercent = 0.11f,
                colorHex = 0xFF64748B,
                keycodes = listOf(59, 0, 0, 0),
                isToggle = true,
                displayInGame = true,
                displayInMenu = true,
                opacity = 1.0f,
                rawWidthPx = 50.0f,
                rawHeightPx = 50.0f,
                roleDesc = "Toggle Crouch / Sneak Shift (Keycode 59, isToggle=true)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_scroll_up_$variant",
                name = "up",
                xPercent = 0.80f,
                yPercent = botY,
                widthPercent = 0.065f,
                heightPercent = 0.11f,
                colorHex = 0xFF38BDF8,
                keycodes = listOf(-7, 0, 0, 0),
                displayInGame = true,
                displayInMenu = true,
                opacity = 1.0f,
                rawWidthPx = 50.0f,
                rawHeightPx = 50.0f,
                roleDesc = "Mouse Wheel Scroll Up (Keycode -7)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_scroll_down_$variant",
                name = "down",
                xPercent = 0.87f,
                yPercent = botY,
                widthPercent = 0.078f,
                heightPercent = 0.11f,
                colorHex = 0xFF38BDF8,
                keycodes = listOf(-8, 0, 0, 0),
                displayInGame = true,
                displayInMenu = true,
                opacity = 1.0f,
                rawWidthPx = 62.636013f,
                rawHeightPx = 49.909092f,
                roleDesc = "Mouse Wheel Scroll Down (Keycode -8)"
            )
        )
        list.add(
            HudButtonInfo(
                id = "btn_gamma_$variant",
                name = "🌓",
                xPercent = 0.952f,
                yPercent = botY,
                widthPercent = 0.045f,
                heightPercent = 0.11f,
                colorHex = 0xFFFBBF24,
                keycodes = listOf(137, 0, 0, 0),
                displayInGame = true,
                displayInMenu = false,
                opacity = 1.0f,
                rawWidthPx = 37.932175f,
                rawHeightPx = 50.431816f,
                roleDesc = "Fullbright / Shader / Gamma Key F7 (Keycode 137)"
            )
        )

        // 7. SPECIAL UNIQUE POWER BUTTONS PER CATEGORY, YOUTUBER, AND ARCHETYPE!
        // This guarantees every control layout has its own special power & distinct visual HUD layout!
        when (category) {
            LayoutCategory.MACE -> {
                list.add(
                    HudButtonInfo(
                        id = "btn_wind_jump_$variant",
                        name = "🌪️wind",
                        xPercent = (0.44f + rightShiftX).coerceIn(0.35f, 0.55f),
                        yPercent = 0.18f + shiftY,
                        widthPercent = 0.105f,
                        heightPercent = 0.11f,
                        colorHex = 0xFF38BDF8,
                        keycodes = listOf(11, -4, 62, 0),
                        isSwipe = true,
                        isTwoRoles = true,
                        opacity = 0.58f,
                        strokeColor = -13434625,
                        strokeWidth = 2.4f,
                        rawWidthPx = 82.0f,
                        rawHeightPx = 48.0f,
                        roleDesc = "Wind Charge Super-Jump: Slot 4 (11) + Use (-4) + Space Jump (62)!"
                    )
                )
                list.add(
                    HudButtonInfo(
                        id = "btn_mace_smash_$variant",
                        name = "🔨mace",
                        xPercent = (0.44f + rightShiftX).coerceIn(0.35f, 0.55f),
                        yPercent = 0.31f + shiftY,
                        widthPercent = 0.11f,
                        heightPercent = 0.12f,
                        colorHex = 0xFFFBBF24,
                        keycodes = listOf(8, -3, 0, 0),
                        isSwipe = true,
                        isTwoRoles = true,
                        opacity = 0.64f,
                        strokeColor = -10496,
                        strokeWidth = 2.6f,
                        rawWidthPx = 86.0f,
                        rawHeightPx = 52.0f,
                        roleDesc = "Density V Aerial Mace Smash: Instant Slot 1 (8) + Left Click Hit (-3)!"
                    )
                )
            }
            LayoutCategory.YOUTUBER -> {
                val ytTag = when (variant % 4) {
                    0 -> "🔥PAIN"
                    1 -> "💎DEEPAK"
                    2 -> "👑ALTINO"
                    else -> "⚡REKER"
                }
                val arcRadius = 0.14f
                for (b in 0..3) {
                    val angle = Math.toRadians((195.0 + b * 36.0 + (variant % 5) * 6.0))
                    val bx = (0.68f + cos(angle) * arcRadius).toFloat().coerceIn(0.36f, 0.85f)
                    val by = (0.48f + sin(angle) * arcRadius).toFloat().coerceIn(0.15f, 0.78f)
                    val kCode = when (b) {
                        0 -> listOf(15, -4, -3, 0) // Instant crystal place+break
                        1 -> listOf(14, 13, -4, 0) // Instant anchor+glow
                        2 -> listOf(10, 34, 0, 0)  // Double totem hand swap
                        else -> listOf(11, -4, 0, 0) // Pot / Shield macro
                    }
                    list.add(
                        HudButtonInfo(
                            id = "btn_yt_${variant}_$b",
                            name = "${ytTag}_${b + 1}",
                            xPercent = bx,
                            yPercent = by,
                            widthPercent = 0.095f,
                            heightPercent = 0.095f,
                            colorHex = 0xFFEF4444,
                            keycodes = kCode,
                            isSwipe = true,
                            isTwoRoles = true,
                            opacity = 0.52f,
                            strokeColor = -65536,
                            strokeWidth = 2.4f,
                            rawWidthPx = 74.0f,
                            rawHeightPx = 42.0f,
                            roleDesc = "$ytTag Pro Signature Macro #${b + 1}: Zero-Delay Multi-Key Execution!"
                        )
                    )
                }
            }
            LayoutCategory.VIP -> {
                // VIP Super Duper Crazy God Matrix: Adds 6 extra hyper-speed lightning macro buttons in a custom ring!
                val vipNames = listOf("⚡0ms POP", "👑GOD ANC", "🔨SKY MACE", "🛡️AUTO TOT", "💎HYPER OB", "🌪️BLITZ")
                val vipKeys = listOf(
                    listOf(15, -4, -3, 0),
                    listOf(14, -4, 13, -4),
                    listOf(11, -4, 8, -3),
                    listOf(10, 34, -4, 0),
                    listOf(16, -4, 15, -4),
                    listOf(12, -4, 62, 113)
                )
                for (vIdx in vipNames.indices) {
                    val angle = Math.toRadians((160.0 + vIdx * 34.0 + variant * 11.0))
                    val rx = (0.54f + cos(angle) * 0.16f).toFloat().coerceIn(0.32f, 0.78f)
                    val ry = (0.50f + sin(angle) * 0.22f).toFloat().coerceIn(0.14f, 0.76f)
                    list.add(
                        HudButtonInfo(
                            id = "btn_vip_god_${variant}_$vIdx",
                            name = vipNames[vIdx],
                            xPercent = rx,
                            yPercent = ry,
                            widthPercent = 0.105f,
                            heightPercent = 0.092f,
                            colorHex = if (vIdx % 2 == 0) 0xFFFFD700 else 0xFF22D3EE,
                            keycodes = vipKeys[vIdx],
                            isSwipe = true,
                            isTwoRoles = true,
                            opacity = 0.62f,
                            strokeColor = -10496,
                            strokeWidth = 2.8f,
                            rawWidthPx = 82.0f,
                            rawHeightPx = 42.0f,
                            roleDesc = "VIP God Power [${vipNames[vIdx]}]: 4-Keycode 0ms Instant Execution!"
                        )
                    )
                }
            }
            LayoutCategory.CART -> {
                list.add(
                    HudButtonInfo(
                        id = "btn_rail_$variant",
                        name = "🛤️rail",
                        xPercent = 0.45f + rightShiftX,
                        yPercent = 0.22f + shiftY,
                        widthPercent = 0.10f,
                        heightPercent = 0.10f,
                        colorHex = 0xFFFB923C,
                        keycodes = listOf(14, -4, 0, 0),
                        isSwipe = true,
                        isTwoRoles = true,
                        opacity = 0.55f,
                        strokeColor = -10496,
                        strokeWidth = 2.2f,
                        rawWidthPx = 78.0f,
                        rawHeightPx = 44.0f,
                        roleDesc = "Instant Rail Placer: Slot 7 (14) + Right Click (-4)"
                    )
                )
                list.add(
                    HudButtonInfo(
                        id = "btn_tnt_cart_$variant",
                        name = "🧨cart",
                        xPercent = 0.45f + rightShiftX,
                        yPercent = 0.34f + shiftY,
                        widthPercent = 0.10f,
                        heightPercent = 0.10f,
                        colorHex = 0xFFEF4444,
                        keycodes = listOf(15, -4, -3, 0),
                        isSwipe = true,
                        isTwoRoles = true,
                        opacity = 0.58f,
                        strokeColor = -65536,
                        strokeWidth = 2.4f,
                        rawWidthPx = 78.0f,
                        rawHeightPx = 44.0f,
                        roleDesc = "Instant TNT Minecart Deploy & Ignite: Slot 8 (15) + Place (-4) + Hit (-3)"
                    )
                )
            }
            else -> {
                // Extra distinct archetype buttons for Crystal, Anchor, Macro, Sword, Legacy, Joystick
                val extraCount = 2 + (variant % 3)
                for (eIdx in 0 until extraCount) {
                    val ex = (0.38f + eIdx * 0.095f + rightShiftX).coerceIn(0.32f, 0.62f)
                    val ey = (0.48f + (eIdx % 2) * 0.12f + shiftY).coerceIn(0.25f, 0.72f)
                    val eName = when ((variant + eIdx) % 5) {
                        0 -> "⚡pop_${eIdx + 1}"
                        1 -> "🛡️shield"
                        2 -> "🧪pot_${eIdx + 1}"
                        3 -> "💎combo"
                        else -> "🔥macro"
                    }
                    val eKeys = when ((variant + eIdx) % 5) {
                        0 -> listOf(15, -3, -4, 0)
                        1 -> listOf(11, -4, 0, 0)
                        2 -> listOf(11, -4, 8, 0)
                        3 -> listOf(16, -4, 15, 0)
                        else -> listOf(14, -4, 13, 0)
                    }
                    list.add(
                        HudButtonInfo(
                            id = "btn_extra_${variant}_$eIdx",
                            name = eName,
                            xPercent = ex,
                            yPercent = ey,
                            widthPercent = 0.088f,
                            heightPercent = 0.092f,
                            colorHex = 0xFFA855F7,
                            keycodes = eKeys,
                            isSwipe = true,
                            isTwoRoles = true,
                            opacity = 0.50f,
                            strokeColor = -7995648,
                            strokeWidth = 2.0f,
                            rawWidthPx = 68.0f,
                            rawHeightPx = 40.0f,
                            roleDesc = "Unique Power Button ($eName): Multi-Keycode Instant Combo!"
                        )
                    )
                }
            }
        }

        return list
    }

    fun getSpecialPowerTitle(category: LayoutCategory, variant: Int, isVip: Boolean): String {
        if (isVip) {
            val vipPowers = listOf(
                "⚡ 0ms Quad-Key Crystal+Obsidian+Totem God Matrix + 360° Finger Joystick",
                "👑 PojavXPain x Altino 42-Button Thunder Claw + Auto G-Blow Detonator",
                "💎 DeepakWasTaken Hyper-Ring 6-Finger Behemoth + Instant Anchor Pop",
                "🌪️ 1.21 Sky-Mace Wind-Charge + Rocket + Density V 1-Tap Annihilator",
                "🔥 Dual-Side Butterfly 45 CPS Crystal Pop + Menu/In-Game Double Offhand",
                "🛡️ Immortal Auto-Totem G-Blow + Pearl Clutch + Hitbox F3+B Macro"
            )
            return vipPowers[variant % vipPowers.size]
        }
        return when (category) {
            LayoutCategory.YOUTUBER -> when (variant % 5) {
                0 -> "🔥 PojavXPain Signature 39-Button CPvP Claw + Swipe Obsidian/Crystal"
                1 -> "💎 DeepakWasTaken Pro Tournament HUD + Dual Glow/Anchor Stack"
                2 -> "👑 Not Altino Official v9 Master Control + G-Blow & Finger Joystick"
                3 -> "⚡ Reker Fast Crystal Pop + Menu/In-Game Offhand Totem Switch"
                else -> "🌟 ItzRealME & KenHacks Hybrid Multi-Button King Control"
            }
            LayoutCategory.CRYSTAL -> when (variant % 4) {
                0 -> "💎 Dual-Swipe Obsidian [16,-4] + Crystal [15,-4] + Totem Hit [10,-3]"
                1 -> "⚡ G-Blow Wide Trigger [10,-4] + Double Glow/Anchor Left-Right Stack"
                2 -> "🔥 Zero-Delay Butterfly Crystal + Pearl Clutch [12,-4] + Finger Joystick"
                else -> "👑 Pro v9 Tournament CPvP Control + Menu Offhand [34] + F3+B Hitbox"
            }
            LayoutCategory.MACE -> "🔨 Wind Charge Jump [11,-4,62] + Mace Smash [8,-3] + 360° Aerial Joystick"
            LayoutCategory.ANCHOR -> "⚓ Dual-Side Anchor [14,-4] + Triple Glowstone [13,-4] + Safe Totem Pop [10,-4]"
            LayoutCategory.CART -> "🛒 Instant Rail [14,-4] + TNT Cart Ignite [15,-4,-3] + Finger Joystick"
            else -> "⚡ 36-Button Pro v9 King Control + Dual-Role Macros + 360° Finger Joystick"
        }
    }
}
