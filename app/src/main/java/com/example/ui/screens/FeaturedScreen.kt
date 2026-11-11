package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.AppCategory
import com.example.model.MediaApp
import com.example.ui.components.TvBadge
import com.example.ui.components.TvMediaAppCard
import com.example.ui.theme.TvBadgeCloud
import com.example.ui.theme.TvBadgeUpdate
import com.example.ui.theme.TvFocusRing
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSecondary
import com.example.ui.theme.TvSuccess
import com.example.ui.theme.TvTextMuted
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun FeaturedScreen(
    apps: List<MediaApp>,
    onAppClick: (MediaApp) -> Unit,
    onSideloadClick: (MediaApp) -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultFeatured = apps.find { it.isFeatured } ?: apps.firstOrNull()
    var spotlightApp by remember { mutableStateOf(defaultFeatured) }

    val pendingUpdateApps = apps.filter { it.hasUpdate }
    val cloudSyncedApps = apps.filter { it.isCloudSynced }
    val mediaCenterApps = apps.filter { it.category == AppCategory.MEDIA_PLAYERS }
    val streamingApps = apps.filter { it.category == AppCategory.STREAMING }
    val sportsApps = apps.filter { it.category == AppCategory.SPORTS }
    val iptvApps = apps.filter { it.category == AppCategory.IPTV_LIVE || it.category == AppCategory.AUDIO_MUSIC }
    val toolApps = apps.filter { it.category == AppCategory.TV_TOOLS || it.category == AppCategory.EMULATORS }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        // Top Dynamic TV Spotlight (Reflects D-pad focused app with high-res TV backdrop)
        item {
            spotlightApp?.let { app ->
                TvCinematicSpotlight(
                    app = app,
                    onOpenDetails = { onAppClick(app) },
                    onSideload = { onSideloadClick(app) }
                )
            }
        }

        // Row 1: Automatic Updates Ready (if any pending)
        if (pendingUpdateApps.isNotEmpty()) {
            item {
                TvLeanbackRow(
                    title = "Automatic Updates Ready",
                    subtitle = "${pendingUpdateApps.size} apps have newer versions in cloud repository",
                    icon = Icons.Default.SystemUpdate,
                    iconTint = TvBadgeUpdate,
                    apps = pendingUpdateApps,
                    onAppClick = onAppClick,
                    onAppFocused = { spotlightApp = it }
                )
            }
        }

        // Row 2: Cloud Storage Synced Library
        if (cloudSyncedApps.isNotEmpty()) {
            item {
                TvLeanbackRow(
                    title = "Cloud-Synced Sideload Library",
                    subtitle = "Synced across your Android TVs from Google Drive, Dropbox, and WebDAV",
                    icon = Icons.Default.CloudDone,
                    iconTint = TvBadgeCloud,
                    apps = cloudSyncedApps,
                    onAppClick = onAppClick,
                    onAppFocused = { spotlightApp = it }
                )
            }
        }

        // Row 3: Essential Media Centers & Home Theater Hubs
        if (mediaCenterApps.isNotEmpty()) {
            item {
                TvLeanbackRow(
                    title = "Media Centers & Video Players",
                    subtitle = "Open source media systems, 4K passthrough, and cinema hubs",
                    icon = Icons.Default.PlayCircle,
                    iconTint = TvPrimary,
                    apps = mediaCenterApps,
                    onAppClick = onAppClick,
                    onAppFocused = { spotlightApp = it }
                )
            }
        }

        // Row 4: Streaming & Community Media Clients
        if (streamingApps.isNotEmpty()) {
            item {
                TvLeanbackRow(
                    title = "Streaming & Community Hubs",
                    subtitle = "Ad-free TV streaming, modular scrapers, and media aggregators",
                    icon = Icons.Default.LiveTv,
                    iconTint = TvSecondary,
                    apps = streamingApps,
                    onAppClick = onAppClick,
                    onAppFocused = { spotlightApp = it }
                )
            }
        }

        // Row 5: Live Sports, Tournaments & Racing
        if (sportsApps.isNotEmpty()) {
            item {
                TvLeanbackRow(
                    title = "Live Sports, Tournaments & Racing",
                    subtitle = "Live matches, combat PPV, Formula 1 telemetry, and condensed 4K replays",
                    icon = Icons.Default.SportsSoccer,
                    iconTint = Color(0xFF10B981),
                    apps = sportsApps,
                    onAppClick = onAppClick,
                    onAppFocused = { spotlightApp = it }
                )
            }
        }

        // Row 5: Live TV, IPTV & Music
        if (iptvApps.isNotEmpty()) {
            item {
                TvLeanbackRow(
                    title = "IPTV, Live TV & Radio",
                    subtitle = "Live broadcast companions, EPG guides, and worldwide radio",
                    icon = Icons.Default.Tv,
                    iconTint = Color(0xFFFFB300),
                    apps = iptvApps,
                    onAppClick = onAppClick,
                    onAppFocused = { spotlightApp = it }
                )
            }
        }

        // Row 6: TV Utilities, Diagnostics & Frontends
        if (toolApps.isNotEmpty()) {
            item {
                TvLeanbackRow(
                    title = "TV Diagnostics, Tools & Frontends",
                    subtitle = "Downloader utilities, 4K network tests, and game emulation frontends",
                    icon = Icons.Default.Build,
                    iconTint = Color(0xFF38BDF8),
                    apps = toolApps,
                    onAppClick = onAppClick,
                    onAppFocused = { spotlightApp = it }
                )
            }
        }
    }
}

