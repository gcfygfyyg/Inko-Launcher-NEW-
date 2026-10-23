package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrowserTab
import com.example.ui.theme.ChromeDarkBorder
import com.example.ui.theme.ChromeDarkOmnibox
import com.example.ui.theme.ChromeDarkSurface
import com.example.ui.theme.ChromeDarkTabActive
import com.example.ui.theme.ChromeDarkTabInactive
import com.example.ui.theme.ChromeLightBorder
import com.example.ui.theme.ChromeLightOmnibox
import com.example.ui.theme.ChromeLightSurface
import com.example.ui.theme.ChromeLightTabActive
import com.example.ui.theme.ChromeLightTabInactive
import com.example.ui.theme.ChromeLightTextMuted
import com.example.ui.theme.ChromeLightTextPrimary
import com.example.ui.theme.ChromeLightTextSecondary
import com.example.ui.theme.ChromeTextMuted
import com.example.ui.theme.ChromeTextPrimary
import com.example.ui.theme.ChromeTextSecondary
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleBlueLight

@Composable
fun TopTabStrip(
    tabs: List<BrowserTab>,
    activeTabId: String,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: () -> Unit,
    onExitFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stripBg = ChromeDarkSurface
    val stripBorder = ChromeDarkBorder
    val addBtnBg = ChromeDarkOmnibox
    val brandBlue = GoogleBlue
    val textSecondary = ChromeTextSecondary

    Surface(
        color = stripBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, stripBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Horizontal scrollable Chrome tabs
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEach { tab ->
                    val isActive = tab.id == activeTabId
                    ChromeHorizontalTab(
                        tab = tab,
                        isActive = isActive,
                        onSelect = { onSelectTab(tab.id) },
                        onClose = { onCloseTab(tab.id) }
                    )
                }

                // New tab button "+"
                Surface(
                    onClick = onNewTab,
                    shape = RoundedCornerShape(8.dp),
                    color = addBtnBg,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("btn_top_strip_new_tab")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Tab",
                            tint = brandBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Exit fullscreen button (moves tabs back to right)
            IconButton(
                onClick = onExitFullscreen,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("btn_top_strip_exit_fullscreen")
            ) {
                Icon(
                    imageVector = Icons.Default.FullscreenExit,
                    contentDescription = "Exit Fullscreen",
                    tint = textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ChromeHorizontalTab(
    tab: BrowserTab,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    val tabBg = if (isActive) ChromeDarkTabActive else ChromeDarkTabInactive
    val tabBorder = if (isActive) GoogleBlue else ChromeDarkBorder
    val titleColor = if (isActive) GoogleBlue else ChromeTextPrimary
    val closeIconTint = if (isActive) ChromeTextSecondary else ChromeTextMuted

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
        color = tabBg,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = tabBorder
        ),
        modifier = Modifier
            .height(28.dp)
            .width(130.dp)
            .testTag("top_horizontal_tab_${tab.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (tab.isLoading) "⏳" else "🌐",
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tab.title.ifBlank { tab.url.ifBlank { "New Tab" } },
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = titleColor,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Tab",
                    tint = closeIconTint,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
