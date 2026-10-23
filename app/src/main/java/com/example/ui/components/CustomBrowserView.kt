package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.local.BrowserHistoryItem
import com.example.data.model.BrowserTab
import com.example.ui.theme.ChromeDarkBg
import com.example.ui.theme.ChromeDarkBorder
import com.example.ui.theme.ChromeDarkOmnibox
import com.example.ui.theme.ChromeDarkSurface
import com.example.ui.theme.ChromeLightBg
import com.example.ui.theme.ChromeLightBorder
import com.example.ui.theme.ChromeLightOmnibox
import com.example.ui.theme.ChromeLightSurface
import com.example.ui.theme.ChromeLightTextMuted
import com.example.ui.theme.ChromeLightTextPrimary
import com.example.ui.theme.ChromeLightTextSecondary
import com.example.ui.theme.ChromeTextMuted
import com.example.ui.theme.ChromeTextPrimary
import com.example.ui.theme.ChromeTextSecondary
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleBlueLight
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.GoogleGreenLight
import com.example.ui.theme.GoogleYellow
import com.example.ui.theme.GoogleYellowLight
import com.example.ui.viewmodel.WebViewCommand

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CustomBrowserView(
    activeTab: BrowserTab,
    webViewCommand: WebViewCommand?,
    onClearCommand: () -> Unit,
    onUpdateTabState: (title: String?, url: String?, isLoading: Boolean?, progress: Int?, canGoBack: Boolean?, canGoForward: Boolean?) -> Unit,
    onBookmarkPage: () -> Unit,
    isBookmarked: Boolean,
    onToggleDesktopMode: () -> Unit,
    isNetworkAvailable: Boolean = true,
    cachedHistory: List<BrowserHistoryItem> = emptyList(),
    onNavigateToUrl: (String) -> Unit = {},
    onGoHome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var currentUrlDisplay by remember { mutableStateOf(activeTab.url) }
    var pageTitleDisplay by remember { mutableStateOf(activeTab.title) }
    var loadProgress by remember { mutableStateOf(0) }
    var isLoadingNow by remember { mutableStateOf(false) }
    var isPageLoadError by remember { mutableStateOf(false) }

    // Theme Colors for upper search bar, background, and icons
    val browserBg = ChromeDarkBg
    val controlsBarBg = ChromeDarkSurface
    val urlCapsuleBg = ChromeDarkOmnibox
    val urlCapsuleBorder = ChromeDarkBorder
    val textPrimary = ChromeTextPrimary
    val textSecondary = ChromeTextSecondary
    val textMuted = ChromeTextMuted
    val domainTextColor = GoogleBlue
    val lockIconColor = GoogleGreen
    val progressColor = GoogleBlue
    val bookmarkStarTint = if (isBookmarked) GoogleYellow else textSecondary

    // Intercept back key to navigate web history if canGoBack
    BackHandler(enabled = activeTab.canGoBack) {
        webViewInstance?.let { wv ->
            if (wv.canGoBack()) {
                wv.goBack()
            }
        }
    }

    // Handle commands from ViewModel
    LaunchedEffect(webViewCommand) {
        when (webViewCommand) {
            is WebViewCommand.LoadUrl -> {
                webViewInstance?.loadUrl(webViewCommand.url)
                onClearCommand()
            }
            is WebViewCommand.GoBack -> {
                webViewInstance?.goBack()
                onClearCommand()
            }
            is WebViewCommand.GoForward -> {
                webViewInstance?.goForward()
                onClearCommand()
            }
            is WebViewCommand.Reload -> {
                webViewInstance?.reload()
                onClearCommand()
            }
            is WebViewCommand.SetDesktopMode -> {
                webViewInstance?.let { wv ->
                    applyDesktopMode(wv.settings, webViewCommand.enabled)
                    wv.reload()
                }
                onClearCommand()
            }
            null -> {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(browserBg)
    ) {
        // Browser Upper Controls / Search Bar
        Surface(
            color = controlsBarBg,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Navigation Arrows & Reload
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { webViewInstance?.goBack() },
                            enabled = activeTab.canGoBack,
                            modifier = Modifier.size(32.dp).testTag("btn_browser_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = if (activeTab.canGoBack) textPrimary else textMuted,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        IconButton(
                            onClick = { webViewInstance?.goForward() },
                            enabled = activeTab.canGoForward,
                            modifier = Modifier.size(32.dp).testTag("btn_browser_forward")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Forward",
                                tint = if (activeTab.canGoForward) textPrimary else textMuted,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        IconButton(
                            onClick = { webViewInstance?.reload() },
                            modifier = Modifier.size(32.dp).testTag("btn_browser_reload")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reload",
                                tint = textSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    // Active URL & Title Capsule (Upper search/URL bar)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = urlCapsuleBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, urlCapsuleBorder),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .height(30.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Secure",
                                tint = lockIconColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = sanitizeDomain(currentUrlDisplay),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = domainTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }

                    // Quick Actions (Desktop mode, Bookmark, Share, Open in Browser)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Desktop Mode Toggle
                        IconButton(
                            onClick = onToggleDesktopMode,
                            modifier = Modifier.size(32.dp).testTag("btn_desktop_mode")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = "Toggle Desktop Mode",
                                tint = if (activeTab.isDesktopMode) domainTextColor else textMuted,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Bookmark Star
                        IconButton(
                            onClick = onBookmarkPage,
                            modifier = Modifier.size(32.dp).testTag("btn_bookmark_page")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Bookmark Page",
                                tint = bookmarkStarTint,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Share Link
                        IconButton(
                            onClick = {
                                shareUrl(context, currentUrlDisplay, pageTitleDisplay)
                            },
                            modifier = Modifier.size(32.dp).testTag("btn_share_page")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Link",
                                tint = textSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Open External Browser
                        IconButton(
                            onClick = {
                                openExternal(context, currentUrlDisplay)
                            },
                            modifier = Modifier.size(32.dp).testTag("btn_open_external")
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = "Open in External App",
                                tint = textSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }

                // Progress Bar
                AnimatedVisibility(visible = isLoadingNow && loadProgress < 100) {
                    LinearProgressIndicator(
                        progress = { loadProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp),
                        color = progressColor,
                        trackColor = Color.Transparent
                    )
                }
            }
        }

        // Real Interactive Web View & Website Scroll
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        setBackgroundColor(android.graphics.Color.parseColor("#202124"))

                        val cookieManager = android.webkit.CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                        try {
                            cookieManager.setAcceptThirdPartyCookies(this, true)
                        } catch (_: Exception) {}

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            builtInZoomControls = true
                            displayZoomControls = false
                            cacheMode = if (!isNetworkAvailable) WebSettings.LOAD_CACHE_ELSE_NETWORK else WebSettings.LOAD_DEFAULT
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            applyDesktopMode(this, activeTab.isDesktopMode)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                forceDark = WebSettings.FORCE_DARK_ON
                            }
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoadingNow = true
                                if (isNetworkAvailable) {
                                    isPageLoadError = false
                                }
                                if (url != null) currentUrlDisplay = url
                                onUpdateTabState(null, url, true, 10, view?.canGoBack(), view?.canGoForward())
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoadingNow = false
                                loadProgress = 100
                                val title = view?.title ?: ""
                                if (title.isNotBlank()) pageTitleDisplay = title
                                if (url != null) currentUrlDisplay = url
                                onUpdateTabState(
                                    title,
                                    url,
                                    false,
                                    100,
                                    view?.canGoBack(),
                                    view?.canGoForward()
                                )
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)
                                if (request?.isForMainFrame == true) {
                                    isPageLoadError = true
                                    isLoadingNow = false
                                }
                            }

                            @Suppress("DEPRECATION")
                            override fun onReceivedError(
                                view: WebView?,
                                errorCode: Int,
                                description: String?,
                                failingUrl: String?
                            ) {
                                super.onReceivedError(view, errorCode, description, failingUrl)
                                isPageLoadError = true
                                isLoadingNow = false
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val url = request?.url?.toString() ?: return false
                                if (url.startsWith("http://") || url.startsWith("https://")) {
                                    return false // Load in WebView
                                }
                                // Handle deep links (mailto, tel, market)
                                return try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    ctx.startActivity(intent)
                                    true
                                } catch (_: Exception) {
                                    true
                                }
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                loadProgress = newProgress
                                onUpdateTabState(null, null, newProgress < 100, newProgress, null, null)
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                if (!title.isNullOrBlank()) {
                                    pageTitleDisplay = title
                                    onUpdateTabState(title, null, null, null, null, null)
                                }
                            }
                        }

                        if (activeTab.url.isNotBlank() && activeTab.url != "chrome://newtab" && activeTab.url != "about:blank") {
                            loadUrl(activeTab.url)
                        }
                        webViewInstance = this
                    }
                },
                update = { wv ->
                    wv.setBackgroundColor(android.graphics.Color.parseColor("#202124"))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        wv.settings.forceDark = WebSettings.FORCE_DARK_ON
                    }
                    if (activeTab.url.isNotBlank() && activeTab.url != "chrome://newtab" && activeTab.url != "about:blank" && wv.url != activeTab.url) {
                        wv.loadUrl(activeTab.url)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("pc_kool_custom_webview")
            )

            val showOfflineScreen = (!isNetworkAvailable || isPageLoadError) &&
                activeTab.url.isNotBlank() &&
                activeTab.url != "about:blank" &&
                activeTab.url != "chrome://newtab"

            if (showOfflineScreen) {
                CustomNoInternetView(
                    failedUrl = activeTab.url,
                    cachedHistory = cachedHistory,
                    onRetry = {
                        isPageLoadError = false
                        webViewInstance?.reload()
                    },
                    onGoHome = onGoHome,
                    onOpenCachedUrl = { url ->
                        isPageLoadError = false
                        onNavigateToUrl(url)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewInstance?.destroy()
            webViewInstance = null
        }
    }
}

private fun applyDesktopMode(settings: WebSettings, isDesktop: Boolean) {
    if (isDesktop) {
        settings.userAgentString =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"
    } else {
        settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"
    }
}

private fun sanitizeDomain(url: String): String {
    return try {
        val uri = Uri.parse(url)
        val host = uri.host ?: url
        host.removePrefix("www.")
    } catch (_: Exception) {
        url
    }
}

private fun shareUrl(context: Context, url: String, title: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "$title\n$url")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share link via INKO Launcher")
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(shareIntent)
}

private fun openExternal(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}
