package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ControlLayout
import com.example.model.FingerStyle
import com.example.model.LayoutCategory
import com.example.model.LauncherType
import com.example.ui.components.CreatorHubDialog
import com.example.ui.components.GoogleAuthGateScreen
import com.example.ui.components.HudMiniPreview
import com.example.ui.components.InteractiveHudPreviewDialog
import com.example.ui.components.LayoutDetailSheet
import com.example.ui.components.TutorialDialog
import com.example.ui.components.UploadCustomLayoutDialog
import com.example.ui.components.VipSubscriptionDialog
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CrystalMagenta
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MaceGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YouTuberRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val layouts by viewModel.filteredLayouts.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val isVipUnlocked by viewModel.vipManager.isVipUnlocked.collectAsState()
    val signedInEmail by viewModel.vipManager.signedInEmail.collectAsState()
    val isOwnerAdmin by viewModel.vipManager.isOwnerAdmin.collectAsState()
    val context = LocalContext.current

    // Mandatory Google Login First Gate
    if (signedInEmail == null) {
        GoogleAuthGateScreen(
            vipManager = viewModel.vipManager,
            onSignedIn = {}
        )
        return
    }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.setUploadDialogVisible(true) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = null,
                        tint = Color.Black
                    )
                },
                text = {
                    Text(
                        text = "Upload My Control",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                },
                containerColor = Color(0xFF10B981),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_upload_control")
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "500+ Control Layouts",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF06B6D4))),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "OFFLINE 570+",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        Text(
                            text = if (isOwnerAdmin) "👑 OWNER: $signedInEmail" else "Pojav • Mojo • Zalith • v9 King Engine",
                            color = if (isOwnerAdmin) Color(0xFF34D399) else MaceGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    // Switch Google Account / Logout Button
                    IconButton(
                        onClick = {
                            viewModel.vipManager.signOutGoogle()
                            Toast.makeText(context, "Signed out of Google Account", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("btn_google_logout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Switch Google Account",
                            tint = Color(0xFF94A3B8)
                        )
                    }

                    // VIP God Controls / Subscription Button
                    IconButton(
                        onClick = { viewModel.setVipDialogVisible(true) },
                        modifier = Modifier.testTag("btn_vip_room")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFFFD700).copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, Color(0xFFFFD700), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 16.sp)
                        }
                    }

                    // Upload My Control Button
                    IconButton(
                        onClick = { viewModel.setUploadDialogVisible(true) },
                        modifier = Modifier.testTag("btn_upload_control")
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = "Upload My Own Control",
                            tint = Color(0xFF34D399)
                        )
                    }

                    // Creator Hub Button (Not Altino VIP access)
                    IconButton(
                        onClick = { viewModel.setCreatorHubVisible(true) },
                        modifier = Modifier.testTag("btn_creator_hub")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Not Altino & YouTuber Hub",
                            tint = YouTuberRed
                        )
                    }

                    // How to Import Guide Button
                    IconButton(
                        onClick = { viewModel.setTutorialVisible(true) },
                        modifier = Modifier.testTag("btn_tutorial")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "How to Import",
                            tint = ElectricCyan
                        )
                    }

                    // Favorites Filter Toggle
                    IconButton(
                        onClick = { viewModel.toggleFavoritesView() },
                        modifier = Modifier.testTag("btn_favorites")
                    ) {
                        BadgedBox(
                            badge = {
                                if (favoriteIds.isNotEmpty()) {
                                    Badge(containerColor = MaceGold) {
                                        Text("${favoriteIds.size}", color = Color.Black, fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.showOnlyFavorites) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Favorites",
                                tint = if (uiState.showOnlyFavorites) MaceGold else TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepViolet)
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
                .testTag("layouts_grid"),
            contentPadding = PaddingValues(bottom = 72.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero Banner & Creator Callout (Full Width)
            item(span = { GridItemSpan(2) }) {
                HeroBannerSection(
                    onOpenNotAltino = { viewModel.setCreatorHubVisible(true) },
                    onExploreMace = { viewModel.selectCategory(LayoutCategory.MACE) },
                    onExploreCrystal = { viewModel.selectCategory(LayoutCategory.CRYSTAL) },
                    onExploreJoystick = { viewModel.setSearchQuery("Joystick") },
                    onUploadCustom = { viewModel.setUploadDialogVisible(true) }
                )
            }

            // VIP Dedicated Lightning Banner when VIP category selected
            if (uiState.selectedCategory == LayoutCategory.VIP) {
                item(span = { GridItemSpan(2) }) {
                    VipHeroBannerSection(
                        isVipUnlocked = isVipUnlocked,
                        onOpenVipSubscription = { viewModel.setVipDialogVisible(true) }
                    )
                }
            }

            // Search Bar (Full Width)
            item(span = { GridItemSpan(2) }) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input"),
                    placeholder = {
                        Text(
                            text = "Search 500+ layouts e.g. crystal_34, mace, Not Altino...",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = NeonPurple
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = CardDark,
                        unfocusedContainerColor = CardDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            // Category Scrollable Tabs (Full Width)
            item(span = { GridItemSpan(2) }) {
                ScrollableTabRow(
                    selectedTabIndex = LayoutCategory.entries.indexOf(uiState.selectedCategory),
                    containerColor = Color.Transparent,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        val index = LayoutCategory.entries.indexOf(uiState.selectedCategory)
                        if (index in tabPositions.indices) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[index]),
                                color = Color(uiState.selectedCategory.colorHex)
                            )
                        }
                    },
                    divider = {}
                ) {
                    LayoutCategory.entries.forEach { category ->
                        val isSelected = uiState.selectedCategory == category
                        val catColor = Color(category.colorHex)
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(category) },
                            text = {
                                Text(
                                    text = "${category.iconEmoji} ${category.displayName}",
                                    color = if (isSelected) catColor else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }
            }

            // Launcher Filter Chips (Full Width)
            item(span = { GridItemSpan(2) }) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LAUNCHER FILTER",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${layouts.size} Layouts Found",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(LauncherType.entries) { launcher ->
                            val isSelected = uiState.selectedLauncher == launcher
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonPurple else CardDark)
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonPurple else CardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.selectLauncher(launcher) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = launcher.displayName,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Version & Hand Grip Sub-Filters (Full Width)
            item(span = { GridItemSpan(2) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val versions = listOf("All", "1.21", "1.20", "1.16.5", "1.8.9")
                    items(versions) { ver ->
                        val isSelected = uiState.selectedVersion == ver
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) ElectricCyan.copy(alpha = 0.25f) else Color(0xFF140D26))
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricCyan else Color(0xFF281C4A),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { viewModel.selectVersion(ver) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (ver == "All") "All MC" else "MC $ver",
                                color = if (isSelected) ElectricCyan else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Grip styles
                    items(FingerStyle.entries) { style ->
                        val isSelected = uiState.selectedFingerStyle == style
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) MaceGold.copy(alpha = 0.25f) else Color(0xFF140D26))
                                .border(
                                    1.dp,
                                    if (isSelected) MaceGold else Color(0xFF281C4A),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { viewModel.selectFingerStyle(style) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = style.label,
                                color = if (isSelected) MaceGold else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Empty State Handling
            if (layouts.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No layouts found for this filter",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try clearing the search query or changing filters.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    viewModel.setSearchQuery("")
                                    viewModel.selectCategory(LayoutCategory.ALL)
                                    viewModel.selectLauncher(LauncherType.ALL)
                                    viewModel.selectVersion("All")
                                    viewModel.selectFingerStyle(FingerStyle.ALL)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Reset All Filters", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 2x2 Grid Layout Items (One at left, one at right)
            items(layouts, key = { it.id }) { layout ->
                val isFav = favoriteIds.contains(layout.id)
                LayoutGridCard(
                    layout = layout,
                    isFavorite = isFav,
                    isVipUnlocked = isVipUnlocked,
                    onCardClick = { viewModel.openLayoutDetail(layout) },
                    onToggleFavorite = { viewModel.toggleFavorite(layout.id) },
                    onUnlockVip = { viewModel.setVipDialogVisible(true) },
                    onCopyJson = {
                        if (layout.isVip && !isVipUnlocked) {
                            viewModel.setVipDialogVisible(true)
                        } else {
                            val clip = ClipData.newPlainText(layout.fileName, layout.jsonContent)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(
                                context,
                                "Copied ${layout.fileName}! Create a .json file in File Manager, paste & move to launcher controls!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
            }
        }

        // Modals & Bottom Sheets (Strictly guarded against VIP bypass)
        uiState.selectedLayoutForDetail?.let { layout ->
            if (layout.isVip && !isVipUnlocked) {
                viewModel.closeLayoutDetail()
                viewModel.setVipDialogVisible(true)
            } else {
                val isFav = favoriteIds.contains(layout.id)
                LayoutDetailSheet(
                    layout = layout,
                    isFavorite = isFav,
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onOpenTutorial = { viewModel.setTutorialVisible(true) },
                    onOpenSimulator = { viewModel.openSimulator(it) },
                    onDeleteCustomLayout = { viewModel.deleteCustomLayout(it) },
                    onDismiss = { viewModel.closeLayoutDetail() }
                )
            }
        }

        uiState.layoutForSimulator?.let { layout ->
            InteractiveHudPreviewDialog(
                layout = layout,
                onDismiss = { viewModel.closeSimulator() }
            )
        }

        if (uiState.showUploadDialog) {
            UploadCustomLayoutDialog(
                onDismiss = { viewModel.setUploadDialogVisible(false) },
                onSaveCustomLayout = { name, file, cat, desc, json ->
                    viewModel.saveCustomLayout(name, file, cat, desc, json)
                }
            )
        }

        if (uiState.showCreatorHub) {
            CreatorHubDialog(
                onDismiss = { viewModel.setCreatorHubVisible(false) },
                onSelectCreatorLayouts = { creatorName ->
                    viewModel.setSearchQuery(creatorName)
                }
            )
        }

        if (uiState.showTutorial) {
            TutorialDialog(
                onDismiss = { viewModel.setTutorialVisible(false) }
            )
        }

        if (uiState.showVipDialog) {
            VipSubscriptionDialog(
                vipManager = viewModel.vipManager,
                onDismiss = { viewModel.setVipDialogVisible(false) }
            )
        }
    }
}

@Composable
fun HeroBannerSection(
    onOpenNotAltino: () -> Unit,
    onExploreMace: () -> Unit,
    onExploreCrystal: () -> Unit,
    onExploreJoystick: () -> Unit,
    onUploadCustom: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B0D36)),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.linearGradient(listOf(CrystalMagenta, NeonPurple, ElectricCyan))
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner),
                    contentDescription = "Minecraft Java Mobile Battle Controls Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xCC110826), Color(0xFF1B0D36))
                            )
                        )
                )

                // Top Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                        .border(1.dp, MaceGold, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "★ OFFICIAL NOT ALTINO CONFIGS",
                        color = MaceGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "570+ Pro Minecraft Java Mobile Layouts",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Engineered for PojavLauncher, Mojo & Zalith. Featuring 1-Button 2-Roles macros, swipeable crystals, 360° analog joysticks, and custom offline layout storage!",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenNotAltino,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = YouTuberRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Creators Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onExploreCrystal,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CrystalMagenta),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Crystal PvP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onExploreMace,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaceGold),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Mace 1.21", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onExploreJoystick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0891B2)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("🕹️ 360° Joysticks", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onUploadCustom,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("📁 Upload My Control", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
fun VipHeroBannerSection(
    isVipUnlocked: Boolean,
    onOpenVipSubscription: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onOpenVipSubscription() }
            .testTag("vip_hero_banner"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF160E2A)),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFF22D3EE), Color(0xFFA855F7)))
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_vip_lightning_1790132228225),
                    contentDescription = "VIP Lightning Controls Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xDD0F091F), Color(0xFF160E2A))
                            )
                        )
                )

                // Top Badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFD700), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "👑 VIP GOD TIER",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF22D3EE), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "⚡ 0ms LATENCY",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⚡ VIP GOD CONTROLS ROOM",
                            color = Color(0xFFFFD700),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Super Duper Crazy Fast 0ms Multi-Role Combos",
                            color = Color(0xFF22D3EE),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (isVipUnlocked) Color(0xFF065F46) else Color(0xFF2E1C07),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isVipUnlocked) Color(0xFF10B981) else Color(0xFFFFD700),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isVipUnlocked) "👑 ACTIVE" else "₹50/mo • ₹200/yr",
                            color = if (isVipUnlocked) Color(0xFF6EE7B7) else Color(0xFFFFD700),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Engineered with 360° Hyper Joystick strafing, 1-tap instant crystal detonation, lightning wind-charge mace combos, and thunder electric particle effects.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenVipSubscription,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isVipUnlocked) Color(0xFF8B5CF6) else Color(0xFFFFD700)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isVipUnlocked) "⚙️ Manage VIP / QR Code" else "💳 Unlock VIP Subscription (QR Space)",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LayoutGridCard(
    layout: ControlLayout,
    isFavorite: Boolean,
    isVipUnlocked: Boolean = false,
    onCardClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onUnlockVip: () -> Unit = {},
    onCopyJson: () -> Unit
) {
    val categoryColor = Color(layout.category.colorHex)
    val isLockedVip = layout.isVip && !isVipUnlocked

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                if (isLockedVip) onUnlockVip() else onCardClick()
            }
            .testTag("layout_card_${layout.id}"),
        colors = CardDefaults.cardColors(containerColor = if (layout.isVip) Color(0xFF140D26) else CardDark),
        border = androidx.compose.foundation.BorderStroke(
            width = if (layout.isVip) 1.5.dp else 1.dp,
            brush = if (layout.isVip) {
                Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFF22D3EE), Color(0xFFA855F7)))
            } else if (layout.isFlagship) {
                androidx.compose.ui.graphics.SolidColor(categoryColor)
            } else {
                androidx.compose.ui.graphics.SolidColor(CardBorder)
            }
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Mini Graphical HUD preview photo
            Box {
                HudMiniPreview(
                    buttons = layout.previewButtons,
                    categoryColor = categoryColor,
                    hasLightningEffect = layout.hasLightningEffect,
                    onClick = {
                        if (isLockedVip) onUnlockVip() else onCardClick()
                    }
                )

                if (isLockedVip) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                            .clickable { onUnlockVip() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔒", fontSize = 18.sp)
                            Text(
                                text = "VIP LOCKED",
                                color = Color(0xFFFFD700),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "₹50/mo • ₹200/yr",
                                color = Color(0xFF22D3EE),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Layout File Name (e.g. crystal_1.json, vip_1.json, etc.)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = layout.name,
                    color = if (layout.isVip) Color(0xFFFFD700) else ElectricCyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MaceGold else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Subtitle feature note & Unique Power
            Text(
                text = layout.specialPower,
                color = if (layout.isVip) Color(0xFF22D3EE) else TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Author
            Text(
                text = layout.author.substringBefore(" ("),
                color = if (layout.isVip) Color(0xFFFFD700) else MaceGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Tags (Macro, Grip, VIP)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (layout.isVip) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFD700).copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                            .border(0.5.dp, Color(0xFFFFD700), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "👑 VIP 0ms",
                            color = Color(0xFFFFD700),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(categoryColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = layout.fingerStyle.label,
                            color = categoryColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (layout.tags.contains("1-Button 2-Roles") || layout.tags.contains("Swipeable") || layout.isVip) {
                    Box(
                        modifier = Modifier
                            .background(MaceGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⚡ FAST",
                            color = MaceGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (layout.hasJoystick) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF0891B2).copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🕹️ 360°",
                            color = Color(0xFF22D3EE),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (layout.isUserUploaded) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF059669).copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "📁 MINE",
                            color = Color(0xFF34D399),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Rating & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaceGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "%.1f".format(layout.rating),
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isLockedVip) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFD700))
                            .clickable { onUnlockVip() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "⚡ UNLOCK",
                            color = Color.Black,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2B1D4F))
                            .clickable { onCopyJson() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "JSON",
                                color = ElectricCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
