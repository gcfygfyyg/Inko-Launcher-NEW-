package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrowserTab
import com.example.data.model.LauncherTab
import com.example.ui.theme.KoolBorder
import com.example.ui.theme.KoolBorderGlow
import com.example.ui.theme.KoolCyan
import com.example.ui.theme.KoolSurfaceCard
import com.example.ui.theme.KoolSurfaceDark
import com.example.ui.theme.KoolSurfaceElevated
import com.example.ui.theme.KoolTextMuted
import com.example.ui.theme.KoolTextPrimary
import com.example.ui.theme.KoolTextSecondary
import com.example.ui.theme.WindowDotGreen
import com.example.ui.theme.WindowDotRed
import com.example.ui.theme.WindowDotYellow

@Composable
fun BrowserTabBar(
    activeLauncherTab: LauncherTab,
    onSelectLauncherTab: (LauncherTab) -> Unit,
    browserTabs: List<BrowserTab>,
    activeBrowserTabId: String,
    onSelectBrowserTab: (String) -> Unit,
    onCloseBrowserTab: (String) -> Unit,
    onNewBrowserTab: () -> Unit,
    onWindowRedClick: () -> Unit,
    onWindowYellowClick: () -> Unit,
    onWindowGreenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(KoolSurfaceDark)
            .padding(top = 4.dp, bottom = 6.dp)
    ) {
        // Top Window Frame Bar (Desktop OS Window style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Window Traffic Light dots
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(WindowDotRed)
                        .clickable { onWindowRedClick() }
                        .testTag("window_dot_red")
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(WindowDotYellow)
                        .clickable { onWindowYellowClick() }
                        .testTag("window_dot_yellow")
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(WindowDotGreen)
                        .clickable { onWindowGreenClick() }
                        .testTag("window_dot_green")
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Brand badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(KoolSurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(KoolCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "INKO Launcher",
                        color = KoolCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Mode Selector Pills
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NavPill(
                    icon = Icons.Default.SpaceDashboard,
                    label = "Home",
                    isSelected = activeLauncherTab == LauncherTab.DESKTOP_HOME,
                    onClick = { onSelectLauncherTab(LauncherTab.DESKTOP_HOME) }
                )
                NavPill(
                    icon = Icons.Default.Language,
                    label = "Web",
                    isSelected = activeLauncherTab == LauncherTab.WEB_BROWSER,
                    onClick = { onSelectLauncherTab(LauncherTab.WEB_BROWSER) }
                )
                NavPill(
                    icon = Icons.Default.Apps,
                    label = "Apps",
                    isSelected = activeLauncherTab == LauncherTab.ALL_APPS,
                    onClick = { onSelectLauncherTab(LauncherTab.ALL_APPS) }
                )
                NavPill(
                    icon = Icons.Default.Widgets,
                    label = "Widgets",
                    isSelected = activeLauncherTab == LauncherTab.WIDGETS_HUB,
                    onClick = { onSelectLauncherTab(LauncherTab.WIDGETS_HUB) }
                )
                NavPill(
                    icon = Icons.Default.Bookmark,
                    label = "Links",
                    isSelected = activeLauncherTab == LauncherTab.BOOKMARKS,
                    onClick = { onSelectLauncherTab(LauncherTab.BOOKMARKS) }
                )
            }
        }

        // Browser Tabs Bar (when in Web mode or showing active web session)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 8.dp, end = 8.dp)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            browserTabs.forEach { tab ->
                val isActive = tab.id == activeBrowserTabId && activeLauncherTab == LauncherTab.WEB_BROWSER
                BrowserTabItem(
                    tab = tab,
                    isActive = isActive,
                    onSelect = {
                        onSelectBrowserTab(tab.id)
                        onSelectLauncherTab(LauncherTab.WEB_BROWSER)
                    },
                    onClose = { onCloseBrowserTab(tab.id) }
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            // Add Tab Button
            Surface(
                onClick = onNewBrowserTab,
                shape = RoundedCornerShape(8.dp),
                color = KoolSurfaceElevated,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("btn_new_browser_tab")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Tab",
                        tint = KoolTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavPill(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) KoolCyan else KoolSurfaceElevated,
        contentColor = if (isSelected) Color(0xFF00363D) else KoolTextSecondary,
        modifier = Modifier
            .height(26.dp)
            .testTag("nav_pill_$label")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun BrowserTabItem(
    tab: BrowserTab,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
        color = if (isActive) KoolSurfaceCard else KoolSurfaceElevated.copy(alpha = 0.6f),
        modifier = Modifier
            .height(28.dp)
            .width(140.dp)
            .testTag("browser_tab_${tab.id}")
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
                    color = if (isActive) KoolCyan else KoolTextPrimary,
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
                    tint = if (isActive) KoolTextSecondary else KoolTextMuted,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
