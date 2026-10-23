package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BrowserHistoryDao {

    @Query("SELECT * FROM browser_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentHistory(limit: Int = 30): Flow<List<BrowserHistoryItem>>

    @Query("SELECT * FROM browser_history WHERE title LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%' ORDER BY timestamp DESC LIMIT 20")
    fun searchHistory(query: String): Flow<List<BrowserHistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: BrowserHistoryItem): Long

    @Query("SELECT * FROM browser_history WHERE url = :url LIMIT 1")
    suspend fun getByUrl(url: String): BrowserHistoryItem?

    @Query("DELETE FROM browser_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM browser_history")
    suspend fun clearAll()
}
