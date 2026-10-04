package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ControlLayoutsRepository
import com.example.data.VipManager
import com.example.data.local.AppDatabase
import com.example.data.local.CustomLayoutEntity
import com.example.data.local.FavoriteEntity
import com.example.model.ControlLayout
import com.example.model.FingerStyle
import com.example.model.LayoutCategory
import com.example.model.LauncherType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class MainUiState(
    val selectedCategory: LayoutCategory = LayoutCategory.ALL,
    val selectedLauncher: LauncherType = LauncherType.ALL,
    val selectedVersion: String = "All",
    val selectedFingerStyle: FingerStyle = FingerStyle.ALL,
    val searchQuery: String = "",
    val showOnlyFavorites: Boolean = false,
    val selectedLayoutForDetail: ControlLayout? = null,
    val layoutForSimulator: ControlLayout? = null,
    val showCreatorHub: Boolean = false,
    val showTutorial: Boolean = false,
    val showUploadDialog: Boolean = false,
    val showVipDialog: Boolean = false,
    val totalLayoutsCount: Int = ControlLayoutsRepository.layouts.size,
    val customLayoutsCount: Int = 0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.layoutDao()
    val vipManager = VipManager.getInstance(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = dao.getAllFavorites()
        .combine(MutableStateFlow(Unit)) { favorites, _ ->
            favorites.map { it.layoutId }.toSet()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val customLayouts: StateFlow<List<ControlLayout>> = dao.getAllCustomLayouts()
        .combine(MutableStateFlow(Unit)) { entities, _ ->
            entities.map { entity ->
                val fallbackCat = LayoutCategory.fromId(entity.category)
                ControlLayout(
                    id = entity.id,
                    fileName = entity.fileName,
                    name = entity.name,
                    category = fallbackCat,
                    targetLaunchers = listOf(LauncherType.POJAV, LauncherType.MOJO, LauncherType.ZALITH),
                    gameVersions = listOf("1.21.x", "1.20+", "1.16.5", "1.8.9"),
                    fingerStyle = FingerStyle.FOUR_FINGER,
                    description = entity.description,
                    macroGuide = "Custom user configuration saved locally on your device. Ready for Pojav/Mojo import.",
                    author = "My Custom Layout",
                    channelInfo = "Local Storage (100% Offline)",
                    rating = 5.0f,
                    downloadsCount = 1,
                    tags = listOf("Custom", "My Upload", "Offline"),
                    previewButtons = ControlLayoutsRepository.layouts.first().previewButtons,
                    jsonContent = entity.jsonContent,
                    isUserUploaded = true
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredLayouts: StateFlow<List<ControlLayout>> = combine(
        _uiState,
        favoriteIds,
        customLayouts
    ) { state, favs, userUploads ->
        val allAvailable = userUploads + ControlLayoutsRepository.layouts

        allAvailable.filter { layout ->
            // Category filter
            val matchCategory = when (state.selectedCategory) {
                LayoutCategory.ALL -> true
                LayoutCategory.MY_UPLOADS -> layout.isUserUploaded
                else -> layout.category == state.selectedCategory
            }

            // Launcher filter
            val matchLauncher = state.selectedLauncher == LauncherType.ALL ||
                    layout.targetLaunchers.contains(state.selectedLauncher)

            // Version filter
            val matchVersion = state.selectedVersion == "All" ||
                    layout.gameVersions.any { it.contains(state.selectedVersion, ignoreCase = true) } ||
                    layout.gameVersions.contains("All Versions")

            // Grip filter
            val matchGrip = state.selectedFingerStyle == FingerStyle.ALL ||
                    layout.fingerStyle == state.selectedFingerStyle

            // Favorites filter
            val matchFavorites = !state.showOnlyFavorites || favs.contains(layout.id)

            // Search query filter
            val matchSearch = if (state.searchQuery.isBlank()) true else {
                val q = state.searchQuery.trim().lowercase()
                layout.name.lowercase().contains(q) ||
                        layout.fileName.lowercase().contains(q) ||
                        layout.author.lowercase().contains(q) ||
                        layout.tags.any { it.lowercase().contains(q) } ||
                        layout.description.lowercase().contains(q) ||
                        layout.macroGuide.lowercase().contains(q)
            }

            matchCategory && matchLauncher && matchVersion && matchGrip && matchFavorites && matchSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ControlLayoutsRepository.layouts)

    fun selectCategory(category: LayoutCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category, showOnlyFavorites = false)
    }

    fun selectLauncher(launcher: LauncherType) {
        _uiState.value = _uiState.value.copy(selectedLauncher = launcher)
    }

    fun selectVersion(version: String) {
        _uiState.value = _uiState.value.copy(selectedVersion = version)
    }

    fun selectFingerStyle(style: FingerStyle) {
        _uiState.value = _uiState.value.copy(selectedFingerStyle = style)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun toggleFavoritesView() {
        _uiState.value = _uiState.value.copy(showOnlyFavorites = !_uiState.value.showOnlyFavorites)
    }

    fun openLayoutDetail(layout: ControlLayout) {
        _uiState.value = _uiState.value.copy(selectedLayoutForDetail = layout)
    }

    fun closeLayoutDetail() {
        _uiState.value = _uiState.value.copy(selectedLayoutForDetail = null)
    }

    fun openSimulator(layout: ControlLayout) {
        _uiState.value = _uiState.value.copy(layoutForSimulator = layout)
    }

    fun closeSimulator() {
        _uiState.value = _uiState.value.copy(layoutForSimulator = null)
    }

    fun setCreatorHubVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showCreatorHub = visible)
    }

    fun setTutorialVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showTutorial = visible)
    }

    fun setUploadDialogVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showUploadDialog = visible)
    }

    fun setVipDialogVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showVipDialog = visible)
    }

    fun saveCustomLayout(
        name: String,
        fileName: String,
        category: String,
        description: String,
        json: String
    ) {
        viewModelScope.launch {
            val entity = CustomLayoutEntity(
                id = "custom_${UUID.randomUUID()}",
                name = name,
                fileName = fileName,
                category = category,
                description = description,
                jsonContent = json
            )
            dao.insertCustomLayout(entity)
            selectCategory(LayoutCategory.MY_UPLOADS)
        }
    }

    fun deleteCustomLayout(layoutId: String) {
        viewModelScope.launch {
            dao.deleteCustomLayout(layoutId)
            if (_uiState.value.selectedLayoutForDetail?.id == layoutId) {
                closeLayoutDetail()
            }
        }
    }

    fun toggleFavorite(layoutId: String) {
        viewModelScope.launch {
            val isFav = favoriteIds.value.contains(layoutId)
            if (isFav) {
                dao.removeFavorite(layoutId)
            } else {
                dao.addFavorite(FavoriteEntity(layoutId = layoutId))
            }
        }
    }
}

