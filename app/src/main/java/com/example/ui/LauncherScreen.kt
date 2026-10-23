package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.LauncherTab
import com.example.ui.components.AppDrawerSheet
import com.example.ui.components.BookmarksScreen
import com.example.ui.components.ChromeSearchSuggestionsDropdown
import com.example.ui.components.ChromeTopBar
import com.example.ui.components.CustomBrowserView
import com.example.ui.components.GoogleChromeHomeView
import com.example.ui.components.OmniBar
import com.example.ui.components.RightTabRail
import com.example.ui.components.SetDefaultLauncherBanner
import com.example.ui.components.TopTabStrip
import com.example.ui.theme.ChromeDarkBg
import com.example.ui.theme.ChromeDarkBorder
import com.example.ui.theme.ChromeDarkSurface
import com.example.ui.theme.ChromeLightBg
import com.example.ui.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch

@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State from ViewModel
    val currentTab by viewModel.currentTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchEngine by viewModel.searchEngine.collectAsState()
    val isOmniBarFocused by viewModel.isOmniBarFocused.collectAsState()
    val matchingApps by viewModel.searchedApps.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val recentWebsites by viewModel.recentWebsites.collectAsState()
    val recentHistory by viewModel.recentHistory.collectAsState()
    val suggestedQueries by viewModel.suggestedSearches.collectAsState()
    val mathResult by viewModel.mathEvaluationResult.collectAsState()

    val browserTabs by viewModel.browserTabs.collectAsState()
    val activeBrowserTabId by viewModel.activeBrowserTabId.collectAsState()
    val activeTab = browserTabs.find { it.id == activeBrowserTabId } ?: browserTabs.first()

    val webViewCommand by viewModel.webViewCommand.collectAsState()
    val isFullscreen by viewModel.isFullscreen.collectAsState()
    val isHalloweenMode by viewModel.isHalloweenMode.collectAsState()

    val widgetConfigs by viewModel.widgetConfigs.collectAsState()
    val stickyNoteText by viewModel.stickyNoteText.collectAsState()
    val isFahrenheit by viewModel.isFahrenheit.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val hardwareStats by viewModel.hardwareStats.collectAsState()
    val isTorchOn by viewModel.isTorchOn.collectAsState()

    val allApps by viewModel.allApps.collectAsState()
    val selectedAppCategory by viewModel.selectedAppCategory.collectAsState()
    val pinnedApps = allApps.filter { it.isPinned }

    // Back handling:
    // 1. If searching, clear query
    // 2. If in fullscreen, exit fullscreen
    // 3. If in another tab, return to DESKTOP_HOME
    BackHandler(enabled = searchQuery.isNotBlank() || isOmniBarFocused || isFullscreen || currentTab != LauncherTab.DESKTOP_HOME) {
        when {
            searchQuery.isNotBlank() || isOmniBarFocused -> {
                viewModel.setSearchQuery("")
                viewModel.setOmniBarFocused(false)
            }
            isFullscreen -> {
                viewModel.setFullscreen(false)
            }
            currentTab != LauncherTab.DESKTOP_HOME -> {
                viewModel.selectLauncherTab(LauncherTab.DESKTOP_HOME)
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("chrome_launcher_root"),
        containerColor = if (isHalloweenMode) Color.Black else ChromeDarkBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isHalloweenMode) Color.Black else ChromeDarkBg)
        ) {
            // When in Fullscreen Mode: Tabs move UP to the top!
            AnimatedVisibility(visible = isFullscreen) {
                TopTabStrip(
                    tabs = browserTabs,
                    activeTabId = activeBrowserTabId,
                    onSelectTab = { tabId ->
                        viewModel.switchBrowserTab(tabId)
                    },
                    onCloseTab = { tabId -> viewModel.closeBrowserTab(tabId) },
                    onNewTab = { viewModel.addBrowserTab() },
                    onExitFullscreen = { viewModel.setFullscreen(false) }
                )
            }

            // Authentic Chrome Top Bar
            ChromeTopBar(
                query = searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it) },
                activeUrl = if (currentTab == LauncherTab.WEB_BROWSER) activeTab.url else "",
                tabCount = browserTabs.size,
                isFullscreen = isFullscreen,
                onToggleFullscreen = { viewModel.toggleFullscreen() },
                onNavigate = { urlOrQuery ->
                    viewModel.navigateTo(urlOrQuery)
                },
                onHomeClick = {
                    viewModel.setSearchQuery("")
                    viewModel.setOmniBarFocused(false)
                    viewModel.selectLauncherTab(LauncherTab.DESKTOP_HOME)
                },
                onNewTabClick = {
                    viewModel.addBrowserTab()
                },
                onReloadClick = { viewModel.browserReload() },
                onToggleDesktopMode = { viewModel.toggleDesktopMode() },
                isDesktopMode = activeTab.isDesktopMode,
                onOpenAllApps = { viewModel.selectLauncherTab(LauncherTab.ALL_APPS) },
                onOpenDefaultLauncherSettings = { viewModel.openDefaultLauncherSettings(context) },
                onFocusChange = { viewModel.setOmniBarFocused(it) },
                isFocused = isOmniBarFocused,
                isHalloweenMode = isHalloweenMode,
                onToggleHalloweenMode = { viewModel.toggleHalloweenMode() }
            )

            // Main Body: in Portrait mode, tabs are on the RIGHT side!
            Box(modifier = Modifier.weight(1f)) {
                // Big Spider Web Background for Halloween Mode
                if (isHalloweenMode) {
                    Image(
                        painter = painterResource(id = R.drawable.img_spider_web_bg),
                        contentDescription = "Halloween Spider Web",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.85f)
                    )
                }

                Row(modifier = Modifier.fillMaxSize()) {
                    // Center / Main Screen Area (Left side)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        when (currentTab) {
                            LauncherTab.DESKTOP_HOME -> {
                                GoogleChromeHomeView(
                                    query = searchQuery,
                                    onQueryChange = { viewModel.setSearchQuery(it) },
                                    onNavigate = { urlOrQuery -> viewModel.navigateTo(urlOrQuery) },
                                    onFocusChange = { viewModel.setOmniBarFocused(it) },
                                    recentWebsites = recentWebsites,
                                    pinnedApps = pinnedApps,
                                    onLaunchApp = { app -> viewModel.launchApp(app) },
                                    onOpenAllApps = { viewModel.selectLauncherTab(LauncherTab.ALL_APPS) },
                                    isHalloweenMode = isHalloweenMode
                                )
                            }

                            LauncherTab.WEB_BROWSER -> {
                                CustomBrowserView(
                                    activeTab = activeTab,
                                    webViewCommand = webViewCommand,
                                    onClearCommand = { viewModel.clearWebViewCommand() },
                                    onUpdateTabState = { title, url, isLoading, progress, canGoBack, canGoForward ->
                                        viewModel.updateActiveTabState(title, url, isLoading, progress, canGoBack, canGoForward)
                                    },
                                    onBookmarkPage = {
                                        viewModel.bookmarkCurrentPage()
                                        scope.launch { snackbarHostState.showSnackbar("Page bookmarked in Chrome") }
                                    },
                                    isBookmarked = bookmarks.any { it.url == activeTab.url },
                                    onToggleDesktopMode = { viewModel.toggleDesktopMode() },
                                    isNetworkAvailable = hardwareStats.isConnected,
                                    cachedHistory = recentHistory,
                                    onNavigateToUrl = { url -> viewModel.navigateTo(url) },
                                    onGoHome = {
                                        viewModel.setSearchQuery("")
                                        viewModel.setOmniBarFocused(false)
                                        viewModel.selectLauncherTab(LauncherTab.DESKTOP_HOME)
                                    }
                                )
                            }

                            LauncherTab.ALL_APPS -> {
                                AppDrawerSheet(
                                    apps = allApps,
                                    selectedCategory = selectedAppCategory,
                                    onSelectCategory = { viewModel.selectAppCategory(it) },
                                    onLaunchApp = { app -> viewModel.launchApp(app) },
                                    onTogglePinApp = { app -> viewModel.togglePinApp(app) },
                                    onOpenAppInfo = { app -> viewModel.openAppInfo(app) }
                                )
                            }

                            LauncherTab.WIDGETS_HUB -> {
                                GoogleChromeHomeView(
                                    query = searchQuery,
                                    onQueryChange = { viewModel.setSearchQuery(it) },
                                    onNavigate = { urlOrQuery -> viewModel.navigateTo(urlOrQuery) },
                                    onFocusChange = { viewModel.setOmniBarFocused(it) },
                                    recentWebsites = recentWebsites,
                                    pinnedApps = pinnedApps,
                                    onLaunchApp = { app -> viewModel.launchApp(app) },
                                    onOpenAllApps = { viewModel.selectLauncherTab(LauncherTab.ALL_APPS) },
                                    isHalloweenMode = isHalloweenMode
                                )
                            }

                            LauncherTab.BOOKMARKS -> {
                                BookmarksScreen(
                                    bookmarks = bookmarks,
                                    onOpenBookmark = { url -> viewModel.navigateTo(url) },
                                    onAddBookmark = { title, url, emoji ->
                                        viewModel.addCustomBookmark(title, url, emoji)
                                        scope.launch { snackbarHostState.showSnackbar("Bookmark added") }
                                    },
                                    onRemoveBookmark = { id -> viewModel.removeBookmark(id) }
                                )
                            }
                        }
                    }

                    // TABS ON THE RIGHT SIDE IN PORTRAIT MODE
                    // When NOT in fullscreen, right tab rail is visible on the right edge!
                    if (!isFullscreen) {
                        RightTabRail(
                            tabs = browserTabs,
                            activeTabId = activeBrowserTabId,
                            onSelectTab = { tabId ->
                                viewModel.switchBrowserTab(tabId)
                            },
                            onCloseTab = { tabId -> viewModel.closeBrowserTab(tabId) },
                            onNewTab = {
                                viewModel.addBrowserTab()
                            },
                            activeLauncherTab = currentTab,
                            onSelectLauncherTab = { tab -> viewModel.selectLauncherTab(tab) },
                            onToggleFullscreen = { viewModel.setFullscreen(true) }
                        )
                    }
                }

                // Live search suggestions and installed app matching overlay when searching
                if ((isOmniBarFocused || searchQuery.isNotBlank()) && searchQuery.isNotEmpty()) {
                    Box(modifier = Modifier.align(Alignment.TopCenter)) {
                        ChromeSearchSuggestionsDropdown(
                            query = searchQuery,
                            onNavigate = { queryOrUrl ->
                                viewModel.navigateTo(queryOrUrl)
                            },
                            matchingApps = matchingApps,
                            onLaunchApp = { app -> viewModel.launchApp(app) },
                            onTogglePinApp = { app -> viewModel.togglePinApp(app) },
                            matchingBookmarks = bookmarks,
                            suggestedQueries = suggestedQueries,
                            mathResult = mathResult
                        )
                    }
                }
            }
        }
    }
}
