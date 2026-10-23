package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrowserTab
import com.example.data.model.LauncherTab
import com.example.ui.theme.ChromeDarkBorder
import com.example.ui.theme.ChromeDarkDivider
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
fun RightTabRail(
    tabs: List<BrowserTab>,
    activeTabId: String,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: () -> Unit,
    activeLauncherTab: LauncherTab,
    onSelectLauncherTab: (LauncherTab) -> Unit,
    onToggleFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val railBg = ChromeDarkSurface
    val railBorder = ChromeDarkBorder
    val addBtnBg = ChromeDarkOmnibox
    val brandBlue = GoogleBlue
    val textSecondary = ChromeTextSecondary

    Surface(
        color = railBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, railBorder),
        modifier = modifier
            .width(58.dp)
            .fillMaxHeight()
            .testTag("right_tab_rail")
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // New Tab "+" Button at the top of the right rail
            Surface(
                onClick = onNewTab,
                shape = CircleShape,
                color = addBtnBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, railBorder),
                modifier = Modifier
                    .size(40.dp)
                    .testTag("btn_right_rail_new_tab")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Tab",
                        tint = brandBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(
                color = railBorder,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Scrollable list of active tabs docked on the right
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isActive = tab.id == activeTabId && activeLauncherTab == LauncherTab.WEB_BROWSER
                    VerticalTabCard(
                        tab = tab,
                        tabIndex = index + 1,
                        isActive = isActive,
                        onSelect = { onSelectTab(tab.id) },
                        onClose = { onCloseTab(tab.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(
                color = railBorder,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Bottom Navigation & Fullscreen Switcher (moves tabs to top)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Home (New Tab Page)
                RailIconButton(
                    icon = Icons.Default.Home,
                    contentDesc = "Home NTP",
                    isSelected = activeLauncherTab == LauncherTab.DESKTOP_HOME,
                    onClick = { onSelectLauncherTab(LauncherTab.DESKTOP_HOME) }
                )

                // All Apps
                RailIconButton(
                    icon = Icons.Default.Apps,
                    contentDesc = "All Apps",
                    isSelected = activeLauncherTab == LauncherTab.ALL_APPS,
                    onClick = { onSelectLauncherTab(LauncherTab.ALL_APPS) }
                )

                // Fullscreen (Tabs go UP)
                IconButton(
                    onClick = onToggleFullscreen,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_rail_toggle_fullscreen")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Move tabs to top (Fullscreen)",
                        tint = brandBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun VerticalTabCard(
    tab: BrowserTab,
    tabIndex: Int,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    val tabBg = if (isActive) ChromeDarkTabActive else ChromeDarkTabInactive
    val brandBlue = GoogleBlue
    val tabBorder = if (isActive) brandBlue else ChromeDarkBorder
    val titleColor = if (isActive) brandBlue else ChromeTextSecondary
    val closeIconTint = if (isActive) ChromeTextSecondary else ChromeTextMuted

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(10.dp),
        color = tabBg,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isActive) 1.5.dp else 1.dp,
            color = tabBorder
        ),
        modifier = Modifier
            .width(48.dp)
            .height(58.dp)
            .testTag("vertical_tab_${tab.id}")
    ) {
        Box(modifier = Modifier.padding(4.dp)) {
            // Close 'x' at top corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(14.dp)
                    .clip(CircleShape)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Tab",
                    tint = closeIconTint,
                    modifier = Modifier.size(10.dp)
                )
            }

            // Tab icon and abbreviated title
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (tab.isLoading) "⏳" else "🌐",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tab.title.take(4).ifBlank { "Tab" },
                    fontSize = 9.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RailIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDesc: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val brandBlue = GoogleBlue
    val textSecondary = ChromeTextSecondary

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isSelected) brandBlue.copy(alpha = 0.2f) else Color.Transparent)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = if (isSelected) brandBlue else textSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}
