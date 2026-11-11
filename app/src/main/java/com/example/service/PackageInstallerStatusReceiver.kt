package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class InstallResult {
    data class Success(val packageName: String, val appName: String) : InstallResult()
    data class PendingUserAction(val intent: Intent, val packageName: String) : InstallResult()
    data class Failure(val packageName: String, val message: String, val status: Int) : InstallResult()
}

class PackageInstallerStatusReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "PackageInstallerReceiver"
        const val ACTION_INSTALL_STATUS = "com.example.omnistoretv.INSTALL_STATUS"
        const val EXTRA_APP_ID = "extra_app_id"
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"

        private val _installEvents = MutableSharedFlow<InstallResult>(extraBufferCapacity = 10)
        val installEvents = _installEvents.asSharedFlow()
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_INSTALL_STATUS) return

        val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
        val message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE) ?: "No status message"
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME) ?: "unknown"
        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: packageName

        Log.d(TAG, "PackageInstaller callback for $packageName - status=$status ($message)")

        when (status) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                // The Android OS requires user action (for instance, initial install on device or permissions)
                val confirmIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_INTENT, Intent::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(Intent.EXTRA_INTENT)
                }

                if (confirmIntent != null) {
                    confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(confirmIntent)
                    _installEvents.tryEmit(InstallResult.PendingUserAction(confirmIntent, packageName))
                }
            }

            PackageInstaller.STATUS_SUCCESS -> {
                Log.i(TAG, "Silent/Sideload installation succeeded for $packageName")
                _installEvents.tryEmit(InstallResult.Success(packageName, appName))
                showTvNotification(context, appName, "Successfully installed and ready on your Android TV!")
            }

            else -> {
                val failureReason = when (status) {
                    PackageInstaller.STATUS_FAILURE_ABORTED -> "Installation aborted by system"
                    PackageInstaller.STATUS_FAILURE_BLOCKED -> "Installation blocked by TV device policy"
                    PackageInstaller.STATUS_FAILURE_CONFLICT -> "Package signature conflict with existing version"
                    PackageInstaller.STATUS_FAILURE_INCOMPATIBLE -> "APK is incompatible with this Android TV architecture"
                    PackageInstaller.STATUS_FAILURE_INVALID -> "Corrupt or invalid APK archive from cloud storage"
                    PackageInstaller.STATUS_FAILURE_STORAGE -> "Insufficient internal storage space on TV"
                    else -> "Installation failed: $message"
                }
                Log.e(TAG, "Install failed: $failureReason")
                _installEvents.tryEmit(InstallResult.Failure(packageName, failureReason, status))
                showTvNotification(context, appName, failureReason)
            }
        }
    }

    private fun showTvNotification(context: Context, appName: String, message: String) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            val channelId = "tv_sideload_channel"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "TV Sideload & Cloud Sync",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for Android TV PackageInstaller sideloading"
                }
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(appName)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(appName.hashCode(), notification)
        } catch (e: Exception) {
            Log.w(TAG, "Could not post TV notification: ${e.message}")
        }
    }
}
