package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppItem
import com.example.data.model.BookmarkItem
import com.example.data.model.BrowserTab
import com.example.data.model.HardwareStats
import com.example.data.model.LauncherTab
import com.example.data.model.RecentWebsite
import com.example.data.model.SearchEngine
import com.example.data.model.WeatherInfo
import com.example.data.model.WidgetConfig
import com.example.data.model.WidgetType
import com.example.data.local.AppDatabase
import com.example.data.local.BrowserHistoryItem
import com.example.data.repository.AppsRepository
import com.example.data.repository.BrowserHistoryRepository
import com.example.data.repository.HardwareMonitor
import com.example.data.repository.LauncherPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = LauncherPreferences(application)
    private val appsRepository = AppsRepository(application, preferences)
    private val hardwareMonitor = HardwareMonitor(application)
    private val database = AppDatabase.getDatabase(application)
    private val historyRepository = BrowserHistoryRepository(database.browserHistoryDao())

    // Current Main Launcher Tab
    private val _currentTab = MutableStateFlow(LauncherTab.DESKTOP_HOME)
    val currentTab: StateFlow<LauncherTab> = _currentTab.asStateFlow()

    // OmniBar & Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchEngine = MutableStateFlow(preferences.getSearchEngine())
    val searchEngine: StateFlow<SearchEngine> = _searchEngine.asStateFlow()

    private val _isOmniBarFocused = MutableStateFlow(false)
    val isOmniBarFocused: StateFlow<Boolean> = _isOmniBarFocused.asStateFlow()

    // Apps State
    private val _allApps = MutableStateFlow<List<AppItem>>(emptyList())
    val allApps: StateFlow<List<AppItem>> = _allApps.asStateFlow()

    private val _selectedAppCategory = MutableStateFlow("All")
    val selectedAppCategory: StateFlow<String> = _selectedAppCategory.asStateFlow()

    // Bookmarks and Speed Dials
    private val _bookmarks = MutableStateFlow(preferences.getBookmarks())
    val bookmarks: StateFlow<List<BookmarkItem>> = _bookmarks.asStateFlow()

    // Recent Websites visited by user
    private val _recentWebsites = MutableStateFlow(preferences.getRecentWebsites())
    val recentWebsites: StateFlow<List<RecentWebsite>> = _recentWebsites.asStateFlow()

    // Widgets
    private val _widgetConfigs = MutableStateFlow(preferences.getWidgetConfigs())
    val widgetConfigs: StateFlow<List<WidgetConfig>> = _widgetConfigs.asStateFlow()

    private val _stickyNoteText = MutableStateFlow(preferences.getStickyNote())
    val stickyNoteText: StateFlow<String> = _stickyNoteText.asStateFlow()

    private val _isFahrenheit = MutableStateFlow(preferences.isFahrenheit())
    val isFahrenheit: StateFlow<Boolean> = _isFahrenheit.asStateFlow()

    private val _weather = MutableStateFlow(
        WeatherInfo(
            temperatureC = 23,
            condition = "Clear Sky",
            location = "Local Cyber Station",
            highC = 26,
            lowC = 17,
            humidity = 48,
            windKmh = 12,
            uvIndex = 4,
            iconEmoji = "☀️"
        )
    )
    val weather: StateFlow<WeatherInfo> = _weather.asStateFlow()

    // Hardware Stats & Torch
    val hardwareStats: StateFlow<HardwareStats> = hardwareMonitor.hardwareStats
    val isTorchOn: StateFlow<Boolean> = hardwareMonitor.isTorchOn

    // Browser Tabs Management
    private val initialTab = BrowserTab(
        id = UUID.randomUUID().toString(),
        title = "New Tab",
        url = ""
    )

    private val _browserTabs = MutableStateFlow(listOf(initialTab))
    val browserTabs: StateFlow<List<BrowserTab>> = _browserTabs.asStateFlow()

    private val _activeBrowserTabId = MutableStateFlow(initialTab.id)
    val activeBrowserTabId: StateFlow<String> = _activeBrowserTabId.asStateFlow()

    // Custom browser webview commands
    private val _webViewCommand = MutableStateFlow<WebViewCommand?>(null)
    val webViewCommand: StateFlow<WebViewCommand?> = _webViewCommand.asStateFlow()

    // Fullscreen state: when true, tabs move to the top; when false (portrait), tabs are on the right side
    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
    }

    fun setFullscreen(enabled: Boolean) {
        _isFullscreen.value = enabled
    }

    // Halloween Mode
    private val _isHalloweenMode = MutableStateFlow(preferences.isHalloweenMode())
    val isHalloweenMode: StateFlow<Boolean> = _isHalloweenMode.asStateFlow()

    fun toggleHalloweenMode() {
        val newVal = !_isHalloweenMode.value
        _isHalloweenMode.value = newVal
        preferences.setHalloweenMode(newVal)
    }


    // Instant math evaluation result if query is a math equation
    val mathEvaluationResult: StateFlow<String?> = _searchQuery.combine(_searchEngine) { q, _ ->
        evaluateSimpleMath(q.trim())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Filtered Apps based on search query
    val searchedApps: StateFlow<List<AppItem>> = combine(_allApps, _searchQuery) { apps, query ->
        if (query.isBlank()) {
            apps
        } else {
            val q = query.lowercase().trim()
            apps.filter {
                it.label.lowercase().contains(q) ||
                it.packageName.lowercase().contains(q) ||
                it.category.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Web Suggestions
    val suggestedSearches: StateFlow<List<String>> = _searchQuery.combine(_searchEngine) { query, _ ->
        if (query.isBlank() || query.startsWith("http://") || query.startsWith("https://")) {
            emptyList()
        } else {
            listOf(
                "$query weather",
                "$query news today",
                "$query wikipedia",
                "$query github",
                "$query images"
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Browsing History from persistent Room Database
    val recentHistory: StateFlow<List<BrowserHistoryItem>> = historyRepository.recentHistory
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val matchingHistory: StateFlow<List<BrowserHistoryItem>> = combine(_searchQuery, historyRepository.recentHistory) { query, history ->
        if (query.isBlank()) {
            history.take(8)
        } else {
            val q = query.trim().lowercase()
            history.filter {
                it.title.lowercase().contains(q) || it.url.lowercase().contains(q)
            }.take(8)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun recordBrowsingHistory(url: String, title: String = "") {
        viewModelScope.launch {
            historyRepository.recordVisit(url, title)
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            historyRepository.deleteHistory(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyRepository.clearAllHistory()
        }
    }

    init {
        loadInstalledApps()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            val apps = appsRepository.getInstalledApps()
            _allApps.value = apps
        }
    }

    fun selectLauncherTab(tab: LauncherTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setOmniBarFocused(focused: Boolean) {
        _isOmniBarFocused.value = focused
    }

    fun setSearchEngine(engine: SearchEngine) {
        _searchEngine.value = engine
        preferences.setSearchEngine(engine)
    }

    fun selectAppCategory(cat: String) {
        _selectedAppCategory.value = cat
    }

    fun launchApp(app: AppItem) {
        appsRepository.launchApp(app)
        // Refresh apps list in background
        loadInstalledApps()
    }

    fun openAppInfo(app: AppItem) {
        appsRepository.openAppInfo(app.packageName)
    }

    fun togglePinApp(app: AppItem) {
        val nowPinned = preferences.togglePinApp(app.packageName)
        _allApps.value = _allApps.value.map {
            if (it.packageName == app.packageName) it.copy(isPinned = nowPinned) else it
        }
    }

    // Browser navigation methods
    fun navigateTo(rawUrlOrQuery: String) {
        val input = rawUrlOrQuery.trim()
        if (input.isEmpty()) return

        val targetUrl = when {
            input.startsWith("http://") || input.startsWith("https://") -> input
            input.contains(".") && !input.contains(" ") -> "https://$input"
            else -> "${_searchEngine.value.searchUrl}${Uri.encode(input)}"
        }

        preferences.setLastUrl(targetUrl)

        // Update active tab or load in web view
        val currentTabId = _activeBrowserTabId.value
        val tabs = _browserTabs.value.toMutableList()
        val index = tabs.indexOfFirst { it.id == currentTabId }
        if (index != -1) {
            tabs[index] = tabs[index].copy(
                url = targetUrl,
                title = input,
                isLoading = true,
                progress = 10
            )
            _browserTabs.value = tabs
        } else {
            val newTab = BrowserTab(
                id = UUID.randomUUID().toString(),
                title = input,
                url = targetUrl,
                isLoading = true
            )
            _browserTabs.value = tabs + newTab
            _activeBrowserTabId.value = newTab.id
        }

        // Switch to web browser tab
        _currentTab.value = LauncherTab.WEB_BROWSER
        _isOmniBarFocused.value = false
        _searchQuery.value = ""

        viewModelScope.launch {
            historyRepository.recordVisit(targetUrl, input)
        }
        preferences.addRecentWebsite(targetUrl, input)
        _recentWebsites.value = preferences.getRecentWebsites()

        _webViewCommand.value = WebViewCommand.LoadUrl(targetUrl)
    }

    fun addBrowserTab(url: String = "") {
        val newTab = BrowserTab(
            id = UUID.randomUUID().toString(),
            title = if (url.isBlank()) "New Tab" else url,
            url = url
        )
        _browserTabs.value = _browserTabs.value + newTab
        _activeBrowserTabId.value = newTab.id
        if (url.isBlank() || url == "chrome://newtab") {
            _currentTab.value = LauncherTab.DESKTOP_HOME
        } else {
            _currentTab.value = LauncherTab.WEB_BROWSER
            _webViewCommand.value = WebViewCommand.LoadUrl(url)
        }
    }

    fun switchBrowserTab(tabId: String) {
        _activeBrowserTabId.value = tabId
        val tab = _browserTabs.value.find { it.id == tabId }
        if (tab != null) {
            if (tab.url.isBlank() || tab.url == "chrome://newtab" || tab.url == "about:blank") {
                _currentTab.value = LauncherTab.DESKTOP_HOME
            } else {
                _currentTab.value = LauncherTab.WEB_BROWSER
                _webViewCommand.value = WebViewCommand.LoadUrl(tab.url)
            }
        }
    }

    fun closeBrowserTab(tabId: String) {
        val tabs = _browserTabs.value.toMutableList()
        if (tabs.size <= 1) {
            // Keep at least one tab on New Tab NTP
            val freshTab = BrowserTab(
                id = UUID.randomUUID().toString(),
                title = "New Tab",
                url = ""
            )
            _browserTabs.value = listOf(freshTab)
            _activeBrowserTabId.value = freshTab.id
            _currentTab.value = LauncherTab.DESKTOP_HOME
            return
        }

        tabs.removeAll { it.id == tabId }
        _browserTabs.value = tabs
        if (_activeBrowserTabId.value == tabId) {
            val last = tabs.last()
            _activeBrowserTabId.value = last.id
            if (last.url.isBlank() || last.url == "chrome://newtab" || last.url == "about:blank") {
                _currentTab.value = LauncherTab.DESKTOP_HOME
            } else {
                _currentTab.value = LauncherTab.WEB_BROWSER
                _webViewCommand.value = WebViewCommand.LoadUrl(last.url)
            }
        }
    }

    fun updateActiveTabState(
        title: String? = null,
        url: String? = null,
        isLoading: Boolean? = null,
        progress: Int? = null,
        canGoBack: Boolean? = null,
        canGoForward: Boolean? = null
    ) {
        val currentTabId = _activeBrowserTabId.value
        val tabs = _browserTabs.value.toMutableList()
        val index = tabs.indexOfFirst { it.id == currentTabId }
        if (index != -1) {
            val old = tabs[index]
            tabs[index] = old.copy(
                title = title ?: old.title,
                url = url ?: old.url,
                isLoading = isLoading ?: old.isLoading,
                progress = progress ?: old.progress,
                canGoBack = canGoBack ?: old.canGoBack,
                canGoForward = canGoForward ?: old.canGoForward
            )
            _browserTabs.value = tabs
        }

        if (url != null && url.isNotBlank() && url != "about:blank" && url != "chrome://newtab") {
            preferences.addRecentWebsite(url, title ?: "")
            _recentWebsites.value = preferences.getRecentWebsites()
            viewModelScope.launch {
                historyRepository.recordVisit(url, title ?: "")
            }
        }
    }

    fun removeRecentWebsite(url: String) {
        preferences.removeRecentWebsite(url)
        _recentWebsites.value = preferences.getRecentWebsites()
    }

    fun toggleDesktopMode() {
        val currentTabId = _activeBrowserTabId.value
        val tabs = _browserTabs.value.toMutableList()
        val index = tabs.indexOfFirst { it.id == currentTabId }
        if (index != -1) {
            val newDesktop = !tabs[index].isDesktopMode
            tabs[index] = tabs[index].copy(isDesktopMode = newDesktop)
            _browserTabs.value = tabs
            _webViewCommand.value = WebViewCommand.SetDesktopMode(newDesktop)
        }
    }

    fun browserGoBack() {
        _webViewCommand.value = WebViewCommand.GoBack
    }

    fun browserGoForward() {
        _webViewCommand.value = WebViewCommand.GoForward
    }

    fun browserReload() {
        _webViewCommand.value = WebViewCommand.Reload
    }

    fun clearWebViewCommand() {
        _webViewCommand.value = null
    }

    // Bookmarks
    fun bookmarkCurrentPage() {
        val activeTab = _browserTabs.value.find { it.id == _activeBrowserTabId.value }
        if (activeTab != null && activeTab.url.isNotBlank()) {
            val item = BookmarkItem(
                id = UUID.randomUUID().toString(),
                title = if (activeTab.title.isNotBlank()) activeTab.title else activeTab.url,
                url = activeTab.url,
                category = "User Saved",
                iconEmoji = "⭐",
                isSpeedDial = true
            )
            preferences.addBookmark(item)
            _bookmarks.value = preferences.getBookmarks()
        }
    }

    fun addCustomBookmark(title: String, url: String, emoji: String = "🌐") {
        val cleanUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
        val item = BookmarkItem(
            id = UUID.randomUUID().toString(),
            title = title.ifBlank { "Bookmark" },
            url = cleanUrl,
            category = "Speed Dial",
            iconEmoji = emoji,
            isSpeedDial = true
        )
        preferences.addBookmark(item)
        _bookmarks.value = preferences.getBookmarks()
    }

    fun removeBookmark(id: String) {
        preferences.removeBookmark(id)
        _bookmarks.value = preferences.getBookmarks()
    }

    // Widgets
    fun updateStickyNote(text: String) {
        _stickyNoteText.value = text
        preferences.setStickyNote(text)
    }

    fun toggleWidgetVisibility(type: WidgetType) {
        val configs = _widgetConfigs.value.map {
            if (it.type == type) it.copy(isVisible = !it.isVisible) else it
        }
        _widgetConfigs.value = configs
        preferences.saveWidgetConfigs(configs)
    }

    fun toggleTemperatureUnit() {
        val newUnit = !_isFahrenheit.value
        _isFahrenheit.value = newUnit
        preferences.setFahrenheit(newUnit)
    }

    fun toggleTorch(): Boolean {
        return hardwareMonitor.toggleTorch()
    }

    fun openDefaultLauncherSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    fun openWifiSettings(context: Context) {
        try {
            context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: Exception) {}
    }

    fun openBluetoothSettings(context: Context) {
        try {
            context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: Exception) {}
    }

    fun openSoundSettings(context: Context) {
        try {
            context.startActivity(Intent(Settings.ACTION_SOUND_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: Exception) {}
    }

    private fun evaluateSimpleMath(expr: String): String? {
        if (expr.isEmpty() || !expr.matches(Regex("^[0-9+\\-*/%^. ()]+$"))) return null
        if (!expr.contains("+") && !expr.contains("-") && !expr.contains("*") && !expr.contains("/") && !expr.contains("%")) return null
        return try {
            val clean = expr.replace(" ", "")
            val result = simpleCalc(clean)
            if (result != null) "= ${"%.4f".format(result).trimEnd('0').trimEnd('.')}" else null
        } catch (_: Exception) {
            null
        }
    }

    private fun simpleCalc(str: String): Double? {
        // Simple 2-operand evaluator for quick omnibar calculations
        val operators = listOf("+", "-", "*", "/", "%")
        for (op in operators) {
            val parts = str.split(op)
            if (parts.size == 2) {
                val left = parts[0].toDoubleOrNull() ?: return null
                val right = parts[1].toDoubleOrNull() ?: return null
                return when (op) {
                    "+" -> left + right
                    "-" -> left - right
                    "*" -> left * right
                    "/" -> if (right != 0.0) left / right else null
                    "%" -> left % right
                    else -> null
                }
            }
        }
        return null
    }

    override fun onCleared() {
        super.onCleared()
        hardwareMonitor.cleanUp()
    }
}

sealed class WebViewCommand {
    data class LoadUrl(val url: String) : WebViewCommand()
    object GoBack : WebViewCommand()
    object GoForward : WebViewCommand()
    object Reload : WebViewCommand()
    data class SetDesktopMode(val enabled: Boolean) : WebViewCommand()
}
