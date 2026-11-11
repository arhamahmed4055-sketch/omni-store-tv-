package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCategory
import com.example.model.MediaApp
import com.example.ui.components.TvMediaAppCard
import com.example.ui.theme.TvFocusRing
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSurfaceVariant
import com.example.ui.theme.TvTextMuted
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun CategoriesScreen(
    apps: List<MediaApp>,
    selectedCategory: AppCategory,
    onCategorySelected: (AppCategory) -> Unit,
    onAppClick: (MediaApp) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredApps = if (selectedCategory == AppCategory.ALL) {
        apps
    } else {
        apps.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Category Pills (Horizontal TV D-pad row)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(AppCategory.values()) { category ->
                val isSelected = category == selectedCategory
                var isFocused by remember { mutableStateOf(false) }

                val icon: ImageVector = when (category) {
                    AppCategory.ALL -> Icons.Default.Apps
                    AppCategory.STREAMING -> Icons.Default.LiveTv
                    AppCategory.MEDIA_PLAYERS -> Icons.Default.PlayCircle
                    AppCategory.IPTV_LIVE -> Icons.Default.Tv
                    AppCategory.AUDIO_MUSIC -> Icons.Default.Headphones
                    AppCategory.SPORTS -> Icons.Default.SportsSoccer
                    AppCategory.TV_TOOLS -> Icons.Default.Build
                    AppCategory.EMULATORS -> Icons.Default.SportsEsports
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                isSelected -> TvPrimary
                                isFocused -> Color(0xFF1E293B)
                                else -> Color(0xFF131A28)
                            }
                        )
                        .border(
                            BorderStroke(
                                width = if (isFocused) 2.dp else if (isSelected) 1.dp else 0.dp,
                                color = if (isFocused) TvFocusRing else if (isSelected) TvPrimary else Color.Transparent
                            ),
                            RoundedCornerShape(12.dp)
                        )
                        .onFocusChanged { isFocused = it.isFocused }
                        .focusable()
                        .clickable { onCategorySelected(category) }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                        .testTag("category_chip_${category.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = category.title,
                            tint = if (isSelected) Color.Black else if (isFocused) TvPrimary else TvTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else if (isFocused) Color.White else TvTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Grid of Apps
        if (filteredApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        tint = TvTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No applications in ${selectedCategory.title}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TvTextSecondary
                    )
                    Text(
                        text = "Upload custom APKs from cloud storage to populate this category.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TvTextMuted
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 270.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredApps, key = { it.id }) { app ->
                    TvMediaAppCard(
                        app = app,
                        onClick = { onAppClick(app) }
                    )
                }
            }
        }
    }
}
