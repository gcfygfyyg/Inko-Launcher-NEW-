package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppItem
import com.example.data.model.BookmarkItem
import com.example.data.model.SearchEngine
import com.example.ui.theme.KoolBorder
import com.example.ui.theme.KoolBorderGlow
import com.example.ui.theme.KoolCyan
import com.example.ui.theme.KoolGreen
import com.example.ui.theme.KoolSurfaceCard
import com.example.ui.theme.KoolSurfaceDark
import com.example.ui.theme.KoolSurfaceElevated
import com.example.ui.theme.KoolTextMuted
import com.example.ui.theme.KoolTextPrimary
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.TextButton
import com.example.data.local.BrowserHistoryItem
import com.example.ui.theme.KoolTextSecondary
import com.example.ui.theme.KoolYellow
import com.example.ui.theme.KoolRed

@Composable
fun OmniBar(
    query: String,
    onQueryChange: (String) -> Unit,
    searchEngine: SearchEngine,
    onSearchEngineChange: (SearchEngine) -> Unit,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
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
    val focusManager = LocalFocusManager.current
    var showEngineMenu by remember { mutableStateOf(false) }

    val isUrlInput = query.startsWith("http://") || query.startsWith("https://") || (query.contains(".") && !query.contains(" "))

    Column(modifier = modifier.fillMaxWidth()) {
        // OmniBar input capsule
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = KoolSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(
                width = if (isFocused) 1.5.dp else 1.dp,
                color = if (isFocused) KoolCyan else KoolBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                // SSL Padlock / Protocol Indicator
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isUrlInput) KoolGreen.copy(alpha = 0.15f) else KoolSurfaceCard)
                        .clickable { showEngineMenu = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (isUrlInput) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secure Connection",
                            tint = KoolGreen,
                            modifier = Modifier.size(15.dp)
                        )
                    } else {
                        Text(
                            text = searchEngine.iconEmoji,
                            fontSize = 14.sp
                        )
                    }

                    // Search engine picker dropdown
                    DropdownMenu(
                        expanded = showEngineMenu,
                        onDismissRequest = { showEngineMenu = false },
                        modifier = Modifier.background(KoolSurfaceElevated)
                    ) {
                        SearchEngine.values().forEach { engine ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = engine.iconEmoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = engine.displayName,
                                            color = if (engine == searchEngine) KoolCyan else KoolTextPrimary,
                                            fontWeight = if (engine == searchEngine) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    onSearchEngineChange(engine)
                                    showEngineMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text Field Input
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text(
                            text = "Search apps or type URL / web query...",
                            color = KoolTextMuted,
                            fontSize = 13.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = KoolTextPrimary,
                        unfocusedTextColor = KoolTextPrimary,
                        cursorColor = KoolCyan,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (isUrlInput) KeyboardType.Uri else KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (query.isNotBlank()) {
                                onNavigate(query)
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { onFocusChange(it.isFocused) }
                        .testTag("omnibar_input")
                )

                // Actions: Clear button
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = KoolTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Go / Search button
                IconButton(
                    onClick = {
                        if (query.isNotBlank()) {
                            onNavigate(query)
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (query.isNotBlank()) KoolCyan else Color.Transparent)
                        .testTag("btn_omnibar_go")
                ) {
                    Icon(
                        imageVector = if (isUrlInput) Icons.Default.ArrowForward else Icons.Default.Search,
                        contentDescription = "Navigate or Search",
                        tint = if (query.isNotBlank()) Color(0xFF00363D) else KoolTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Live Dropdown / Search Results Panel when query is active or omnibar is focused
        AnimatedVisibility(
            visible = isFocused || query.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = KoolSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, KoolBorderGlow),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .heightIn(max = 380.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // Math Result if detected
                    if (mathResult != null) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = KoolSurfaceElevated,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        onQueryChange(mathResult.removePrefix("= ").trim())
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = "Calculation",
                                        tint = KoolYellow,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = "Calculator Instant Answer", fontSize = 11.sp, color = KoolTextMuted)
                                        Text(
                                            text = mathResult,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = KoolYellow,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Direct Web Navigation Action
                    if (query.isNotBlank()) {
                        item {
                            Surface(
                                onClick = {
                                    onNavigate(query)
                                    focusManager.clearFocus()
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = KoolSurfaceCard,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .testTag("omnibar_action_web_search")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isUrlInput) Icons.Default.OpenInNew else Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = KoolCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isUrlInput) "Open Website" else "Search ${searchEngine.displayName}",
                                            fontSize = 11.sp,
                                            color = KoolCyan,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = if (isUrlInput) "https://$query" else "\"$query\"",
                                            fontSize = 13.sp,
                                            color = KoolTextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = KoolTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
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
                                    tint = KoolTextMuted,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No recent browsing history",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = KoolTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Websites you visit will be saved locally and appear here",
                                    fontSize = 11.sp,
                                    color = KoolTextMuted
                                )
                            }
                        }
                    }

                    // Persistent Browsing History Section
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
                                        tint = KoolCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (query.isBlank()) "RECENT BROWSING HISTORY" else "MATCHING HISTORY",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KoolTextSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                TextButton(
                                    onClick = onClearAllHistory,
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                    modifier = Modifier.height(24.dp).testTag("btn_clear_history_omnibar")
                                ) {
                                    Text(
                                        text = "Clear",
                                        fontSize = 11.sp,
                                        color = KoolRed,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        items(historyItems, key = { "hist_${it.id}" }) { item ->
                            Surface(
                                onClick = {
                                    onNavigate(item.url)
                                    focusManager.clearFocus()
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("omnibar_history_${item.id}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(KoolSurfaceElevated),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = KoolCyan,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title.ifBlank { item.url },
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = KoolTextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = item.url,
                                            fontSize = 11.sp,
                                            color = KoolTextMuted,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteHistoryItem(item.id) },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove from history",
                                            tint = KoolTextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Matching Apps Section
                    if (matchingApps.isNotEmpty()) {
                        item {
                            Text(
                                text = "📱 INSTALLED APPS (${matchingApps.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = KoolTextSecondary,
                                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp),
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        items(matchingApps.take(6)) { app ->
                            AppSearchResultRow(
                                app = app,
                                onLaunch = {
                                    onLaunchApp(app)
                                    focusManager.clearFocus()
                                },
                                onTogglePin = { onTogglePinApp(app) }
                            )
                        }
                    }

                    // Matching Bookmarks Section
                    val matchingBookmarksFiltered = matchingBookmarks.filter {
                        it.title.contains(query, true) || it.url.contains(query, true)
                    }
                    if (matchingBookmarksFiltered.isNotEmpty()) {
                        item {
                            Text(
                                text = "🔖 SAVED LINKS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = KoolTextSecondary,
                                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp),
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        items(matchingBookmarksFiltered.take(4)) { bookmark ->
                            Surface(
                                onClick = {
                                    onNavigate(bookmark.url)
                                    focusManager.clearFocus()
                                },
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
                                        Text(text = bookmark.title, fontSize = 13.sp, color = KoolTextPrimary, maxLines = 1)
                                        Text(text = bookmark.url, fontSize = 11.sp, color = KoolTextMuted, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }

                    // Search Queries Autocomplete suggestions
                    if (suggestedQueries.isNotEmpty()) {
                        item {
                            Text(
                                text = "🔍 WEB SUGGESTIONS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = KoolTextSecondary,
                                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp),
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        items(suggestedQueries.take(4)) { suggestion ->
                            Surface(
                                onClick = {
                                    onNavigate(suggestion)
                                    focusManager.clearFocus()
                                },
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
                                        tint = KoolTextMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = suggestion,
                                        fontSize = 13.sp,
                                        color = KoolTextPrimary,
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
    }
}

@Composable
private fun AppSearchResultRow(
    app: AppItem,
    onLaunch: () -> Unit,
    onTogglePin: () -> Unit
) {
    Surface(
        onClick = onLaunch,
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_result_${app.packageName}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // App Icon Avatar
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(KoolSurfaceElevated)
                    .border(1.dp, KoolBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.label.take(1).uppercase(),
                    color = KoolCyan,
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
                    color = KoolTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.category,
                    fontSize = 10.sp,
                    color = KoolTextMuted
                )
            }

            // Pin button
            IconButton(
                onClick = onTogglePin,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (app.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                    contentDescription = "Pin to Home",
                    tint = if (app.isPinned) KoolCyan else KoolTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Launch button
            Button(
                onClick = onLaunch,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KoolCyan.copy(alpha = 0.2f),
                    contentColor = KoolCyan
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Open",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "Open", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
