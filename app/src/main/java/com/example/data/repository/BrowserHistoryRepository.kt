package com.example.data.repository

import com.example.data.local.BrowserHistoryDao
import com.example.data.local.BrowserHistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BrowserHistoryRepository(private val dao: BrowserHistoryDao) {

    val recentHistory: Flow<List<BrowserHistoryItem>> = dao.getRecentHistory(30)

    fun searchHistory(query: String): Flow<List<BrowserHistoryItem>> = dao.searchHistory(query)

    suspend fun recordVisit(url: String, title: String = "") = withContext(Dispatchers.IO) {
        val trimmedUrl = url.trim()
        if (trimmedUrl.isBlank() ||
            trimmedUrl == "about:blank" ||
            trimmedUrl == "chrome://newtab" ||
            trimmedUrl.startsWith("javascript:")
        ) {
            return@withContext
        }

        val cleanTitle = if (title.isNotBlank()) title else sanitizeUrlToTitle(trimmedUrl)
        val existing = dao.getByUrl(trimmedUrl)

        if (existing != null) {
            dao.insertOrUpdate(
                existing.copy(
                    title = if (title.isNotBlank()) title else existing.title,
                    timestamp = System.currentTimeMillis(),
                    visitCount = existing.visitCount + 1
                )
            )
        } else {
            dao.insertOrUpdate(
                BrowserHistoryItem(
                    url = trimmedUrl,
                    title = cleanTitle,
                    timestamp = System.currentTimeMillis(),
                    visitCount = 1
                )
            )
        }
    }

    suspend fun deleteHistory(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        dao.clearAll()
    }

    private fun sanitizeUrlToTitle(url: String): String {
        return try {
            val uri = android.net.Uri.parse(url)
            val host = uri.host ?: url
            host.removePrefix("www.")
        } catch (_: Exception) {
            url
        }
    }
}