/**
 * Android TV 10-foot Cinematic Spotlight Hero.
 * Updates dynamically as the TV remote focuses different apps!
 */
@Composable
fun TvCinematicSpotlight(
    app: MediaApp,
    onOpenDetails: () -> Unit,
    onSideload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(BorderStroke(1.5.dp, Color(0xFF1E293B)), RoundedCornerShape(24.dp))
            .testTag("tv_cinematic_spotlight"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0E17))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background TV Backdrop Artwork with gradient vignette
            Image(
                painter = painterResource(id = R.drawable.tv_hero_backdrop),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterEnd),
                alpha = 0.35f
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF090D15),
                                Color(0xFF090D15).copy(alpha = 0.95f),
                                Color(0xFF0B1220).copy(alpha = 0.65f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Content Column
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .padding(end = 20.dp)
                ) {
                    // Badges row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (app.hasUpdate) {
                            TvBadge(
                                text = "UPDATE: v${app.installedVersionName} ➔ v${app.versionName}",
                                color = TvBadgeUpdate
                            )
                        } else if (app.isInstalled) {
                            TvBadge(text = "INSTALLED ON THIS TV", color = TvSuccess)
                        } else {
                            TvBadge(text = "SIDELOAD AVAILABLE", color = TvPrimary)
                        }

                        if (app.isCloudSynced && app.cloudSource != null) {
                            TvBadge(text = "FROM ${app.cloudSource.displayName.uppercase()}", color = TvBadgeCloud)
                        }

                        TvBadge(text = app.category.title.uppercase(), color = Color(0xFF94A3B8))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Title with crossfade
                    AnimatedContent(
                        targetState = app.name,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "spotlightTitle"
                    ) { name ->
                        Text(
                            text = name,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Metadata: Developer, Rating, Size
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = app.developer,
                            style = MaterialTheme.typography.titleMedium,
                            color = TvPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "•", color = TvTextMuted)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${app.rating}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFFB300),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "•", color = TvTextMuted)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${app.formattedSize} • ${app.minAndroidVersion}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TvTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = app.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TvTextSecondary,
                        maxLines = 2,
                        lineHeight = 19.sp,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // TV Action Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                if (app.hasUpdate || !app.isInstalled) {
                                    onSideload()
                                } else {
                                    onOpenDetails()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (app.hasUpdate) TvBadgeUpdate else if (app.isInstalled) TvSecondary else TvPrimary
                            ),
                            modifier = Modifier.testTag("spotlight_primary_btn")
                        ) {
                            Icon(
                                imageVector = if (app.hasUpdate) Icons.Default.SystemUpdate else if (app.isInstalled) Icons.Default.PlayArrow else Icons.Default.Download,
                                contentDescription = null,
                                tint = if (app.hasUpdate || app.isInstalled) Color.White else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (app.hasUpdate) "1-Click Update to v${app.versionName}" else if (app.isInstalled) "Launch on TV" else "Sideload to TV",
                                color = if (app.hasUpdate || app.isInstalled) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = onOpenDetails,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.testTag("spotlight_details_btn")
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = TvTextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Full Info & Changelog", color = Color.White, fontSize = 13.sp)
                        }
                    }
                }

                // Spotlight App Big Holographic Icon
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF131B2A))
                        .border(BorderStroke(2.dp, TvPrimary.copy(alpha = 0.5f)), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (app.iconUrl.isNotBlank()) {
                        AsyncImage(
                            model = app.iconUrl,
                            contentDescription = app.name,
                            modifier = Modifier.size(96.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = app.name,
                            tint = TvPrimary,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Standard Android TV Leanback Row.
 * Horizontal carousel row with title, item count, and remote-friendly TV cards.
 */
@Composable
fun TvLeanbackRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    apps: List<MediaApp>,
    onAppClick: (MediaApp) -> Unit,
    onAppFocused: (MediaApp) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TvTextPrimary
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = TvTextMuted
                    )
                }
            }

            Text(
                text = "${apps.size} items",
                style = MaterialTheme.typography.labelSmall,
                color = TvTextMuted
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
        ) {
            items(apps, key = { it.id }) { app ->
                TvMediaAppCard(
                    app = app,
                    onClick = { onAppClick(app) },
                    onFocus = { onAppFocused(app) },
                    modifier = Modifier.width(280.dp)
                )
            }
        }
    }
}
