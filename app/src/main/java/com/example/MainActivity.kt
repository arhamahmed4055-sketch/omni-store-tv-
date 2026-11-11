package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.service.SideloadManager
import com.example.ui.components.AppDetailsDialog
import com.example.ui.components.TvRemoteKeyHint
import com.example.ui.components.TvSideNavigationRail
import com.example.ui.components.TvTopBar
import com.example.ui.components.TvUpdateNotificationBanner
import com.example.ui.components.TvVirtualRemoteOverlay
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CloudSyncScreen
import com.example.ui.screens.FeaturedScreen
import com.example.ui.screens.InstalledScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.UpdatesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TvBackground
import com.example.viewmodel.NavigationTab
import com.example.viewmodel.StoreViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                OmniStoreTvApp()
            }
        }
    }
}

@Composable
fun OmniStoreTvApp(
    viewModel: StoreViewModel = viewModel()
) {
    val context = LocalContext.current
    val sideloadManager = SideloadManager(context)

    val activeTab by viewModel.activeTab.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val apps by viewModel.apps.collectAsState()
    val selectedAppForDetails by viewModel.selectedAppForDetails.collectAsState()
    val downloadStates by viewModel.downloadStates.collectAsState()
    val cloudAccounts by viewModel.cloudAccounts.collectAsState()
    val syncDevices by viewModel.syncDevices.collectAsState()
    val updateNotifications by viewModel.updateNotifications.collectAsState()
    val isSyncingCloud by viewModel.isSyncingCloud.collectAsState()
    val lastCloudSyncStatus by viewModel.lastCloudSyncStatus.collectAsState()
    val isVirtualRemoteVisible by viewModel.isVirtualRemoteVisible.collectAsState()
    val autoCheckUpdates by viewModel.autoCheckUpdates.collectAsState()
    val updateIntervalHours by viewModel.updateIntervalHours.collectAsState()
    val silentInstallEnabled by viewModel.silentInstallEnabled.collectAsState()
    val bannerNotification by viewModel.bannerNotification.collectAsState()

    // Handle Android TV Back remote key
    BackHandler(enabled = selectedAppForDetails != null || activeTab != NavigationTab.FEATURED) {
        if (selectedAppForDetails != null) {
            viewModel.closeAppDetails()
        } else if (activeTab != NavigationTab.FEATURED) {
            viewModel.selectTab(NavigationTab.FEATURED)
        }
    }

    // Filter apps by search query if user entered anything
    val displayApps = if (searchQuery.isBlank()) {
        apps
    } else {
        apps.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true) ||
                    it.developer.contains(searchQuery, ignoreCase = true) ||
                    it.category.title.contains(searchQuery, ignoreCase = true)
        }
    }

    val installedApps = apps.filter { it.isInstalled }
    val appsWithUpdates = apps.filter { it.hasUpdate }
    val syncedApps = apps.filter { it.isCloudSynced }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(TvBackground)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(TvBackground)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // TV Top Bar
                TvTopBar(
                    searchQuery = searchQuery,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    pendingUpdatesCount = appsWithUpdates.size,
                    cloudSyncStatus = lastCloudSyncStatus,
                    onSyncClick = { viewModel.syncCloudStorage() },
                    onToggleRemote = { viewModel.toggleVirtualRemote() }
                )

                // TV Update Notification Slide-Down Banner
                TvUpdateNotificationBanner(
                    notification = bannerNotification,
                    onUpdateClick = { appId ->
                        val targetApp = apps.find { it.id == appId }
                        if (targetApp != null) {
                            viewModel.sideloadApp(targetApp)
                        }
                    },
                    onDismiss = { viewModel.dismissBannerNotification() }
                )

                // Main Content Area: Side Navigation Rail + Main View
                Row(modifier = Modifier.fillMaxSize()) {
                    TvSideNavigationRail(
                        activeTab = activeTab,
                        onTabSelected = { viewModel.selectTab(it) },
                        pendingUpdatesCount = appsWithUpdates.size
                    )

                    // Active Screen based on Tab
                    Box(modifier = Modifier.weight(1f)) {
                        when (activeTab) {
                            NavigationTab.FEATURED -> {
                                FeaturedScreen(
                                    apps = displayApps,
                                    onAppClick = { viewModel.openAppDetails(it) },
                                    onSideloadClick = { viewModel.sideloadApp(it) }
                                )
                            }
                            NavigationTab.CATEGORIES -> {
                                CategoriesScreen(
                                    apps = displayApps,
                                    selectedCategory = selectedCategory,
                                    onCategorySelected = { viewModel.selectCategory(it) },
                                    onAppClick = { viewModel.openAppDetails(it) }
                                )
                            }
                            NavigationTab.CLOUD_SYNC -> {
                                CloudSyncScreen(
                                    cloudAccounts = cloudAccounts,
                                    syncDevices = syncDevices,
                                    syncedApps = syncedApps,
                                    isSyncing = isSyncingCloud,
                                    syncStatusMessage = lastCloudSyncStatus,
                                    onSyncNowClick = { viewModel.syncCloudStorage() },
                                    onAppClick = { viewModel.openAppDetails(it) },
                                    onUploadApp = { name, pkg, ver, cat, url, dev, prov, desc ->
                                        viewModel.uploadCustomApp(name, pkg, ver, cat, url, dev, prov, desc)
                                    },
                                    onAddCloudAccount = { prov, name, path ->
                                        viewModel.addCloudAccount(prov, name, path)
                                    }
                                )
                            }
                            NavigationTab.UPDATES -> {
                                UpdatesScreen(
                                    appsWithUpdates = appsWithUpdates,
                                    downloadStates = downloadStates,
                                    onUpdateApp = { viewModel.sideloadApp(it) },
                                    onUpdateAll = { viewModel.updateAllApps() },
                                    onAppClick = { viewModel.openAppDetails(it) },
                                    onCheckForUpdates = { viewModel.checkForUpdates() }
                                )
                            }
                            NavigationTab.INSTALLED -> {
                                InstalledScreen(
                                    installedApps = installedApps,
                                    onLaunchApp = { viewModel.launchApp(it) },
                                    onAppClick = { viewModel.openAppDetails(it) },
                                    onUninstallApp = { viewModel.uninstallApp(it) }
                                )
                            }
                            NavigationTab.SETTINGS -> {
                                SettingsScreen(
                                    autoCheckUpdates = autoCheckUpdates,
                                    onToggleAutoCheckUpdates = { viewModel.setAutoCheckUpdates(it) },
                                    updateIntervalHours = updateIntervalHours,
                                    onUpdateIntervalChange = { viewModel.setUpdateIntervalHours(it) },
                                    onOpenSecuritySettings = { sideloadManager.openUnknownSourcesSettings() },
                                    silentInstallEnabled = silentInstallEnabled,
                                    onToggleSilentInstall = { viewModel.setSilentInstallEnabled(it) }
                                )
                            }
                        }
                    }
                }

                // Android TV Remote Shortcut HUD Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF070A0F))
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TvRemoteKeyHint(keyLabel = "▲▼◀▶", actionLabel = "Navigate")
                        TvRemoteKeyHint(keyLabel = "OK", actionLabel = "Select / Sideload")
                        TvRemoteKeyHint(keyLabel = "PLAY", actionLabel = "Quick Launch")
                        TvRemoteKeyHint(keyLabel = "BACK", actionLabel = "Return")
                    }

                    Text(
                        text = "Android TV 10-Foot Interface",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontSize = 10.sp
                    )
                }
            }

            // App Details & Sideload Action Modal Dialog
            AppDetailsDialog(
                app = selectedAppForDetails,
                downloadState = selectedAppForDetails?.let { downloadStates[it.id] },
                onDismiss = { viewModel.closeAppDetails() },
                onSideload = { viewModel.sideloadApp(it) },
                onLaunch = { viewModel.launchApp(it) },
                onUninstall = { viewModel.uninstallApp(it) }
            )

            // On-screen Virtual TV Remote overlay (collapsible simulator)
            TvVirtualRemoteOverlay(
                isVisible = isVirtualRemoteVisible,
                onClose = { viewModel.toggleVirtualRemote() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            )
        }
    }
}
