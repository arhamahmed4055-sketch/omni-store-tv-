package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppCategory
import com.example.model.CloudAccount
import com.example.model.CloudProvider
import com.example.model.MediaApp
import com.example.model.SyncDevice
import com.example.ui.components.TvBadge
import com.example.ui.components.TvMediaAppCard
import com.example.ui.theme.TvBadgeCloud
import com.example.ui.theme.TvFocusRing
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSecondary
import com.example.ui.theme.TvSuccess
import com.example.ui.theme.TvSurface
import com.example.ui.theme.TvSurfaceVariant
import com.example.ui.theme.TvTextMuted
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun CloudSyncScreen(
    cloudAccounts: List<CloudAccount>,
    syncDevices: List<SyncDevice>,
    syncedApps: List<MediaApp>,
    isSyncing: Boolean,
    syncStatusMessage: String,
    onSyncNowClick: () -> Unit,
    onAppClick: (MediaApp) -> Unit,
    onUploadApp: (name: String, pkg: String, ver: String, cat: AppCategory, url: String, dev: String, provider: CloudProvider, desc: String) -> Unit,
    onAddCloudAccount: (provider: CloudProvider, name: String, folderOrUrl: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showUploadDialog by remember { mutableStateOf(false) }
    var showAddAccountDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        // Top Header & Sync Bar
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
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = TvPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Cloud Storage Sync Engine",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = syncStatusMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSyncing) TvPrimary else TvSuccess
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { showUploadDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TvSecondary),
                            modifier = Modifier.testTag("upload_cloud_app_btn")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload App to Cloud", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onSyncNowClick,
                            enabled = !isSyncing,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TvPrimary),
                            modifier = Modifier.testTag("sync_all_devices_btn")
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Syncing...", color = Color.Black)
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync All Devices", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section 1: Connected Cloud Integrations
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Connected Cloud Integrations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TvTextPrimary
                )

                OutlinedButton(
                    onClick = { showAddAccountDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TvPrimary),
                    border = BorderStroke(1.dp, TvPrimary.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Cloud Storage")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(cloudAccounts) { account ->
                    CloudAccountCard(account = account)
                }
            }
        }

        // Section 2: Synced Multi-Device Ecosystem
        item {
            Text(
                text = "Synced Multi-Device Ecosystem",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TvTextPrimary
            )
            Text(
                text = "APKs and preferences uploaded to your cloud automatically sync across all your Android TVs and devices.",
                style = MaterialTheme.typography.bodyMedium,
                color = TvTextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                syncDevices.forEach { device ->
                    SyncDeviceCard(device = device, modifier = Modifier.weight(1f))
                }
            }
        }

        // Section 3: Cloud-Synced Sideload Library
        item {
            Text(
                text = "Cloud-Synced Media Library (${syncedApps.size} Applications)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TvTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 270.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(((syncedApps.size / 2 + 1) * 220).coerceAtLeast(300).dp)
            ) {
                items(syncedApps, key = { it.id }) { app ->
                    TvMediaAppCard(app = app, onClick = { onAppClick(app) })
                }
            }
        }
    }

    // Modal: Upload / Sideload App directly from Cloud
    if (showUploadDialog) {
        UploadAppCloudDialog(
            onDismiss = { showUploadDialog = false },
            onUpload = { name, pkg, ver, cat, url, dev, prov, desc ->
                onUploadApp(name, pkg, ver, cat, url, dev, prov, desc)
                showUploadDialog = false
            }
        )
    }

    // Modal: Connect New Cloud Storage Integration
    if (showAddAccountDialog) {
        AddCloudAccountDialog(
            onDismiss = { showAddAccountDialog = false },
            onAdd = { prov, name, path ->
                onAddCloudAccount(prov, name, path)
                showAddAccountDialog = false
            }
        )
    }
}

@Composable
fun CloudAccountCard(
    account: CloudAccount,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(260.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E)),
        border = BorderStroke(1.dp, Color(account.provider.colorHex).copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(account.provider.colorHex).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (account.provider) {
                            CloudProvider.GOOGLE_DRIVE -> Icons.Default.Cloud
                            CloudProvider.DROPBOX -> Icons.Default.CloudQueue
                            CloudProvider.WEBDAV -> Icons.Default.Dns
                            CloudProvider.CUSTOM_REPO -> Icons.Default.Hub
                        },
                        contentDescription = account.provider.displayName,
                        tint = Color(account.provider.colorHex),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(TvSuccess)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Connected", color = TvSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = account.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = account.endpointOrFolder,
                style = MaterialTheme.typography.bodyMedium,
                color = TvTextMuted,
                fontSize = 11.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${account.appCount} APKs Synced",
                    style = MaterialTheme.typography.labelSmall,
                    color = TvPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Auto-Sync Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = TvTextMuted
                )
            }
        }
    }
}

