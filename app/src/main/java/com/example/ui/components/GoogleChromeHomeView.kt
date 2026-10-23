package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppItem
import com.example.data.model.RecentWebsite
import com.example.ui.theme.ChromeDarkBg
import com.example.ui.theme.ChromeDarkBorder
import com.example.ui.theme.ChromeDarkOmnibox
import com.example.ui.theme.ChromeLightBg
import com.example.ui.theme.ChromeLightBorder
import com.example.ui.theme.ChromeLightOmnibox
import com.example.ui.theme.ChromeLightTextMuted
import com.example.ui.theme.ChromeLightTextPrimary
import com.example.ui.theme.ChromeLightTextSecondary
import com.example.ui.theme.ChromeTextMuted
import com.example.ui.theme.ChromeTextPrimary
import com.example.ui.theme.ChromeTextSecondary
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleBlueLight
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoogleChromeHomeView(
    query: String,
    onQueryChange: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit = {},
    recentWebsites: List<RecentWebsite>,
    pinnedApps: List<AppItem>,
    onLaunchApp: (AppItem) -> Unit,
    onOpenAllApps: () -> Unit,
    isHalloweenMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val homeBg = if (isHalloweenMode) Color.Transparent else ChromeDarkBg
    val textPrimary = ChromeTextPrimary
    val textSecondary = ChromeTextSecondary
    val textMuted = ChromeTextMuted
    val omniboxBg = if (isHalloweenMode) {
        Color.Black.copy(alpha = 0.8f)
    } else {
        ChromeDarkOmnibox
    }
    val omniboxBorder = if (isHalloweenMode) {
        Color(0xFFFF7518).copy(alpha = 0.7f)
    } else {
        ChromeDarkBorder
    }
    val shortcutBg = ChromeDarkOmnibox
    val shortcutBorder = ChromeDarkBorder
    val brandBlue = GoogleBlue

    val isUrlInput = query.startsWith("http://") || query.startsWith("https://") || (query.contains(".") && !query.contains(" "))

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                onQueryChange(spokenText)
                onNavigate(spokenText)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(homeBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        // Iconic Google Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Text(
                text = "G",
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = GoogleBlue,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "o",
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = GoogleRed,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "o",
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = GoogleYellow,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "g",
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = GoogleBlue,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "l",
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = GoogleGreen,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "e",
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = GoogleRed,
                fontFamily = FontFamily.SansSerif
            )
        }

        // Fully functional Chrome NTP Center Search Bar
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = omniboxBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, omniboxBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("chrome_ntp_search_bar")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = textSecondary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text(
                            text = "Search or type URL...",
                            color = textMuted,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary,
                        cursorColor = brandBlue,
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
                        .testTag("chrome_ntp_search_input")
                )

                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            try {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Search Google or type URL...")
                                }
                                voiceLauncher.launch(intent)
                            } catch (_: Exception) {
                                onFocusChange(true)
                            }
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice",
                            tint = brandBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // MOST RECENT WEBSITES SECTION
        if (recentWebsites.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp, start = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = brandBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Recent Websites",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textSecondary
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalArrangement = Arrangement.spacedBy(18.dp),
                maxItemsInEachRow = 4
            ) {
                recentWebsites.take(8).forEach { site ->
                    ChromeRecentWebsiteTile(
                        site = site,
                        backgroundColor = shortcutBg,
                        borderColor = shortcutBorder,
                        textColor = textPrimary,
                        onClick = { onNavigate(site.url) }
                    )
                }

                // Pinned phone app shortcut (if any)
                pinnedApps.firstOrNull()?.let { app ->
                    ChromeShortcutTile(
                        badgeText = "📱",
                        label = app.label,
                        backgroundColor = shortcutBg,
                        borderColor = shortcutBorder,
                        textColor = textPrimary,
                        onClick = { onLaunchApp(app) }
                    )
                }

                // All Phone Apps shortcut
                ChromeShortcutTile(
                    badgeText = "🚀",
                    label = "All Apps",
                    backgroundColor = shortcutBg,
                    borderColor = shortcutBorder,
                    textColor = textPrimary,
                    onClick = onOpenAllApps
                )
            }
        } else {
            // When no websites have been visited yet
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 12.dp, bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = textMuted.copy(alpha = 0.5f),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recent websites you visit will appear here",
                    fontSize = 12.sp,
                    color = textMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pinnedApps.firstOrNull()?.let { app ->
                        ChromeShortcutTile(
                            badgeText = "📱",
                            label = app.label,
                            backgroundColor = shortcutBg,
                            borderColor = shortcutBorder,
                            textColor = textPrimary,
                            onClick = { onLaunchApp(app) }
                        )
                    }
                    ChromeShortcutTile(
                        badgeText = "🚀",
                        label = "All Apps",
                        backgroundColor = shortcutBg,
                        borderColor = shortcutBorder,
                        textColor = textPrimary,
                        onClick = onOpenAllApps
                    )
                }
            }
        }
    }
}

@Composable
private fun ChromeRecentWebsiteTile(
    site: RecentWebsite,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    val initial = site.title.trim().firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString()
        ?: site.domain.firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString()
        ?: "🌐"

    val badgeColor = when (Math.abs(site.domain.hashCode()) % 4) {
        0 -> GoogleBlue
        1 -> GoogleRed
        2 -> GoogleGreen
        else -> GoogleYellow
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable { onClick() }
            .testTag("chrome_recent_${site.domain}")
    ) {
        Surface(
            shape = CircleShape,
            color = backgroundColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
            modifier = Modifier.size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = initial,
                    color = badgeColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = site.title.ifBlank { site.domain },
            fontSize = 12.sp,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ChromeShortcutTile(
    badgeText: String,
    label: String,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick() }
            .testTag("chrome_shortcut_$label")
    ) {
        Surface(
            shape = CircleShape,
            color = backgroundColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
            modifier = Modifier.size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = badgeText, fontSize = 22.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
