package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BrowserHistoryItem
import com.example.data.model.extractDomain
import com.example.ui.theme.ChromeDarkBg
import com.example.ui.theme.ChromeDarkBorder
import com.example.ui.theme.ChromeDarkOmnibox
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
fun CustomNoInternetView(
    failedUrl: String,
    cachedHistory: List<BrowserHistoryItem>,
    onRetry: () -> Unit,
    onGoHome: () -> Unit,
    onOpenCachedUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var searchQuery by remember { mutableStateOf("") }
    var copyNotice by remember { mutableStateOf<String?>(null) }

    val filteredHistory = remember(cachedHistory, searchQuery) {
        if (searchQuery.isBlank()) {
            cachedHistory
        } else {
            val q = searchQuery.trim().lowercase()
            cachedHistory.filter {
                it.title.lowercase().contains(q) || it.url.lowercase().contains(q)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ChromeDarkBg)
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .testTag("custom_no_internet_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            // Offline Hero Icon
            Surface(
                shape = CircleShape,
                color = ChromeDarkOmnibox,
                border = androidx.compose.foundation.BorderStroke(2.dp, GoogleRed.copy(alpha = 0.5f)),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = "No Internet",
                        tint = GoogleRed,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Headline
            Text(
                text = "No Internet Connection",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ChromeTextPrimary,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "You're currently offline. The page could not be loaded, but you can browse your cached pages and history below.",
                fontSize = 13.sp,
                color = ChromeTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            if (failedUrl.isNotBlank() && failedUrl != "about:blank" && failedUrl != "chrome://newtab") {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ChromeDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ChromeDarkBorder),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "URL: ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChromeTextMuted
                        )
                        Text(
                            text = failedUrl,
                            fontSize = 11.sp,
                            color = GoogleYellow,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                    modifier = Modifier.testTag("no_internet_retry_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Try Again",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedButton(
                    onClick = onGoHome,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ChromeDarkBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ChromeTextPrimary),
                    modifier = Modifier.testTag("no_internet_home_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = ChromeTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Go to Home",
                        fontSize = 13.sp,
                        color = ChromeTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            HorizontalDivider(
                color = ChromeDarkBorder,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Cached History Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = GoogleBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cached Browsing History",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ChromeTextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ChromeDarkOmnibox,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ChromeDarkBorder)
                ) {
                    Text(
                        text = "${cachedHistory.size} saved",
                        fontSize = 11.sp,
                        color = ChromeTextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search filter for cached history
            if (cachedHistory.isNotEmpty()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search cached pages...",
                            fontSize = 12.sp,
                            color = ChromeTextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ChromeTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoogleBlue,
                        unfocusedBorderColor = ChromeDarkBorder,
                        focusedContainerColor = ChromeDarkOmnibox,
                        unfocusedContainerColor = ChromeDarkOmnibox,
                        focusedTextColor = ChromeTextPrimary,
                        unfocusedTextColor = ChromeTextPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("offline_history_search_input")
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Cached history items list
        if (filteredHistory.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ChromeDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ChromeDarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = ChromeTextMuted.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No cached pages match '$searchQuery'" else "No cached browsing history available yet",
                            fontSize = 12.sp,
                            color = ChromeTextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredHistory, key = { it.id }) { item ->
                CachedHistoryRow(
                    item = item,
                    onClick = { onOpenCachedUrl(item.url) },
                    onCopy = {
                        clipboardManager.setText(AnnotatedString(item.url))
                        copyNotice = "Copied to clipboard"
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CachedHistoryRow(
    item: BrowserHistoryItem,
    onClick: () -> Unit,
    onCopy: () -> Unit
) {
    val domain = extractDomain(item.url)
    val formattedTime = remember(item.timestamp) {
        try {
            val sdf = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
            sdf.format(Date(item.timestamp))
        } catch (_: Exception) {
            "Recent"
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ChromeDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ChromeDarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("cached_history_item_${item.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Initial Badge
            Surface(
                shape = CircleShape,
                color = ChromeDarkOmnibox,
                border = androidx.compose.foundation.BorderStroke(1.dp, ChromeDarkBorder),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = domain.firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString() ?: "🌐",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoogleBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title.ifBlank { domain },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = ChromeTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = domain,
                        fontSize = 11.sp,
                        color = ChromeTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• $formattedTime",
                        fontSize = 10.sp,
                        color = ChromeTextMuted
                    )
                }
            }

            // Quick Actions
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Link",
                    tint = ChromeTextMuted,
                    modifier = Modifier.size(15.dp)
                )
            }

            IconButton(
                onClick = onClick,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Open Cached Page",
                    tint = GoogleBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
