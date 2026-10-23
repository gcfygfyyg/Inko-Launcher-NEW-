package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
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
import com.example.ui.theme.ChromeDarkBg
import com.example.ui.theme.ChromeDarkBorder
import com.example.ui.theme.ChromeDarkDivider
import com.example.ui.theme.ChromeDarkOmnibox
import com.example.ui.theme.ChromeDarkOmniboxHover
import com.example.ui.theme.ChromeDarkSurface
import com.example.ui.theme.ChromeLightBorder
import com.example.ui.theme.ChromeLightDivider
import com.example.ui.theme.ChromeLightOmnibox
import com.example.ui.theme.ChromeLightOmniboxHover
import com.example.ui.theme.ChromeLightSurface
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

@Composable
fun ChromeTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    activeUrl: String,
    tabCount: Int,
    isFullscreen: Boolean,
    onToggleFullscreen: () -> Unit,
    onNavigate: (String) -> Unit,
    onHomeClick: () -> Unit,
    onNewTabClick: () -> Unit,
    onReloadClick: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    isDesktopMode: Boolean,
    onOpenAllApps: () -> Unit,
    onOpenDefaultLauncherSettings: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    isFocused: Boolean,
    isHalloweenMode: Boolean = false,
    onToggleHalloweenMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var showMenu by remember { mutableStateOf(false) }

    val barBg = if (isHalloweenMode) Color.Black else ChromeDarkSurface
    val textPrimary = ChromeTextPrimary
    val textSecondary = ChromeTextSecondary
    val textMuted = ChromeTextMuted
    val omniboxBg = if (isFocused) ChromeDarkOmniboxHover else ChromeDarkOmnibox
    val omniboxBorder = if (isFocused) GoogleBlue else ChromeDarkBorder
    val dividerColor = ChromeDarkDivider
    val brandBlue = GoogleBlue

    val isUrlInput = query.startsWith("http://") || query.startsWith("https://") || (query.contains(".") && !query.contains(" "))

    Surface(
        color = barBg,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Chrome Home Button
            IconButton(
                onClick = onHomeClick,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("btn_chrome_home")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Chrome Home",
                    tint = textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Authentic Chrome Pill-shaped Omnibox
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = omniboxBg,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = omniboxBorder
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .padding(horizontal = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    // Lock icon / Google G
                    if (isUrlInput || activeUrl.startsWith("https://")) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secure",
                            tint = GoogleGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        // Google "G" icon
                        Text(
                            text = "G",
                            fontWeight = FontWeight.Bold,
                            color = brandBlue,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Omnibox Text Input
                    TextField(
                        value = query,
                        onValueChange = onQueryChange,
                        placeholder = {
                            Text(
                                text = if (activeUrl.isNotBlank() && activeUrl != "https://www.google.com") {
                                    activeUrl.removePrefix("https://").removePrefix("http://")
                                } else {
                                    "Search or type URL..."
                                },
                                color = textMuted,
                                fontSize = 13.sp,
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
                            .testTag("chrome_omnibox_input")
                    )

                    // Clear button or Voice search
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { onFocusChange(true) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Search",
                                tint = brandBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Tab Count Badge (e.g. "[ 1 ]" in rounded rectangle, exactly like Chrome mobile)
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .border(
                        width = 1.5.dp,
                        color = textSecondary,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clickable { onNewTabClick() }
                    .testTag("btn_chrome_tab_count"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tabCount.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    fontFamily = FontFamily.SansSerif
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            // Fullscreen Mode Toggle ("in full screen mode it goes up")
            IconButton(
                onClick = onToggleFullscreen,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("btn_toggle_fullscreen")
            ) {
                Icon(
                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    contentDescription = if (isFullscreen) "Exit Fullscreen" else "Fullscreen",
                    tint = if (isFullscreen) brandBlue else textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // 3-dots Chrome Menu (⋮)
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("btn_chrome_menu")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Chrome Menu",
                        tint = textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(barBg)
                ) {
                    DropdownMenuItem(
                        text = { Text("New Tab", color = textPrimary) },
                        onClick = {
                            showMenu = false
                            onNewTabClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Reload Page", color = textPrimary) },
                        onClick = {
                            showMenu = false
                            onReloadClick()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isFullscreen) "Exit Fullscreen (Tabs to Right)" else "Fullscreen (Tabs to Top)",
                                color = brandBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        onClick = {
                            showMenu = false
                            onToggleFullscreen()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isDesktopMode) "✓ Desktop Site" else "Desktop Site",
                                color = textPrimary
                            )
                        },
                        onClick = {
                            showMenu = false
                            onToggleDesktopMode()
                        }
                    )
                    HorizontalDivider(color = dividerColor)


                    // Halloween Mode Toggle
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isHalloweenMode) "🕷️ Halloween Mode: ON" else "🕸️ Halloween Mode",
                                    color = if (isHalloweenMode) Color(0xFFFF7518) else textPrimary,
                                    fontWeight = if (isHalloweenMode) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        onClick = {
                            showMenu = false
                            onToggleHalloweenMode()
                        },
                        modifier = Modifier.testTag("menu_item_halloween_mode")
                    )

                    HorizontalDivider(color = dividerColor)

                    DropdownMenuItem(
                        text = { Text("📱 All Phone Apps", color = textPrimary) },
                        onClick = {
                            showMenu = false
                            onOpenAllApps()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("⚙️ Set as Default Launcher", color = GoogleYellow) },
                        onClick = {
                            showMenu = false
                            onOpenDefaultLauncherSettings()
                        }
                    )
                }
            }
        }
    }
}
