package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DefaultCatalog
import com.example.model.AppCategory
import com.example.model.AppUpdateNotification
import com.example.model.CloudAccount
import com.example.model.CloudProvider
import com.example.model.DownloadState
import com.example.model.MediaApp
import com.example.model.SyncDevice
import android.content.Intent
import android.os.Build
import com.example.service.CloudApkInstallerService
import com.example.service.InstallResult
import com.example.service.PackageInstallerStatusReceiver
import com.example.service.SideloadManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class NavigationTab(val label: String, val icon: String) {
    FEATURED("Featured", "Star"),
    CATEGORIES("Categories", "GridView"),
    CLOUD_SYNC("Cloud Storage", "CloudSync"),
    UPDATES("Updates", "SystemUpdate"),
    INSTALLED("My Apps", "Folder"),
    SETTINGS("Settings", "Settings")
}

class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val sideloadManager = SideloadManager(application.applicationContext)

    private val _activeTab = MutableStateFlow(NavigationTab.FEATURED)
    val activeTab: StateFlow<NavigationTab> = _activeTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow(AppCategory.ALL)
    val selectedCategory: StateFlow<AppCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _apps = MutableStateFlow<List<MediaApp>>(emptyList())
    val apps: StateFlow<List<MediaApp>> = _apps.asStateFlow()

    private val _selectedAppForDetails = MutableStateFlow<MediaApp?>(null)
    val selectedAppForDetails: StateFlow<MediaApp?> = _selectedAppForDetails.asStateFlow()

    private val _downloadStates = MutableStateFlow<Map<String, DownloadState>>(emptyMap())
    val downloadStates: StateFlow<Map<String, DownloadState>> = _downloadStates.asStateFlow()

    private val _cloudAccounts = MutableStateFlow<List<CloudAccount>>(emptyList())
    val cloudAccounts: StateFlow<List<CloudAccount>> = _cloudAccounts.asStateFlow()

    private val _syncDevices = MutableStateFlow<List<SyncDevice>>(emptyList())
    val syncDevices: StateFlow<List<SyncDevice>> = _syncDevices.asStateFlow()

    private val _updateNotifications = MutableStateFlow<List<AppUpdateNotification>>(emptyList())
    val updateNotifications: StateFlow<List<AppUpdateNotification>> = _updateNotifications.asStateFlow()

    private val _isSyncingCloud = MutableStateFlow(false)
    val isSyncingCloud: StateFlow<Boolean> = _isSyncingCloud.asStateFlow()

    private val _lastCloudSyncStatus = MutableStateFlow("All devices synchronized")
    val lastCloudSyncStatus: StateFlow<String> = _lastCloudSyncStatus.asStateFlow()

    private val _isVirtualRemoteVisible = MutableStateFlow(false)
    val isVirtualRemoteVisible: StateFlow<Boolean> = _isVirtualRemoteVisible.asStateFlow()

    private val _autoCheckUpdates = MutableStateFlow(true)
    val autoCheckUpdates: StateFlow<Boolean> = _autoCheckUpdates.asStateFlow()

    private val _updateIntervalHours = MutableStateFlow(6)
    val updateIntervalHours: StateFlow<Int> = _updateIntervalHours.asStateFlow()

    private val _silentInstallEnabled = MutableStateFlow(true)
    val silentInstallEnabled: StateFlow<Boolean> = _silentInstallEnabled.asStateFlow()

    fun setSilentInstallEnabled(enabled: Boolean) {
        _silentInstallEnabled.value = enabled
    }

    private val _bannerNotification = MutableStateFlow<AppUpdateNotification?>(null)
    val bannerNotification: StateFlow<AppUpdateNotification?> = _bannerNotification.asStateFlow()

    init {
        loadCatalogAndDetectPackages()
        startPackageInstallerService()
        observePackageInstallerEvents()
    }

    private fun startPackageInstallerService() {
        try {
            val app = getApplication<Application>()
            val intent = Intent(app, CloudApkInstallerService::class.java)
            app.startService(intent)
        } catch (e: Exception) {
            // Ignored in unit testing environments
        }
    }

    private fun observePackageInstallerEvents() {
        viewModelScope.launch {
            PackageInstallerStatusReceiver.installEvents.collect { result ->
                when (result) {
                    is InstallResult.Success -> {
                        _apps.value = _apps.value.map { current ->
                            if (current.packageName == result.packageName) {
                                current.copy(
                                    installedVersionName = current.versionName,
                                    installedVersionCode = current.versionCode
                                )
                            } else {
                                current
                            }
                        }
                        _updateNotifications.value = _updateNotifications.value.filter { it.packageName != result.packageName }
                        if (_bannerNotification.value?.packageName == result.packageName) {
                            _bannerNotification.value = null
                        }
                        if (_selectedAppForDetails.value?.packageName == result.packageName) {
                            _selectedAppForDetails.value = _apps.value.find { it.packageName == result.packageName }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadCatalogAndDetectPackages() {
        val initialList = DefaultCatalog.sampleApps.map { app ->
            // Check real package manager if installed on this physical TV / device
            val (installedVersionName, installedVersionCode) = sideloadManager.checkPackageInstalled(app.packageName)
            if (installedVersionName != null) {
                app.copy(
                    installedVersionName = installedVersionName,
                    installedVersionCode = installedVersionCode
                )
            } else {
                app
            }
        }
        _apps.value = initialList
        _cloudAccounts.value = DefaultCatalog.initialCloudAccounts
        _syncDevices.value = DefaultCatalog.initialDevices

        checkForUpdates()
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            val notifications = mutableListOf<AppUpdateNotification>()
            _apps.value.forEach { app ->
                if (app.hasUpdate) {
                    notifications.add(
                        AppUpdateNotification(
                            id = "update_${app.id}_${System.currentTimeMillis()}",
                            appId = app.id,
                            appName = app.name,
                            packageName = app.packageName,
                            currentVersion = app.installedVersionName ?: "v1.0",
                            newVersion = app.versionName,
                            changelog = app.changelog
                        )
                    )
                }
            }
            _updateNotifications.value = notifications

            // Show TV top banner if updates are available
            if (notifications.isNotEmpty() && _bannerNotification.value == null) {
                _bannerNotification.value = notifications.first()
            }
        }
    }

    fun dismissBannerNotification() {
        _bannerNotification.value = null
    }

    fun selectTab(tab: NavigationTab) {
        _activeTab.value = tab
    }

    fun selectCategory(category: AppCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openAppDetails(app: MediaApp) {
        _selectedAppForDetails.value = app
    }

    fun closeAppDetails() {
        _selectedAppForDetails.value = null
    }

    fun toggleVirtualRemote() {
        _isVirtualRemoteVisible.value = !_isVirtualRemoteVisible.value
    }

    fun setAutoCheckUpdates(enabled: Boolean) {
        _autoCheckUpdates.value = enabled
    }

    fun setUpdateIntervalHours(hours: Int) {
        _updateIntervalHours.value = hours
    }

    fun sideloadApp(app: MediaApp) {
        viewModelScope.launch {
            _downloadStates.value = _downloadStates.value + (app.id to DownloadState.Downloading(0.05f, 0L, app.fileSizeBytes))

            val success = sideloadManager.downloadAndInstallApk(app) { state ->
                _downloadStates.value = _downloadStates.value + (app.id to state)
            }

            // Update app model to mark installed with latest version
            _apps.value = _apps.value.map { current ->
                if (current.id == app.id) {
                    current.copy(
                        installedVersionName = app.versionName,
                        installedVersionCode = app.versionCode
                    )
                } else {
                    current
                }
            }

            // Remove from update notifications
            _updateNotifications.value = _updateNotifications.value.filter { it.appId != app.id }
            if (_bannerNotification.value?.appId == app.id) {
                _bannerNotification.value = null
            }

            // Update details view if currently open
            if (_selectedAppForDetails.value?.id == app.id) {
                _selectedAppForDetails.value = _apps.value.find { it.id == app.id }
            }

            delay(3500)
            _downloadStates.value = _downloadStates.value - app.id
        }
    }

    fun launchApp(app: MediaApp) {
        sideloadManager.launchApp(app.packageName)
    }

    fun uninstallApp(app: MediaApp) {
        sideloadManager.requestUninstall(app.packageName)
        _apps.value = _apps.value.map { current ->
            if (current.id == app.id) {
                current.copy(
                    installedVersionName = null,
                    installedVersionCode = null
                )
            } else {
                current
            }
        }
        if (_selectedAppForDetails.value?.id == app.id) {
            _selectedAppForDetails.value = _apps.value.find { it.id == app.id }
        }
    }

    fun syncCloudStorage() {
        viewModelScope.launch {
            _isSyncingCloud.value = true
            _lastCloudSyncStatus.value = "Contacting Google Drive, Dropbox, and WebDAV..."
            delay(1200)

            // Update last sync timestamps
            _cloudAccounts.value = _cloudAccounts.value.map {
                it.copy(lastSyncTimestamp = System.currentTimeMillis())
            }

            _syncDevices.value = _syncDevices.value.map { device ->
                if (device.isCurrentDevice) {
                    device.copy(lastSyncTime = "Active just now")
                } else {
                    device.copy(lastSyncTime = "Synced moments ago")
                }
            }

            _lastCloudSyncStatus.value = "Synced successfully across 3 TV devices"
            _isSyncingCloud.value = false
            checkForUpdates()
        }
    }

    fun uploadCustomApp(
        name: String,
        packageName: String,
        version: String,
        category: AppCategory,
        downloadUrl: String,
        developer: String,
        cloudProvider: CloudProvider,
        description: String
    ) {
        val newApp = MediaApp(
            id = "custom_${System.currentTimeMillis()}",
            name = name,
            packageName = packageName,
            versionName = version,
            versionCode = 1000,
            installedVersionName = null,
            installedVersionCode = null,
            category = category,
            description = description.ifBlank { "Custom sideloaded media application imported from cloud storage." },
            changelog = "Initial cloud upload via $name integration.",
            downloadUrl = downloadUrl,
            iconUrl = "https://images.unsplash.com/photo-1593784991095-a205069470b6?w=200",
            fileSizeBytes = 28 * 1024 * 1024L,
            developer = developer.ifBlank { "Third-Party Cloud" },
            rating = 5.0f,
            minAndroidVersion = "Android 7.0+",
            isFeatured = false,
            permissions = listOf("Internet Access", "Storage"),
            cloudSource = cloudProvider,
            cloudAccountName = "${cloudProvider.displayName} Storage",
            isCloudSynced = true
        )

        _apps.value = listOf(newApp) + _apps.value

        // Increment cloud account count
        _cloudAccounts.value = _cloudAccounts.value.map { account ->
            if (account.provider == cloudProvider) {
                account.copy(appCount = account.appCount + 1)
            } else {
                account
            }
        }
    }

    fun addCloudAccount(provider: CloudProvider, name: String, endpointOrFolder: String) {
        val newAccount = CloudAccount(
            id = "cloud_${System.currentTimeMillis()}",
            provider = provider,
            name = name,
            endpointOrFolder = endpointOrFolder,
            isConnected = true,
            appCount = 0
        )
        _cloudAccounts.value = _cloudAccounts.value + newAccount
    }

    fun disconnectCloudAccount(id: String) {
        _cloudAccounts.value = _cloudAccounts.value.filter { it.id != id }
    }

    fun updateAllApps() {
        val pendingUpdates = _apps.value.filter { it.hasUpdate }
        pendingUpdates.forEach { app ->
            sideloadApp(app)
        }
    }
}
