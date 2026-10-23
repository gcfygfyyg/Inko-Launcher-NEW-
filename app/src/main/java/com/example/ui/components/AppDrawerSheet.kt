package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppItem
import com.example.ui.theme.KoolBorder
import com.example.ui.theme.KoolCyan
import com.example.ui.theme.KoolSurfaceCard
import com.example.ui.theme.KoolSurfaceDark
import com.example.ui.theme.KoolSurfaceElevated
import com.example.ui.theme.KoolTextMuted
import com.example.ui.theme.KoolTextPrimary
import com.example.ui.theme.KoolTextSecondary

@Composable
fun AppDrawerSheet(
    apps: List<AppItem>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onLaunchApp: (AppItem) -> Unit,
    onTogglePinApp: (AppItem) -> Unit,
    onOpenAppInfo: (AppItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var inDrawerSearch by remember { mutableStateOf("") }

    val categories = listOf("All", "Internet", "Media", "Utilities", "Communication", "System", "Apps")

    val filteredApps = apps.filter { app ->
        val matchesCategory = (selectedCategory == "All") || app.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = inDrawerSearch.isBlank() ||
                app.label.contains(inDrawerSearch, ignoreCase = true) ||
                app.packageName.contains(inDrawerSearch, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KoolSurfaceDark)
            .padding(top = 8.dp)
    ) {
        // Search & Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "ALL APPLICATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = KoolCyan,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${filteredApps.size} apps available",
                    fontSize = 12.sp,
                    color = KoolTextSecondary
                )
            }

            // In-drawer quick filter input
            OutlinedTextField(
                value = inDrawerSearch,
                onValueChange = { inDrawerSearch = it },
                placeholder = { Text("Filter apps...", fontSize = 11.sp, color = KoolTextMuted) },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = KoolTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = KoolTextPrimary,
                    unfocusedTextColor = KoolTextPrimary,
                    focusedContainerColor = KoolSurfaceElevated,
                    unfocusedContainerColor = KoolSurfaceElevated,
                    focusedBorderColor = KoolCyan,
                    unfocusedBorderColor = KoolBorder
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .width(160.dp)
                    .height(40.dp)
            )
        }

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = cat == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectCategory(cat) },
                    label = { Text(text = cat, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KoolCyan,
                        selectedLabelColor = Color(0xFF00363D),
                        containerColor = KoolSurfaceElevated,
                        labelColor = KoolTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) KoolCyan else KoolBorder,
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }

        // Apps Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 92.dp),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("all_apps_grid")
        ) {
            items(filteredApps, key = { it.id }) { app ->
                AppGridCard(
                    app = app,
                    onLaunch = { onLaunchApp(app) },
                    onTogglePin = { onTogglePinApp(app) },
                    onOpenInfo = { onOpenAppInfo(app) }
                )
            }
        }
    }
}

@Composable
private fun AppGridCard(
    app: AppItem,
    onLaunch: () -> Unit,
    onTogglePin: () -> Unit,
    onOpenInfo: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = KoolSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (app.isPinned) KoolCyan.copy(alpha = 0.5f) else KoolBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_card_${app.packageName}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Options 3-dots
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = KoolTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(KoolSurfaceElevated)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, null, tint = KoolCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Launch App", color = KoolTextPrimary)
                            }
                        },
                        onClick = {
                            showMenu = false
                            onLaunch()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (app.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                    null,
                                    tint = KoolCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (app.isPinned) "Unpin from Home" else "Pin to Home", color = KoolTextPrimary)
                            }
                        },
                        onClick = {
                            showMenu = false
                            onTogglePin()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, tint = KoolTextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("App Info", color = KoolTextPrimary)
                            }
                        },
                        onClick = {
                            showMenu = false
                            onOpenInfo()
                        }
                    )
                }
            }

            // Main app button click
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp)
            ) {
                Surface(
                    onClick = onLaunch,
                    shape = RoundedCornerShape(16.dp),
                    color = KoolSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, KoolBorder),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = app.label.take(1).uppercase(),
                            color = KoolCyan,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = app.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KoolTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = app.category,
                    fontSize = 9.sp,
                    color = KoolTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
