package com.example.model

enum class AppCategory(val title: String, val iconName: String) {
    ALL("All Applications", "Apps"),
    STREAMING("Streaming & Community", "LiveTv"),
    MEDIA_PLAYERS("Media Players", "PlayCircle"),
    IPTV_LIVE("IPTV & Live TV", "Tv"),
    AUDIO_MUSIC("Audio & Radio", "Headphones"),
    SPORTS("Live Sports & Events", "SportsSoccer"),
    TV_TOOLS("TV Utilities", "Build"),
    EMULATORS("Gaming & Frontends", "SportsEsports")
}

enum class CloudProvider(val displayName: String, val icon: String, val colorHex: Long) {
    GOOGLE_DRIVE("Google Drive", "Cloud", 0xFF4285F4),
    DROPBOX("Dropbox", "CloudQueue", 0xFF0061FE),
    WEBDAV("Nextcloud / WebDAV", "Dns", 0xFF0082C9),
    CUSTOM_REPO("Custom Cloud Repo", "Hub", 0xFF10B981)
}

data class CloudAccount(
    val id: String,
    val provider: CloudProvider,
    val name: String,
    val endpointOrFolder: String,
    val isConnected: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val appCount: Int = 0
)

data class SyncDevice(
    val id: String,
    val name: String,
    val deviceModel: String,
    val isCurrentDevice: Boolean = false,
    val lastSyncTime: String,
    val isOnline: Boolean = true
)

data class MediaApp(
    val id: String,
    val name: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val installedVersionName: String? = null,
    val installedVersionCode: Long? = null,
    val category: AppCategory,
    val description: String,
    val changelog: String,
    val downloadUrl: String,
    val iconUrl: String,
    val bannerUrl: String? = null,
    val fileSizeBytes: Long,
    val developer: String,
    val rating: Float = 4.8f,
    val minAndroidVersion: String = "Android 8.0+",
    val cloudSource: CloudProvider? = null,
    val cloudAccountName: String? = null,
    val isCloudSynced: Boolean = false,
    val permissions: List<String> = emptyList(),
    val isFeatured: Boolean = false
) {
    val isInstalled: Boolean
        get() = installedVersionName != null

    val hasUpdate: Boolean
        get() = isInstalled && (installedVersionCode != null && versionCode > installedVersionCode)

    val formattedSize: String
        get() {
            val mb = fileSizeBytes / (1024.0 * 1024.0)
            return String.format("%.1f MB", mb)
        }
}

data class AppUpdateNotification(
    val id: String,
    val appId: String,
    val appName: String,
    val packageName: String,
    val currentVersion: String,
    val newVersion: String,
    val changelog: String,
    val timestamp: Long = System.currentTimeMillis(),
    var isDismissed: Boolean = false
)

sealed class DownloadState {
    object Idle : DownloadState()
    data class Downloading(val progress: Float, val downloadedBytes: Long, val totalBytes: Long) : DownloadState()
    data class Installing(val message: String) : DownloadState()
    data class Completed(val message: String) : DownloadState()
    data class Error(val error: String) : DownloadState()
}
