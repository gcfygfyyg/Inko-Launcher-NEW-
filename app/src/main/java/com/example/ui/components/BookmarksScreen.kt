package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookmarkItem
import com.example.ui.theme.KoolBorder
import com.example.ui.theme.KoolCyan
import com.example.ui.theme.KoolRed
import com.example.ui.theme.KoolSurfaceCard
import com.example.ui.theme.KoolSurfaceDark
import com.example.ui.theme.KoolSurfaceElevated
import com.example.ui.theme.KoolTextMuted
import com.example.ui.theme.KoolTextPrimary
import com.example.ui.theme.KoolTextSecondary

@Composable
fun BookmarksScreen(
    bookmarks: List<BookmarkItem>,
    onOpenBookmark: (String) -> Unit,
    onAddBookmark: (String, String, String) -> Unit,
    onRemoveBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddRow by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var urlInput by remember { mutableStateOf("") }
    var emojiInput by remember { mutableStateOf("🌐") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KoolSurfaceDark)
            .padding(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "BOOKMARKS & SPEED DIALS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = KoolCyan,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${bookmarks.size} saved web links",
                    fontSize = 12.sp,
                    color = KoolTextSecondary
                )
            }

            Button(
                onClick = { showAddRow = !showAddRow },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KoolCyan,
                    contentColor = Color(0xFF00363D)
                ),
                modifier = Modifier.testTag("btn_toggle_add_bookmark")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (showAddRow) "Close" else "Add Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Add inline form
        if (showAddRow) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = KoolSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, KoolCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Save New Bookmark", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KoolCyan)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = emojiInput,
                            onValueChange = { emojiInput = it },
                            label = { Text("Emoji") },
                            modifier = Modifier.width(70.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = KoolTextPrimary,
                                unfocusedTextColor = KoolTextPrimary,
                                focusedBorderColor = KoolCyan,
                                unfocusedBorderColor = KoolBorder
                            )
                        )
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Title") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = KoolTextPrimary,
                                unfocusedTextColor = KoolTextPrimary,
                                focusedBorderColor = KoolCyan,
                                unfocusedBorderColor = KoolBorder
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("URL (e.g. reddit.com)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = KoolTextPrimary,
                            unfocusedTextColor = KoolTextPrimary,
                            focusedBorderColor = KoolCyan,
                            unfocusedBorderColor = KoolBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (urlInput.isNotBlank()) {
                                onAddBookmark(titleInput.ifBlank { "Link" }, urlInput, emojiInput.ifBlank { "🌐" })
                                titleInput = ""
                                urlInput = ""
                                emojiInput = "🌐"
                                showAddRow = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KoolCyan, contentColor = Color(0xFF00363D)),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save to Bookmarks", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bookmarks List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bookmarks, key = { it.id }) { item ->
                Surface(
                    onClick = { onOpenBookmark(item.url) },
                    shape = RoundedCornerShape(12.dp),
                    color = KoolSurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, KoolBorder),
                    modifier = Modifier.fillMaxWidth().testTag("bookmark_item_${item.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = KoolSurfaceElevated,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = item.iconEmoji, fontSize = 18.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
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
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        IconButton(
                            onClick = { onOpenBookmark(item.url) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.OpenInNew, "Open", tint = KoolCyan, modifier = Modifier.size(16.dp))
                        }

                        IconButton(
                            onClick = { onRemoveBookmark(item.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, "Delete", tint = KoolRed.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