@Composable
fun SyncDeviceCard(
    device: SyncDevice,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (device.isCurrentDevice) Color(0xFF16253B) else Color(0xFF131A28)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (device.isCurrentDevice) TvPrimary.copy(alpha = 0.6f) else Color(0xFF1E293B)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tv,
                    contentDescription = null,
                    tint = if (device.isCurrentDevice) TvPrimary else TvTextSecondary,
                    modifier = Modifier.size(22.dp)
                )

                if (device.isCurrentDevice) {
                    TvBadge(text = "THIS TV", color = TvPrimary)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (device.isOnline) TvSuccess else TvTextMuted)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (device.isOnline) "Online" else "Offline",
                            color = if (device.isOnline) TvSuccess else TvTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = device.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 13.sp
            )

            Text(
                text = device.deviceModel,
                style = MaterialTheme.typography.bodyMedium,
                color = TvTextMuted,
                fontSize = 11.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = device.lastSyncTime,
                style = MaterialTheme.typography.labelSmall,
                color = TvTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadAppCloudDialog(
    onDismiss: () -> Unit,
    onUpload: (name: String, pkg: String, ver: String, cat: AppCategory, url: String, dev: String, provider: CloudProvider, desc: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("1.0.0") }
    var selectedCategory by remember { mutableStateOf(AppCategory.STREAMING) }
    var downloadUrl by remember { mutableStateOf("") }
    var developer by remember { mutableStateOf("") }
    var selectedProvider by remember { mutableStateOf(CloudProvider.GOOGLE_DRIVE) }
    var description by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF101622)),
            border = BorderStroke(1.5.dp, TvPrimary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upload App from Cloud Storage",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TvTextSecondary)
                    }
                }

                Text(
                    text = "Add an APK from your connected cloud storage (Google Drive, Dropbox, WebDAV) to automatically sideload and sync across all TV devices.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TvTextMuted
                )

                // Cloud Provider Selection
                Text("Select Cloud Source", style = MaterialTheme.typography.labelSmall, color = TvTextSecondary)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CloudProvider.values().forEach { prov ->
                        val isSel = prov == selectedProvider
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) Color(prov.colorHex).copy(alpha = 0.25f) else Color(0xFF1E293B))
                                .border(
                                    BorderStroke(1.dp, if (isSel) Color(prov.colorHex) else Color(0xFF334155)),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedProvider = prov }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = prov.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSel) Color.White else TvTextMuted,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // App Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (packageName.isBlank()) {
                            packageName = "com.tv.${it.lowercase().replace(" ", "")}"
                        }
                    },
                    label = { Text("Application Name") },
                    placeholder = { Text("e.g. CinemaHD TV, IPTV Extreme") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TvPrimary,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Package & Version
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = packageName,
                        onValueChange = { packageName = it },
                        label = { Text("Package ID") },
                        placeholder = { Text("com.example.app") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TvPrimary,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.weight(1.5f)
                    )

                    OutlinedTextField(
                        value = version,
                        onValueChange = { version = it },
                        label = { Text("Version") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TvPrimary,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Cloud APK URL / Path
                OutlinedTextField(
                    value = downloadUrl,
                    onValueChange = { downloadUrl = it },
                    label = { Text("Cloud Storage URL or File Link") },
                    placeholder = { Text("https://drive.google.com/... or https://dl.dropbox.com/...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TvPrimary,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Developer & Description
                OutlinedTextField(
                    value = developer,
                    onValueChange = { developer = it },
                    label = { Text("Developer / Source") },
                    placeholder = { Text("e.g. Community Build, Cloud Share") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TvPrimary,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TvTextSecondary)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onUpload(
                                    name,
                                    packageName.ifBlank { "com.custom.${name.lowercase().replace(" ", "")}" },
                                    version.ifBlank { "1.0.0" },
                                    selectedCategory,
                                    downloadUrl.ifBlank { "https://example.com/apk/$name.apk" },
                                    developer.ifBlank { "Cloud Upload" },
                                    selectedProvider,
                                    description
                                )
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = TvPrimary)
                    ) {
                        Text("Upload & Sync", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddCloudAccountDialog(
    onDismiss: () -> Unit,
    onAdd: (provider: CloudProvider, name: String, folderOrUrl: String) -> Unit
) {
    var selectedProvider by remember { mutableStateOf(CloudProvider.GOOGLE_DRIVE) }
    var accountName by remember { mutableStateOf("") }
    var folderOrUrl by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF101622)),
            border = BorderStroke(1.5.dp, TvPrimary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Connect Cloud Storage",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TvTextSecondary)
                    }
                }

                // Provider Chooser
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CloudProvider.values().forEach { prov ->
                        val isSel = prov == selectedProvider
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) Color(prov.colorHex).copy(alpha = 0.25f) else Color(0xFF1E293B))
                                .border(
                                    BorderStroke(1.dp, if (isSel) Color(prov.colorHex) else Color(0xFF334155)),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedProvider = prov }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = prov.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSel) Color.White else TvTextMuted,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text("Integration Label") },
                    placeholder = { Text("e.g. My Media Drive, Bedroom Nextcloud") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TvPrimary,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = folderOrUrl,
                    onValueChange = { folderOrUrl = it },
                    label = { Text("Folder Path or Cloud API Endpoint") },
                    placeholder = { Text("/My Drive/APKs or https://nas.lan/dav") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TvPrimary,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TvTextSecondary)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (accountName.isNotBlank()) {
                                onAdd(selectedProvider, accountName, folderOrUrl.ifBlank { "/Default/APKs" })
                            }
                        },
                        enabled = accountName.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = TvPrimary)
                    ) {
                        Text("Connect & Sync", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
