package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BrowserHistoryItem
import com.example.data.model.AppItem
import com.example.data.model.BookmarkItem
import com.example.data.model.SearchEngine
import com.example.ui.theme.ChromeDarkBorder
import com.example.ui.theme.ChromeDarkOmnibox
import com.example.ui.theme.ChromeDarkOmniboxHover
import com.example.ui.theme.ChromeDarkSurface
import com.example.ui.theme.ChromeTextMuted
import com.example.ui.theme.ChromeTextPrimary
import com.example.ui.theme.ChromeTextSecondary
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChromeSearchSuggestionsDropdown(
    query: String,
    onNavigate: (String) -> Unit,
    matchingApps: List<AppItem>,
    onLaunchApp: (AppItem) -> Unit,
    onTogglePinApp: (AppItem) -> Unit,
    matchingBookmarks: List<BookmarkItem>,
    suggestedQueries: List<String>,
    mathResult: String?,
    historyItems: List<BrowserHistoryItem> = emptyList(),
    onDeleteHistoryItem: (Long) -> Unit = {},
    onClearAllHistory: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isUrlInput = query.startsWith("http://") || query.startsWith("https://") || (query.contains(".") && !query.contains(" "))

    Surface(
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        color = ChromeDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ChromeDarkBorder),
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .heightIn(max = 450.dp)
            .testTag("chrome_suggestions_dropdown")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Math Calculation Result (if applicable)
            if (mathResult != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ChromeDarkOmnibox,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onNavigate(mathResult.removePrefix("= ").trim()) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = GoogleYellow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Calculator Result", fontSize = 11.sp, color = ChromeTextMuted)
                                Text(
                                    text = mathResult,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoogleYellow,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // Top Search / Open URL action row (when query is entered)
            if (query.isNotBlank()) {
                item {
                    Surface(
                        onClick = { onNavigate(query) },
                        shape = RoundedCornerShape(8.dp),
                        color = ChromeDarkOmnibox,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(
                                imageVector = if (isUrlInput) Icons.Default.OpenInNew else Icons.Default.Search,
                                contentDescription = null,
                                tint = GoogleBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isUrlInput) "Open URL" else "Search Google",
                                    fontSize = 11.sp,
                                    color = GoogleBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = query,
                                    fontSize = 13.sp,
                                    color = ChromeTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = ChromeTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // PERSISTENT WEB BROWSING HISTORY SECTION
            if (historyItems.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 4.dp, top = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = GoogleBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (query.isBlank()) "RECENT BROWSING HISTORY" else "MATCHING HISTORY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChromeTextSecondary,
                                fontFamily = FontFamily.SansSerif
                            )
                        }

                        TextButton(
                            onClick = onClearAllHistory,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                            modifier = Modifier.height(24.dp).testTag("btn_clear_history")
                        ) {
                            Text(
                                text = "Clear",
                                fontSize = 11.sp,
                                color = GoogleRed,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                items(historyItems, key = { it.id }) { historyItem ->
                    Surface(
                        onClick = { onNavigate(historyItem.url) },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("history_item_${historyItem.id}")
                            .testTag("omnibar_history_${historyItem.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            // Circular icon badge
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(ChromeDarkOmnibox)
                                    .border(1.dp, ChromeDarkBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = ChromeTextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = historyItem.title.ifBlank { historyItem.url },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ChromeTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 1.dp)
                                ) {
                                    Text(
                                        text = formatHost(historyItem.url),
                                        fontSize = 11.sp,
                                        color = GoogleBlue,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    Text(
                                        text = " • ${formatHistoryTime(historyItem.timestamp)}",
                                        fontSize = 10.sp,
                                        color = ChromeTextMuted,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Delete single history entry button
                            IconButton(
                                onClick = { onDeleteHistoryItem(historyItem.id) },
                                modifier = Modifier
                                    .size(26.dp)
                                    .testTag("btn_delete_history_${historyItem.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove from history",
                                    tint = ChromeTextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Empty state when omnibar focused with empty query and no browsing history
            if (query.isBlank() && historyItems.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = ChromeTextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No recent browsing history",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ChromeTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Websites you visit will be saved locally and appear here",
                            fontSize = 11.sp,
                            color = ChromeTextMuted
                        )
                    }
                }
            }

            // Matching Installed Apps
            if (matchingApps.isNotEmpty() && query.isNotBlank()) {
                item {
                    Text(
                        text = "📱 INSTALLED APPS (${matchingApps.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChromeTextSecondary,
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp),
                        fontFamily = FontFamily.SansSerif
                    )
                }

                items(matchingApps.take(5)) { app ->
                    Surface(
                        onClick = { onLaunchApp(app) },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ChromeDarkOmnibox)
                                    .border(1.dp, ChromeDarkBorder, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = app.label.take(1).uppercase(),
                                    color = GoogleBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ChromeTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = app.category,
                                    fontSize = 10.sp,
                                    color = ChromeTextMuted
                                )
                            }

                            IconButton(
                                onClick = { onTogglePinApp(app) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (app.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                    contentDescription = "Pin",
                                    tint = if (app.isPinned) GoogleBlue else ChromeTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Button(
                                onClick = { onLaunchApp(app) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoogleBlue.copy(alpha = 0.2f),
                                    contentColor = GoogleBlue
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Open", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Matching Bookmarks
            val matchingBookmarksFiltered = matchingBookmarks.filter {
                query.isNotBlank() && (it.title.contains(query, true) || it.url.contains(query, true))
            }
            if (matchingBookmarksFiltered.isNotEmpty()) {
                item {
                    Text(
                        text = "🔖 SAVED LINKS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChromeTextSecondary,
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp)
                    )
                }

                items(matchingBookmarksFiltered.take(3)) { bookmark ->
                    Surface(
                        onClick = { onNavigate(bookmark.url) },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = bookmark.iconEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = bookmark.title, fontSize = 13.sp, color = ChromeTextPrimary, maxLines = 1)
                                Text(text = bookmark.url, fontSize = 11.sp, color = ChromeTextMuted, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // Suggested Search Queries
            if (suggestedQueries.isNotEmpty() && query.isNotBlank()) {
                item {
                    Text(
                        text = "🔍 GOOGLE SUGGESTIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChromeTextSecondary,
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp)
                    )
                }

                items(suggestedQueries.take(4)) { suggestion ->
                    Surface(
                        onClick = { onNavigate(suggestion) },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = ChromeTextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = suggestion,
                                fontSize = 13.sp,
                                color = ChromeTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatHistoryTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diffMs = now - timestamp
    val diffSec = diffMs / 1000
    val diffMin = diffSec / 60
    val diffHours = diffMin / 60
    val diffDays = diffHours / 24

    return when {
        diffMin < 1 -> "Just now"
        diffMin < 60 -> "${diffMin}m ago"
        diffHours < 24 -> "${diffHours}h ago"
        diffDays == 1L -> "Yesterday"
        diffDays < 7 -> "${diffDays}d ago"
        else -> {
            val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

private fun formatHost(url: String): String {
    return try {
        val uri = android.net.Uri.parse(url)
        val host = uri.host ?: url
        host.removePrefix("www.")
    } catch (_: Exception) {
        url
    }
}
