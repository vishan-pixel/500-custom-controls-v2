package com.example.model

enum class LayoutCategory(
    val id: String,
    val displayName: String,
    val iconEmoji: String,
    val colorHex: Long
) {
    ALL("all", "All King Controls", "🔥", 0xFFA855F7),
    VIP("vip", "VIP God Controls", "👑", 0xFFFFD700),
    YOUTUBER("youtuber", "YouTubers (Pain/Deepak)", "🌟", 0xFFEF4444),
    CRYSTAL("crystal", "Crystal PvP", "💎", 0xFFF43F5E),
    MACE("mace", "Mace 1.21+", "🔨", 0xFFFBBF24),
    JOYSTICK("joystick", "Finger Joysticks", "🕹️", 0xFF06B6D4),
    ANCHOR("anchor", "Anchor + Glow", "⚓", 0xFF3B82F6),
    MACRO_SWIPE("macro", "Dual Macros", "⚡", 0xFFA855F7),
    CART("cart", "Cart & Trap PvP", "🛒", 0xFFFB923C),
    SWORD_AXE("sword_axe", "Sword & Pot PvP", "⚔️", 0xFF10B981),
    LEGACY_189("legacy", "1.8.9 Bedwars", "🏹", 0xFF8B5CF6),
    MY_UPLOADS("my_uploads", "My Controls", "📁", 0xFF34D399);

    companion object {
        fun fromId(id: String): LayoutCategory = entries.firstOrNull { it.id == id } ?: ALL
    }
}

enum class LauncherType(val displayName: String, val shortName: String) {
    ALL("All Launchers", "All"),
    POJAV("PojavLauncher", "Pojav"),
    MOJO("Mojo Launcher", "Mojo"),
    ZALITH("Zalith Launcher", "Zalith"),
    HOLY("Holy Launcher", "Holy")
}

enum class FingerStyle(val label: String) {
    ALL("All Grips"),
    TWO_FINGER("2-Finger + Joystick"),
    THREE_FINGER("3-Finger Claw"),
    FOUR_FINGER("4-Finger Pro Claw"),
    FIVE_FINGER("5-Finger God Claw"),
    TABLET("6-Finger / Tablet")
}

data class HudButtonInfo(
    val id: String,
    val name: String,
    val xPercent: Float, // 0.0 to 1.0 (relative to landscape screen)
    val yPercent: Float, // 0.0 to 1.0
    val widthPercent: Float,
    val heightPercent: Float,
    val colorHex: Long,
    val keycodes: List<Int>, // 4-element Pojav keycode list [k1, k2, k3, k4]
    val isSwipe: Boolean = false,
    val isTwoRoles: Boolean = false,
    val roleDesc: String = "",
    val isJoystick: Boolean = false,
    val isToggle: Boolean = false,
    val displayInGame: Boolean = true,
    val displayInMenu: Boolean = false,
    val opacity: Float = 0.55f,
    val strokeColor: Int = -1,
    val strokeWidth: Float = 0.0f,
    val cornerRadius: Float = 0.0f,
    val rawDynamicX: String? = null,
    val rawDynamicY: String? = null,
    val rawWidthPx: Float? = null,
    val rawHeightPx: Float? = null
)

data class ControlLayout(
    val id: String,
    val fileName: String, // e.g. "crystal_1.json"
    val name: String,
    val category: LayoutCategory,
    val targetLaunchers: List<LauncherType>,
    val gameVersions: List<String>,
    val fingerStyle: FingerStyle,
    val description: String,
    val macroGuide: String,
    val author: String,
    val channelInfo: String? = null,
    val rating: Float = 4.9f,
    val downloadsCount: Int = 1200,
    val tags: List<String> = emptyList(),
    val previewButtons: List<HudButtonInfo> = emptyList(),
    val jsonContent: String = "",
    val isFlagship: Boolean = false,
    val hasJoystick: Boolean = true,
    val isUserUploaded: Boolean = false,
    val isVip: Boolean = false,
    val hasLightningEffect: Boolean = false,
    val specialPower: String = "Dual-Macro Instant Pop + 360° Finger Joystick",
    val buttonCount: Int = 34
)

data class CreatorProfile(
    val name: String,
    val handle: String,
    val platform: String,
    val discord: String,
    val subscribers: String,
    val description: String,
    val isFeaturedOwner: Boolean = false
)
