package com.example.data.model

import android.graphics.drawable.Drawable

data class AppItem(
    val id: String,
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: Drawable? = null,
    val isPinned: Boolean = false,
    val category: String = "Apps",
    val launchCount: Int = 0,
    val lastLaunchedTime: Long = 0L
)

data class BrowserTab(
    val id: String,
    val title: String,
    val url: String,
    val faviconUrl: String? = null,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isDesktopMode: Boolean = false
)

data class BookmarkItem(
    val id: String,
    val title: String,
    val url: String,
    val category: String = "Popular",
    val iconEmoji: String = "🌐",
    val isSpeedDial: Boolean = true
)

data class RecentWebsite(
    val url: String,
    val title: String,
    val domain: String = extractDomain(url),
    val timestamp: Long = System.currentTimeMillis()
)

fun extractDomain(url: String): String {
    return try {
        val clean = url.removePrefix("https://").removePrefix("http://").removePrefix("www.")
        clean.substringBefore("/").substringBefore("?").ifBlank { url }
    } catch (_: Exception) {
        url
    }
}

enum class SearchEngine(val displayName: String, val searchUrl: String, val iconEmoji: String) {
    GOOGLE("Google", "https://www.google.com/search?q=", "🔍"),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=", "🦆"),
    BING("Bing", "https://www.bing.com/search?q=", "🅱️"),
    BRAVE("Brave", "https://search.brave.com/search?q=", "🦁"),
    ECOSIA("Ecosia", "https://www.ecosia.org/search?q=", "🌱"),
    WIKIPEDIA("Wikipedia", "https://en.wikipedia.org/wiki/Special:Search?search=", "📚")
}

enum class LauncherTab {
    DESKTOP_HOME,
    WEB_BROWSER,
    ALL_APPS,
    WIDGETS_HUB,
    BOOKMARKS
}

enum class WidgetType(val title: String, val iconEmoji: String) {
    CLOCK_HARDWARE("Clock & Hardware", "⏱️"),
    WEATHER_AMBIENT("Ambient Weather", "🌤️"),
    SPEED_DIAL("Speed Dial & Pinned", "⚡"),
    STICKY_NOTE("Quick Scratchpad", "📝"),
    SYSTEM_TOGGLES("System Quick Controls", "🎛️"),
    RECENT_APPS("Recent Apps", "🕒")
}

data class WidgetConfig(
    val type: WidgetType,
    val isVisible: Boolean = true,
    val order: Int = 0
)

data class WeatherInfo(
    val temperatureC: Int = 22,
    val condition: String = "Partly Cloudy",
    val location: String = "Local Station",
    val highC: Int = 25,
    val lowC: Int = 18,
    val humidity: Int = 45,
    val windKmh: Int = 14,
    val uvIndex: Int = 5,
    val iconEmoji: String = "⛅"
)

data class HardwareStats(
    val batteryPct: Int = 88,
    val isCharging: Boolean = false,
    val ramUsedMb: Long = 3450,
    val ramTotalMb: Long = 6144,
    val storageUsedGb: Double = 42.5,
    val storageTotalGb: Double = 128.0,
    val isConnected: Boolean = true,
    val networkType: String = "Wi-Fi (Fast)",
    val cpuTempC: Int = 34
)
