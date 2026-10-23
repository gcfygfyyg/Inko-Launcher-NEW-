package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.BookmarkItem
import com.example.data.model.RecentWebsite
import com.example.data.model.SearchEngine
import com.example.data.model.WidgetConfig
import com.example.data.model.WidgetType
import com.example.data.model.extractDomain
import org.json.JSONArray
import org.json.JSONObject

class LauncherPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pc_kool_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SEARCH_ENGINE = "search_engine"
        private const val KEY_STICKY_NOTE = "sticky_note"
        private const val KEY_PINNED_APPS = "pinned_apps"
        private const val KEY_BOOKMARKS = "bookmarks"
        private const val KEY_RECENT_WEBSITES = "recent_websites"
        private const val KEY_WIDGET_CONFIGS = "widget_configs"
        private const val KEY_TEMP_UNIT_FAHRENHEIT = "temp_unit_f"
        private const val KEY_LAST_URL = "last_url"
        private const val KEY_HALLOWEEN_MODE = "halloween_mode"
    }

    fun getSearchEngine(): SearchEngine {
        val name = prefs.getString(KEY_SEARCH_ENGINE, SearchEngine.GOOGLE.name)
        return try {
            SearchEngine.valueOf(name ?: SearchEngine.GOOGLE.name)
        } catch (_: Exception) {
            SearchEngine.GOOGLE
        }
    }

    fun setSearchEngine(engine: SearchEngine) {
        prefs.edit().putString(KEY_SEARCH_ENGINE, engine.name).apply()
    }

    fun getStickyNote(): String {
        return prefs.getString(
            KEY_STICKY_NOTE,
            "💡 Welcome to INKO Launcher!\n• Search any app or website from the top bar\n• Set as your default phone launcher!"
        ) ?: ""
    }

    fun setStickyNote(text: String) {
        prefs.edit().putString(KEY_STICKY_NOTE, text).apply()
    }

    fun getPinnedAppPackages(): Set<String> {
        return prefs.getStringSet(
            KEY_PINNED_APPS,
            setOf("com.android.chrome", "com.google.android.youtube", "com.google.android.apps.maps")
        ) ?: emptySet()
    }

    fun togglePinApp(packageName: String): Boolean {
        val current = getPinnedAppPackages().toMutableSet()
        val isNowPinned = if (current.contains(packageName)) {
            current.remove(packageName)
            false
        } else {
            current.add(packageName)
            true
        }
        prefs.edit().putStringSet(KEY_PINNED_APPS, current).apply()
        return isNowPinned
    }

    fun isAppPinned(packageName: String): Boolean {
        return getPinnedAppPackages().contains(packageName)
    }

    fun getBookmarks(): List<BookmarkItem> {
        val jsonStr = prefs.getString(KEY_BOOKMARKS, null)
        if (jsonStr.isNullOrEmpty()) {
            return emptyList()
        }
        return try {
            val list = mutableListOf<BookmarkItem>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    BookmarkItem(
                        id = obj.optString("id", i.toString()),
                        title = obj.optString("title", "Link"),
                        url = obj.optString("url", "https://google.com"),
                        category = obj.optString("category", "Favorites"),
                        iconEmoji = obj.optString("iconEmoji", "🌐"),
                        isSpeedDial = obj.optBoolean("isSpeedDial", true)
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveBookmarks(bookmarks: List<BookmarkItem>) {
        val array = JSONArray()
        for (item in bookmarks) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("url", item.url)
                put("category", item.category)
                put("iconEmoji", item.iconEmoji)
                put("isSpeedDial", item.isSpeedDial)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_BOOKMARKS, array.toString()).apply()
    }

    fun addBookmark(item: BookmarkItem) {
        val current = getBookmarks().toMutableList()
        current.removeAll { it.url == item.url }
        current.add(0, item)
        saveBookmarks(current)
    }

    fun removeBookmark(id: String) {
        val current = getBookmarks().toMutableList()
        current.removeAll { it.id == id }
        saveBookmarks(current)
    }

    fun getRecentWebsites(): List<RecentWebsite> {
        val jsonStr = prefs.getString(KEY_RECENT_WEBSITES, null) ?: return emptyList()
        return try {
            val list = mutableListOf<RecentWebsite>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val url = obj.optString("url", "")
                if (url.isNotBlank() && url != "about:blank" && url != "chrome://newtab") {
                    list.add(
                        RecentWebsite(
                            url = url,
                            title = obj.optString("title", extractDomain(url)),
                            domain = obj.optString("domain", extractDomain(url)),
                            timestamp = obj.optLong("timestamp", 0L)
                        )
                    )
                }
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addRecentWebsite(url: String, title: String) {
        if (url.isBlank() || url == "about:blank" || url == "chrome://newtab") return
        val current = getRecentWebsites().toMutableList()
        current.removeAll {
            it.url.equals(url, ignoreCase = true) ||
            extractDomain(it.url).equals(extractDomain(url), ignoreCase = true)
        }
        val cleanTitle = if (title.isNotBlank() && title != "New Tab" && !title.startsWith("http")) {
            title
        } else {
            extractDomain(url)
        }
        current.add(
            0,
            RecentWebsite(
                url = url,
                title = cleanTitle,
                domain = extractDomain(url),
                timestamp = System.currentTimeMillis()
            )
        )
        val trimmed = current.take(12)
        val array = JSONArray()
        for (item in trimmed) {
            val obj = JSONObject().apply {
                put("url", item.url)
                put("title", item.title)
                put("domain", item.domain)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_RECENT_WEBSITES, array.toString()).apply()
    }

    fun removeRecentWebsite(url: String) {
        val current = getRecentWebsites().toMutableList()
        current.removeAll { it.url == url }
        val array = JSONArray()
        for (item in current) {
            val obj = JSONObject().apply {
                put("url", item.url)
                put("title", item.title)
                put("domain", item.domain)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_RECENT_WEBSITES, array.toString()).apply()
    }

    fun clearRecentWebsites() {
        prefs.edit().remove(KEY_RECENT_WEBSITES).apply()
    }

    fun getWidgetConfigs(): List<WidgetConfig> {
        val jsonStr = prefs.getString(KEY_WIDGET_CONFIGS, null)
        if (jsonStr.isNullOrEmpty()) {
            return getDefaultWidgetConfigs()
        }
        return try {
            val list = mutableListOf<WidgetConfig>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val typeName = obj.optString("type")
                val type = WidgetType.values().find { it.name == typeName } ?: continue
                list.add(
                    WidgetConfig(
                        type = type,
                        isVisible = obj.optBoolean("isVisible", true),
                        order = obj.optInt("order", i)
                    )
                )
            }
            if (list.isEmpty()) getDefaultWidgetConfigs() else list
        } catch (_: Exception) {
            getDefaultWidgetConfigs()
        }
    }

    fun saveWidgetConfigs(configs: List<WidgetConfig>) {
        val array = JSONArray()
        configs.forEachIndexed { index, config ->
            val obj = JSONObject().apply {
                put("type", config.type.name)
                put("isVisible", config.isVisible)
                put("order", index)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_WIDGET_CONFIGS, array.toString()).apply()
    }

    private fun getDefaultWidgetConfigs(): List<WidgetConfig> {
        return listOf(
            WidgetConfig(WidgetType.CLOCK_HARDWARE, isVisible = true, order = 0),
            WidgetConfig(WidgetType.WEATHER_AMBIENT, isVisible = true, order = 1),
            WidgetConfig(WidgetType.SPEED_DIAL, isVisible = true, order = 2),
            WidgetConfig(WidgetType.STICKY_NOTE, isVisible = true, order = 3),
            WidgetConfig(WidgetType.SYSTEM_TOGGLES, isVisible = true, order = 4),
            WidgetConfig(WidgetType.RECENT_APPS, isVisible = true, order = 5)
        )
    }

    fun isFahrenheit(): Boolean = prefs.getBoolean(KEY_TEMP_UNIT_FAHRENHEIT, false)
    fun setFahrenheit(isF: Boolean) = prefs.edit().putBoolean(KEY_TEMP_UNIT_FAHRENHEIT, isF).apply()

    fun getLastUrl(): String = prefs.getString(KEY_LAST_URL, "") ?: ""
    fun setLastUrl(url: String) = prefs.edit().putString(KEY_LAST_URL, url).apply()

    fun isHalloweenMode(): Boolean = prefs.getBoolean(KEY_HALLOWEEN_MODE, false)
    fun setHalloweenMode(enabled: Boolean) = prefs.edit().putBoolean(KEY_HALLOWEEN_MODE, enabled).apply()
}
