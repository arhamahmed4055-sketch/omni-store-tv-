package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.DownloadState
import com.example.model.MediaApp
import com.example.ui.components.TvBadge
import com.example.ui.theme.TvBadgeCloud
import com.example.ui.theme.TvBadgeUpdate
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSuccess
import com.example.ui.theme.TvTextMuted
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun UpdatesScreen(
    appsWithUpdates: List<MediaApp>,
    downloadStates: Map<String, DownloadState>,
    onUpdateApp: (MediaApp) -> Unit,
    onUpdateAll: () -> Unit,
    onAppClick: (MediaApp) -> Unit,
    onCheckForUpdates: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        // Summary Header Card
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF131A28),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = null,
                                tint = if (appsWithUpdates.isNotEmpty()) TvBadgeUpdate else TvSuccess,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (appsWithUpdates.isNotEmpty()) {
                                    "${appsWithUpdates.size} Application Updates Available"
                                } else {
                                    "All Sideloaded Apps Are Up to Date"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Automatic update daemon monitors remote repositories and connected cloud drives.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TvTextMuted
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onCheckForUpdates,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Check Now", color = Color.White)
                        }

                        if (appsWithUpdates.isNotEmpty()) {
                            Button(
                                onClick = onUpdateAll,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TvBadgeUpdate),
                                modifier = Modifier.testTag("update_all_apps_btn")
                            ) {
                                Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Update All (${appsWithUpdates.size})", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (appsWithUpdates.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = TvSuccess,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No pending updates",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "All installed media applications match the latest versions in your cloud repository.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TvTextMuted
                        )
                    }
                }
            }
        } else {
            items(appsWithUpdates, key = { it.id }) { app ->
                val downloadState = downloadStates[app.id]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onAppClick(app) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E)),
                    border = BorderStroke(1.dp, Color(0xFF243044))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF1E293B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (app.iconUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = app.iconUrl,
                                            contentDescription = app.name,
                                            modifier = Modifier.size(42.dp)
                                        )
                                    } else {
                                        Icon(Icons.Default.Tv, contentDescription = null, tint = TvPrimary)
                                    }
                                }

                                Column {
                                    Text(
                                        text = app.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Installed: v${app.installedVersionName ?: "N/A"}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TvTextMuted
                                        )
                                        Text(
                                            text = "  ➔  ",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TvBadgeUpdate,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "New: v${app.versionName}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TvSuccess,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "  •  ${app.formattedSize}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TvTextMuted
                                        )
                                    }
                                }
                            }

                            // Update Button / Progress
                            if (downloadState is DownloadState.Downloading) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Downloading ${(downloadState.progress * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TvPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { downloadState.progress },
                                        modifier = Modifier
                                            .width(130.dp)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = TvPrimary,
                                        trackColor = Color(0xFF334155)
                                    )
                                }
                            } else if (downloadState is DownloadState.Installing) {
                                Text(
                                    text = "Installing...",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color(0xFFC7D2FE)
                                )
                            } else {
                                Button(
                                    onClick = { onUpdateApp(app) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TvBadgeUpdate),
                                    modifier = Modifier.testTag("update_single_app_${app.id}")
                                ) {
                                    Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("1-Click Update", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        // Changelog Highlight
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0F172A),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "RELEASE HIGHLIGHTS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TvTextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = app.changelog,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
