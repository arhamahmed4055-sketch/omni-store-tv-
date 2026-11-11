package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.DownloadState
import com.example.model.MediaApp
import com.example.ui.theme.TvBadgeCloud
import com.example.ui.theme.TvBadgeUpdate
import com.example.ui.theme.TvFocusRing
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSecondary
import com.example.ui.theme.TvSuccess
import com.example.ui.theme.TvSurface
import com.example.ui.theme.TvTextMuted
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun AppDetailsDialog(
    app: MediaApp?,
    downloadState: DownloadState?,
    onDismiss: () -> Unit,
    onSideload: (MediaApp) -> Unit,
    onLaunch: (MediaApp) -> Unit,
    onUninstall: (MediaApp) -> Unit
) {
    if (app == null) return

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF101622)),
            border = BorderStroke(1.5.dp, TvFocusRing.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                // Top Header Row with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (app.iconUrl.isNotBlank()) {
                                AsyncImage(
                                    model = app.iconUrl,
                                    contentDescription = app.name,
                                    modifier = Modifier.size(62.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Tv,
                                    contentDescription = app.name,
                                    tint = TvPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = app.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${app.developer} • ${app.category.title}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TvTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${app.rating} / 5.0",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFFFB300),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "•  ${app.formattedSize}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TvTextMuted
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TvTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Status Badge Strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (app.hasUpdate) {
                        TvBadge(text = "UPDATE: ${app.installedVersionName} ➔ ${app.versionName}", color = TvBadgeUpdate)
                    } else if (app.isInstalled) {
                        TvBadge(text = "CURRENTLY INSTALLED (v${app.installedVersionName})", color = TvSuccess)
                    } else {
                        TvBadge(text = "READY TO SIDELOAD (v${app.versionName})", color = TvPrimary)
                    }

                    if (app.isCloudSynced && app.cloudSource != null) {
                        TvBadge(text = "SYNCED VIA ${app.cloudSource.displayName}", color = TvBadgeCloud)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions Section (Sideload / Launch / Uninstall / Progress)
                if (downloadState is DownloadState.Downloading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E293B))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Downloading APK package...",
                                style = MaterialTheme.typography.labelLarge,
                                color = TvPrimary
                            )
                            Text(
                                text = "${(downloadState.progress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { downloadState.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = TvPrimary,
                            trackColor = Color(0xFF334155)
                        )
                    }
                } else if (downloadState is DownloadState.Installing) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF312E81))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = downloadState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFC7D2FE)
                        )
                    }
                } else if (downloadState is DownloadState.Completed) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF064E3B))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = TvSuccess)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = downloadState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFA7F3D0)
                            )
                        }
                    }
                } else {
                    // Normal Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (app.hasUpdate) {
                            Button(
                                onClick = { onSideload(app) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("modal_update_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = TvBadgeUpdate),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.SystemUpdate, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Update to v${app.versionName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        } else if (!app.isInstalled) {
                            Button(
                                onClick = { onSideload(app) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("modal_sideload_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = TvPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sideload on Android TV", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        } else {
                            Button(
                                onClick = { onLaunch(app) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("modal_launch_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = TvSecondary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Launch Application", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            OutlinedButton(
                                onClick = { onUninstall(app) },
                                modifier = Modifier.height(48.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Uninstall")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Uninstall")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Description
                Text(
                    text = "APPLICATION OVERVIEW",
                    style = MaterialTheme.typography.labelSmall,
                    color = TvTextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = app.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TvTextPrimary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Changelog
                Text(
                    text = "WHAT'S NEW IN V${app.versionName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TvTextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = app.changelog,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCBD5E1),
                        modifier = Modifier.padding(14.dp),
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Package Details & Permissions
                Text(
                    text = "PACKAGE INFORMATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = TvTextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Package ID", style = MaterialTheme.typography.labelSmall, color = TvTextMuted)
                        Text(app.packageName, style = MaterialTheme.typography.bodyMedium, color = TvTextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Compatibility", style = MaterialTheme.typography.labelSmall, color = TvTextMuted)
                        Text(app.minAndroidVersion, style = MaterialTheme.typography.bodyMedium, color = TvTextSecondary)
                    }
                }

                if (app.permissions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = TvPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Declared Permissions: ${app.permissions.joinToString(", ")}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TvTextMuted
                        )
                    }
                }
            }
        }
    }
}
