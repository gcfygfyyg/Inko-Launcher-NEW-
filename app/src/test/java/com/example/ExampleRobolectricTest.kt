package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.SearchEngine
import com.example.data.repository.BrowserHistoryRepository
import com.example.data.repository.LauncherPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("INKO Launcher", appName)
  }

  @Test
  fun `launcher preferences defaults and recent websites test`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val preferences = LauncherPreferences(context)

    assertEquals(SearchEngine.GOOGLE, preferences.getSearchEngine())
    assertNotNull(preferences.getStickyNote())
    
    // Recent websites persistence
    preferences.addRecentWebsite("https://news.ycombinator.com", "Hacker News")
    val recent = preferences.getRecentWebsites()
    assertTrue(recent.isNotEmpty())
    assertEquals("https://news.ycombinator.com", recent[0].url)
    assertEquals("Hacker News", recent[0].title)
  }

  @Test
  fun `halloween mode toggle and persistence test`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val preferences = LauncherPreferences(context)

    assertEquals(false, preferences.isHalloweenMode())
    preferences.setHalloweenMode(true)
    assertEquals(true, preferences.isHalloweenMode())
    preferences.setHalloweenMode(false)
    assertEquals(false, preferences.isHalloweenMode())
  }

  @Test
  fun `launcher preferences and note test`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val preferences = LauncherPreferences(context)

    assertTrue(preferences.getStickyNote().contains("INKO Launcher"))
    assertEquals(false, preferences.isHalloweenMode())
  }

  @Test
  fun `persistent browsing history save and retrieval test`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val inMemoryDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    val dao = inMemoryDb.browserHistoryDao()
    val repository = BrowserHistoryRepository(dao)

    // Initially empty
    val initialHistory = repository.recentHistory.first()
    assertTrue(initialHistory.isEmpty())

    // Record visit to a URL
    repository.recordVisit("https://developer.android.com", "Android Developers")
    repository.recordVisit("https://kotlinlang.org", "Kotlin Programming")

    val updatedHistory = repository.recentHistory.first()
    assertEquals(2, updatedHistory.size)
    assertEquals("https://kotlinlang.org", updatedHistory[0].url)
    assertEquals("Kotlin Programming", updatedHistory[0].title)

    // Record repeat visit updates timestamp and count
    repository.recordVisit("https://kotlinlang.org", "Kotlin Programming")
    val reUpdatedHistory = repository.recentHistory.first()
    assertEquals(2, reUpdatedHistory.size)
    assertEquals(2, reUpdatedHistory[0].visitCount)

    // Delete single item
    repository.deleteHistory(reUpdatedHistory[0].id)
    val afterDelete = repository.recentHistory.first()
    assertEquals(1, afterDelete.size)
    assertEquals("https://developer.android.com", afterDelete[0].url)

    // Clear all
    repository.clearAllHistory()
    val afterClear = repository.recentHistory.first()
    assertTrue(afterClear.isEmpty())

    inMemoryDb.close()
  }
}
